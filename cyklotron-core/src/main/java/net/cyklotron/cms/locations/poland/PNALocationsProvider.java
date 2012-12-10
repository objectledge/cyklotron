package net.cyklotron.cms.locations.poland;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jcontainer.dna.Logger;
import org.objectledge.database.Database;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.UnsupportedCharactersInFilePathException;
import org.objectledge.utils.Timer;

import net.cyklotron.cms.locations.Location;
import net.cyklotron.cms.locations.LocationsProvider;

/**
 * LocationProvider implementation for Poland using Pocztowe Numery Adresowe (postal area codes)
 * published by Poczta Polska SA.
 * 
 * @author rafal
 */
public class PNALocationsProvider
    implements LocationsProvider
{
    private static final String ENCODING = "UTF-8";

    private static final String SOURCE_LOCATION = "http://www.poczta-polska.pl/spispna/spispna.pdf";

    private static final String CACHE_DIRECTORY = "/ngo/locations/";

    private static final String SOURCE_TMP_FILE = "spispna.pdf.tmp";

    private static final String SOURCE_FILE = "spispna.pdf";

    private static final String CACHE_TMP_FILE = "spispna.csv.tmp";

    private static final String CACHE_FILE = "spispna.csv";

    private final Logger logger;

    private final Database database;

    private final FileSystem fileSystem;

    private List<Location> cachedLocations = null;

    public PNALocationsProvider(Logger logger, FileSystem fileSystem, Database database)
    {
        this.logger = logger;
        this.fileSystem = fileSystem;
        this.database = database;
    }

    /**
     * Download source file. Data is downloaded from {@link #SOURCE_LOCATION} and written to
     * {@link #SOURCE_TMP_FILE}.
     * 
     * @return true if download was successful.
     */
    private boolean downloadSource()
    {
        try
        {
            Timer timer = new Timer();
            HttpClient client = new HttpClient();
            HttpMethod method = new GetMethod(SOURCE_LOCATION);
            client.executeMethod(method);
            if(!fileSystem.isDirectory(CACHE_DIRECTORY))
            {
                fileSystem.mkdirs(CACHE_DIRECTORY);
            }
            fileSystem.write(CACHE_DIRECTORY + SOURCE_TMP_FILE, method.getResponseBodyAsStream());
            method.releaseConnection();
            rename(SOURCE_TMP_FILE, SOURCE_FILE);
            logger.info("downloaded " + fileSystem.length(CACHE_DIRECTORY + SOURCE_FILE)
                + " bytes in " + timer.getElapsedSeconds() + "s");
            return true;
        }
        catch(IOException e)
        {
            logger.error("failed to download source from " + SOURCE_LOCATION, e);
            return false;
        }
    }

    /**
     * Parse source file. Source file is parsed and on success {@link #cachedContent} variable is
     * updated.
     * 
     * @return true if parsing was successful.
     */
    private void parseSource()
    {
        try
        {
            PNASourceParser parser = new PNASourceParser(fileSystem, logger);
            parser.parse(CACHE_DIRECTORY + SOURCE_FILE);
            List<String[]> content = parser.getContent();
            writeCache(parser.getHeadings(), content);
            writeDB(content);
            readDB();
        }
        catch(IOException e)
        {
            logger.error("failed to parse source file " + SOURCE_FILE, e);
        }
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
                    .prepareStatement("INSERT INTO locations_pna(pna, miejscowość, ulica, "
                        + "numery, gmina, powiat, województwo, nazwa, nazwa_pod, nazwa_rm) "
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
                            pstmt.setString(9, null);
                            pstmt.setString(10, area);
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

    private void readDB()
    {
        try(Connection conn = database.getConnection(); Statement stmt = conn.createStatement())
        {
            Timer timer = new Timer();
            try(ResultSet rs = stmt.executeQuery("SELECT * FROM locations_vpna"))
            {
                cachedLocations = new ArrayList<Location>();
                while(rs.next())
                {
                    String terc = rs.getString("woj") + rs.getString("pow") + rs.getString("gmi")
                        + rs.getString("rodz_gmi");
                    String area = rs.getString("miejscowość") == rs.getString("nazwa") ? rs
                        .getString("nazwa_rm") != null ? rs.getString("nazwa_rm") : "" : rs
                        .getString("nazwa");

                    cachedLocations.add(new Location(rs.getString("województwo"), rs
                        .getString("powiat"), rs.getString("gmina"), rs.getString("miejscowość"),
                        area, rs.getString("ulica") != null ? rs.getString("ulica") : "", rs
                            .getString("pna"), terc, rs.getString("sym") != null ? rs
                            .getString("sym") : ""));
                }
                logger.info("READ " + cachedLocations.size() + " items from locations_bpna DB in "
                    + timer.getElapsedSeconds() + "s");
            }
        }
        catch(SQLException e)
        {
            logger.error("error on wroting items to DB in ", e);
            throw new RuntimeException(e);
        }
    }

    private void writeCache(String[] headings, List<String[]> content)
    {
        try
        {
            Timer timer = new Timer();
            Writer writer = fileSystem.getWriter(CACHE_DIRECTORY + CACHE_TMP_FILE, ENCODING);
            PNASourceParser.dump(Collections.singletonList(headings), writer);
            PNASourceParser.dump(content, writer);
            writer.close();
            rename(CACHE_TMP_FILE, CACHE_FILE);
            logger.info("wrote " + content.size() + " items to cache in "
                + timer.getElapsedSeconds() + "s");
        }
        catch(UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
        catch(IOException e)
        {
            logger.error("failed to write cache file " + CACHE_FILE, e);
        }
    }

    private void parseCache()
    {
        readDB();
    }

    /**
     * Stores temporary cache file for future use.
     * 
     * @throws IOException
     */
    private void rename(String from, String to)
        throws IOException
    {
        try
        {
            fileSystem.rename(CACHE_DIRECTORY + from, CACHE_DIRECTORY + to);
        }
        catch(UnsupportedCharactersInFilePathException e)
        {
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
            if(fileSystem.exists(CACHE_DIRECTORY + CACHE_FILE))
            {
                parseCache();
            }
            else
            {
                fromSource();
            }
        }
        return cachedLocations;
    }

    @Override
    public Collection<Location> fromSource()
    {
        if(downloadSource())
        {
            parseSource();
        }
        return cachedLocations;
    }
}
