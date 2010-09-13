package net.cyklotron.cms.ngodatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.UnsupportedCharactersInFilePathException;
import org.objectledge.utils.Timer;

/**
 * LocationProvider implementation for Poland using Pocztowe Numery Adresowe (postal area codes)
 * published by Poczta Polska SA.
 * 
 * @author rafal
 */
public class PNALocationsProvider
    implements LocationsProvider
{
    private static final String SOURCE_LOCATION = "http://www.poczta-polska.pl/spispna/spispna.pdf";

    private static final String CACHE_DIRECTORY = "/ngo/locations/";

    private static final String CACHE_FILE = "spispna.pdf";

    private static final String CACHE_TMP_FILE = "spispna.pdf.tmp";

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
     * {@link #CACHE_TMP_FILE}.
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
            fileSystem.write(CACHE_DIRECTORY + CACHE_TMP_FILE, method.getResponseBodyAsStream());
            method.releaseConnection();
            logger.info("downloaded " + fileSystem.length(CACHE_DIRECTORY + CACHE_TMP_FILE)
                + " bytes in " + timer.getElapsedSeconds() + "s");
            return true;
        }
        catch(IOException e)
        {
            logger.error("failed to download source from " + SOURCE_LOCATION, e);
            return false;
        }
        catch(UnsupportedCharactersInFilePathException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parse source file. Source file is parsed and on success {@link #cachedContent} variable is
     * updated.
     * 
     * @param fileName can be either {@link #CACHE_TMP_FILE} or {@link #CACHE_FILE}.
     * @return true if parsing was successful.
     */
    private boolean parseSource(String fileName)
    {
        try
        {
            PNASourceParser parser = new PNASourceParser(fileSystem, logger);
            parser.parse(CACHE_DIRECTORY + fileName);
            cachedLocations = instantiate(parser.getContent());
            return true;
        }
        catch(IOException e)
        {
            logger.error("failed to parse source file " + fileName, e);
            return false;
        }
    }

    /**
     * Stores temporary cache file for future use.
     */
    private void cacheSourceFile()
    {
        try
        {
            fileSystem.rename(CACHE_DIRECTORY + CACHE_TMP_FILE, CACHE_DIRECTORY + CACHE_FILE);
        }
        catch(IOException e)
        {
            logger.error("failed to rename " + CACHE_TMP_FILE + " to " + CACHE_FILE, e);
        }
        catch(UnsupportedCharactersInFilePathException e)
        {
            throw new RuntimeException(e);
        }
    }

    private List<Location> instantiate(List<String[]> content)
    {
        List<Location> locations = new ArrayList<Location>(content.size());
        for(String[] row : content)
        {
            locations.add(new Location(row[6], row[1], row[2], row[0]));
        }
        return locations;
    }

    @Override
    public Collection<Location> fromCache()
    {
        if(cachedLocations == null)
        {
            if(fileSystem.exists(CACHE_DIRECTORY + CACHE_FILE))
            {
                parseSource(CACHE_FILE);
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
            if(parseSource(CACHE_TMP_FILE))
            {
                cacheSourceFile();
            }
        }
        return cachedLocations;
    }
}
