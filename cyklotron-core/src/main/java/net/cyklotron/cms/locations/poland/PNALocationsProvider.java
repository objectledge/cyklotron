package net.cyklotron.cms.locations.poland;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.UnsupportedCharactersInFilePathException;
import org.objectledge.utils.Timer;

import net.cyklotron.cms.files.util.CSVFileReader;
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
    /**
     * The fields defined for location identification for Poland.
     * <ul>
     * <li>postCode: PNA (kod pocztowy)</li>
     * <li>street: nazwa ulicy (placu itp.)</li>
     * <li>city: miejscowość</li>
     * <li>commune: gmina</li>
     * <li>district: powiat</li>
     * <li>province: województwo</li>
     * </ul>
     */
    public static final String[] FIELDS = { "postCode", "street", "city", "commune", "district",
                    "province" };

    private static final String ENCODING = "UTF-8";

    private static final String CACHE_DIRECTORY = "/ngo/locations/";

    private static final String SOURCE_FILE = "spispna.pdf";

    private static final String CACHE_TMP_FILE = "spispna.csv.tmp";

    private static final String CACHE_FILE = "spispna.csv";

    private final Logger logger;

    private final FileSystem fileSystem;

    private final PNAProvider pnaProvider;

    private List<Location> cachedLocations = null;

    public PNALocationsProvider(Logger logger, FileSystem fileSystem)
    {
        this.logger = logger;
        this.fileSystem = fileSystem;
        pnaProvider = new PNAProvider(fileSystem, logger);
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
            cachedLocations = new ArrayList<Location>(content.size());
            for(String[] row : content)
            {
                Map<String, String> fieldValues = new HashMap<>(4);
                fieldValues.put("province", row[6]);
                fieldValues.put("district", row[5]);
                fieldValues.put("commune", row[4]);
                fieldValues.put("city", stripCityName(row[1]));
                fieldValues.put("street", row[2] != null ? row[2] : "");
                fieldValues.put("postCode", row[0]);
                cachedLocations.add(new Location(FIELDS, fieldValues));
            }
        }
        catch(IOException e)
        {
            logger.error("failed to parse source file " + SOURCE_FILE, e);
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
        InputStream is = fileSystem.getInputStream(CACHE_DIRECTORY + CACHE_FILE);
        try
        {
            Timer timer = new Timer();
            CSVFileReader csvReader = new CSVFileReader(is, ENCODING, ';');
            Map<String, String> line;
            cachedLocations = new ArrayList<Location>();
            while((line = csvReader.getNextLine()) != null)
            {
                Map<String, String> fieldValues = new HashMap<>(4);
                fieldValues.put("province", line.get("Województwo"));
                fieldValues.put("district", line.get("Powiat"));
                fieldValues.put("commune", line.get("Gmina"));
                fieldValues.put("city", line.get("Miejscowość"));
                fieldValues.put("street", line.get("Ulica"));
                fieldValues.put("postCode", line.get("PNA"));
                cachedLocations.add(new Location(FIELDS, fieldValues));
            }
            logger.info("loaded " + cachedLocations.size() + " items from cache in "
                + timer.getElapsedSeconds() + "s");
        }
        catch(IOException e)
        {
            logger.error("failed to parse cache file " + CACHE_FILE, e);
        }
        finally
        {
            try
            {
                is.close();
            }
            catch(IOException e)
            {
                logger.error("i/o error", e);
            }
        }
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
        if(pnaProvider.downloadSource())
        {
            parseSource();
        }
        return cachedLocations;
    }

    @Override
    public String[] getFields()
    {
        return FIELDS;
    }

    @Override
    public Set<FieldOptions> getOptions(String field)
    {
        switch(field)
        {
        case "postCode":
            return EnumSet.of(FieldOptions.NOT_ANALYZED);
        case "street":
            return EnumSet.of(FieldOptions.MULTI_TERM_SUBQUERY);
        default:
            return EnumSet.noneOf(FieldOptions.class);
        }
    }
}