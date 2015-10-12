package net.cyklotron.cms.locations.poland;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.jcontainer.dna.Logger;
import org.objectledge.database.Database;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.html.HTMLService;
import org.objectledge.utils.Timer;

import net.cyklotron.cms.locations.Location;

/**
 * LocationProvider implementation for Poland using Pocztowe Numery Adresowe (postal area codes)
 * published by Poczta Polska SA and administrative portioning of the state TERYT published by GUS.
 * <p>
 * The implmentation requires access to PostgreSQL database with tables and views defined in
 * {@code src/main/resources/sql/locations/LocationsDataTables.sql}
 * </p>
 * 
 * @author rafal.krzewski@caltha.pl
 * @author lukasz.urbanski@caltha.pl
 */
public class PNATERYTLocationsProvider
    implements net.cyklotron.cms.locations.LocationsProvider
{
    /**
     * The fields defined for location identification for Poland.
     * <ul>
     * <li>postCode: PNA (kod pocztowy)</li>
     * <li>sym: identyfikator miejscowości z rejestru SIMC GUS</li>
     * <li>terc: identyfikator gminy z rejestru TERC GUS - złożenie pól WOJ, POW, GMI, RODZ</li>
     * <li>street: nazwa ulicy (placu itp.)</li>
     * <li>area: dzielnica/część miejscowości</li>
     * <li>city: miejscowość</li>
     * <li>commune: gmina</li>
     * <li>district: powiat</li>
     * <li>province: województwo</li>
     * </ul>
     */
    public static final String[] FIELDS = { "postCode", "sym", "terc", "street", "area", "city",
                    "commune", "district", "province", "areaName", "areaType", "areaLevel",
                    "areaRank" };

    private final Logger logger;

    private final Database database;

    private volatile List<Location> cachedLocations = null;

    private final PNAProvider pnaProvider;

    private final TERCProvider tercProvider;

    private final FileSystem fileSystem;

    private final Map<String, Set<FieldOptions>> options = new HashMap<>();

    public PNATERYTLocationsProvider(Logger logger, FileSystem fileSystem, HTMLService htmlService,
        Database database)
    {
        this.logger = logger;
        this.fileSystem = fileSystem;
        this.database = database;
        pnaProvider = new PNAProvider(fileSystem, logger);
        tercProvider = new TERCProvider(logger, fileSystem, htmlService, database);

        options.put("postCode", EnumSet.of(FieldOptions.NOT_ANALYZED));
        options.put("areaType", EnumSet.of(FieldOptions.NOT_ANALYZED));
        options.put("areaLevel", EnumSet.of(FieldOptions.INTEGER));
        options.put("street", EnumSet.noneOf(FieldOptions.class));
    }

    private void writeDB(List<String[]> content)
    {
        try(Connection conn = database.getConnection())
        {
            Timer timer = new Timer();
            conn.setAutoCommit(false);
            try
            {
                try(PreparedStatement pstmt = conn.prepareStatement("DELETE FROM locations_pna"))
                {
                    pstmt.execute();
                }

                int[] inserted;
                try(PreparedStatement pstmt = conn
                    .prepareStatement("INSERT INTO locations_pna(pna, \"miejscowość\", ulica, "
                        + "numery, gmina, powiat, \"województwo\", nazwa, nazwa_pod, nazwa_rm) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"))
                {
                    for(String[] row : content)
                    {
                        String city = stripCityName(row[1]);

                        pstmt.setString(1, row[0]);
                        pstmt.setString(2, city);
                        pstmt.setString(3, row[2]);
                        pstmt.setString(4, row[3]);
                        pstmt.setString(5, row[4]);
                        pstmt.setString(6, row[5]);
                        pstmt.setString(7, row[6]);

                        // fill extra fields form matching with TERYT data.
                        String area = stripAreaName(row[1]);
                        if(area == null)
                        {
                            pstmt.setString(8, city);
                            pstmt.setString(9, city);
                            pstmt.setString(10, null);
                        }
                        else if(area.matches("^[A-ZĆŁÓŃŚŹŻ].+$"))
                        {
                            pstmt.setString(8, area);
                            pstmt.setString(9, city);
                            pstmt.setString(10, null);
                        }
                        else if(area.matches("^[a-z].+$"))
                        {
                            pstmt.setString(8, city);
                            pstmt.setString(9, city);
                            pstmt.setString(10, area);
                        }
                        // workaround for apparent error in PNA 2013-01
                        if(city.equals("Wałbrzych") && row[5].equals("wałbrzyski"))
                        {
                            pstmt.setString(6, "Wałbrzych");
                        }
                        pstmt.addBatch();
                    }
                    inserted = pstmt.executeBatch();
                }
                conn.commit();
                logger.info("INSERT " + inserted.length + " with " + content.size()
                    + " items to locations_pna DB in " + timer.getElapsedSeconds() + "s");
            }
            catch(SQLException e)
            {
                logger.error("failed to write data to database", e);
                try
                {
                    conn.rollback();
                }
                catch(SQLException ex)
                {
                    logger.error("rollback failed", ex);
                    e.addSuppressed(ex);
                }
            }
        }
        catch(SQLException e)
        {
            logger.error("failed to acquire or close connection", e);
        }
    }

    private List<Location> readDB()
    {
        try(Connection conn = database.getConnection(); Statement stmt = conn.createStatement())
        {
            Timer timer = new Timer();
            List<Location> locations = new ArrayList<>();
            try(ResultSet rs = stmt.executeQuery("SELECT * FROM locations_vpna"))
            {
                int i = 0;
                while(rs.next())
                {
                    String terc = rs.getString("woj") != null ? (rs.getString("woj")
                        + rs.getString("pow") + rs.getString("gmi").trim() + rs
                        .getString("rodz_gmi")) : "";
                    String area = rs.getString("miejscowość") == rs.getString("nazwa") ? rs
                        .getString("nazwa_rm") != null ? rs.getString("nazwa_rm") : "" : rs
                        .getString("nazwa");
                    String street = rs.getString("ulica") != null ? rs.getString("ulica") : "";
                    String sym = rs.getString("sym") != null ? rs.getString("sym") : "";

                    // create a new map, Location objects store the reference internally
                    Map<String, String> fieldValues = new HashMap<>();
                    fieldValues.put("province", rs.getString("województwo"));
                    fieldValues.put("district", rs.getString("powiat"));
                    fieldValues.put("commune", rs.getString("gmina"));
                    fieldValues.put("city", rs.getString("miejscowość"));
                    fieldValues.put("area", area);
                    fieldValues.put("street", street);
                    fieldValues.put("terc", terc);
                    fieldValues.put("sym", sym);
                    fieldValues.put("postCode", rs.getString("pna"));
                    fieldValues.put("areaType", "okręg pocztowy");
                    locations.add(new Location(FIELDS, fieldValues));
                    i++;
                }
                logger.info("READ " + i + " postal regions from locations_vpna view in "
                    + timer.getElapsedSeconds() + "s");
            }
            try(ResultSet rs = stmt.executeQuery("SELECT * FROM locations_varea"))
            {
                int i = 0;
                while(rs.next())
                {
                    Map<String, String> fieldValues = new HashMap<>();
                    fieldValues.put("areaName", rs.getString("nazwa"));
                    fieldValues.put("province", rs.getString("województwo"));
                    fieldValues.put("district", rs.getString("powiat"));
                    fieldValues.put("commune", rs.getString("gmina"));
                    fieldValues.put("city", rs.getString("miejscowość"));
                    final String terc = rs.getString("terc");
                    final String sym = rs.getString("sym");
                    final int level = terc.length() + (sym != null ? 1 : 0);
                    fieldValues.put("terc", terc);
                    fieldValues.put("sym", sym);
                    fieldValues.put("areaType", rs.getString("typ"));
                    fieldValues.put("areaRank", rs.getString("rm"));
                    fieldValues.put("areaLevel", Integer.toString(level));
                    locations.add(new Location(FIELDS, fieldValues));
                    i++;
                }
                logger.info("READ " + i + " administrative aras from locations_varea view in "
                    + timer.getElapsedSeconds() + "s");
            }
            return locations;
        }
        catch(SQLException e)
        {
            logger.error("error reading data from database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Strip city name from extra information
     */
    private String stripCityName(String city)
    {
        return city != null ? city.replaceFirst("\\s[(].+[)]", "") : null;
    }

    /**
     * Strip area name from extra information
     */
    private String stripAreaName(String city)
    {
        return city != null && city.matches("^.+\\s[(].*[)]$") ? city.replaceFirst(".+\\s[(]", "")
            .replaceFirst("[)].*", "") : null;
    }

    @Override
    public Collection<Location> fromCache()
    {
        if(cachedLocations == null)
        {
            cachedLocations = readDB();
            if(cachedLocations.size() == 0)
            {
                fromSource();
            }
        }
        return cachedLocations;
    }

    @Override
    public Collection<Location> fromSource()
    {
        tercProvider.fetch();
        try
        {
            List<String[]> locations = null;
            if(pnaProvider.downloadSource())
            {
                locations = pnaProvider.parseSource();
            }
            if(locations != null)
            {
                writeDB(locations);
            }
            cachedLocations = readDB();
        }
        catch(IOException e)
        {
            logger.error("failed to parse source data", e);
        }
        return cachedLocations;
    }

    /**
     * {@see #FIELDS}
     */
    @Override
    public String[] getFields()
    {
        return FIELDS;
    }

    @Override
    public Set<FieldOptions> getOptions(String field)
    {
        return options.containsKey(field) ? options.get(field) : EnumSet.noneOf(FieldOptions.class);
    }

    public Term getFineGrainedLocationMarker()
    {
        return new Term("areaType", "okręg pocztowy");
    }

    public Sort getCoarseGrainedLocationSort()
    {
        return new Sort(new SortField[] { SortField.FIELD_SCORE,
                        new SortField("areaLevel", SortField.Type.INT),
                        new SortField("terc", SortField.Type.STRING) });
    }

    public void merge(String field, String value1, String value2, Map<String, String> merged)
    {
        if(field.equals("terc"))
        {
            String cs = commonSubstring(value1, value2);
            mergedAreaDesc(cs, merged);
        }
        else if(value2.equals(value1))
        {
            merged.put(field, value1);
        }
    }

    private String commonSubstring(String s1, String s2)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < s1.length() && i < s2.length() && s1.charAt(i) == s2.charAt(i); i++)
        {
            sb.append(s1.charAt(i));
        }
        return sb.toString();
    }

    private void mergedAreaDesc(String mergedValue, Map<String, String> entries)
    {
        if(mergedValue.length() == 7)
        {
            entries.put("areaLevel", "7");
            entries.put("areaType", "gmina");
            entries.put("terc", mergedValue);
        }
        else if(mergedValue.length() >= 4)
        {
            entries.put("areaLevel", "4");
            entries.put("areaType", "powiat");
            entries.put("terc", mergedValue.substring(0, 4));
        }
        else if(mergedValue.length() >= 2)
        {
            entries.put("areaLevel", "2");
            entries.put("areaType", "województwo");
            entries.put("terc", mergedValue.substring(0, 2));
        }
    }
}
