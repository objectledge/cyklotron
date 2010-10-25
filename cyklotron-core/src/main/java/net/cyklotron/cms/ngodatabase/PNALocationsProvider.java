package net.cyklotron.cms.ngodatabase;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.UnsupportedCharactersInFilePathException;
import org.objectledge.utils.Timer;

import net.cyklotron.cms.files.util.CSVFileReader;

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

    private final FileSystem fileSystem;

    private List<Location> cachedLocations = null;

    public PNALocationsProvider(Logger logger, FileSystem fileSystem)
    {
        this.logger = logger;
        this.fileSystem = fileSystem;
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
            cachedLocations = new ArrayList<Location>(content.size());
            for(String[] row : content)
            {
                cachedLocations.add(new Location(row[6], stripCityName(row[1]),
                    row[2] != null ? row[2] : "", row[0]));
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
            logger.info("wrote " + content.size() + " items to cache in " + timer.getElapsedSeconds() + "s");
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
        InputStream is = fileSystem.getInputStream(CACHE_DIRECTORY
            + CACHE_FILE);
        try
        {
            Timer timer = new Timer();
            CSVFileReader csvReader = new CSVFileReader(is, ENCODING, ';');
            Map<String, String> line;
            cachedLocations = new ArrayList<Location>();
            while((line = csvReader.getNextLine()) != null)
            {
                cachedLocations.add(new Location(line.get("Województwo"), stripCityName(line.get("Miejscowość")), line
                    .get("Ulica"), line.get("PNA")));
            }
            logger.info("loaded " + cachedLocations.size() + " items from cache in " + timer.getElapsedSeconds() + "s");
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
        return city!= null ? city.replaceFirst("\\s[(].+[)]","") : null;
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
