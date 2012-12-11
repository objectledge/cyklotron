package net.cyklotron.cms.locations.poland;

import java.io.IOException;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.UnsupportedCharactersInFilePathException;
import org.objectledge.utils.Timer;

public class PNAProvider
{
    private static final String SOURCE_LOCATION = "http://www.poczta-polska.pl/spispna/spispna.pdf";

    private static final String CACHE_DIRECTORY = "/ngo/locations/";

    private static final String SOURCE_TMP_FILE = "spispna.pdf.tmp";

    private static final String SOURCE_FILE = "spispna.pdf";

    private final FileSystem fileSystem;

    private final Logger logger;

    public PNAProvider(FileSystem fileSystem, Logger logger)
    {
        this.fileSystem = fileSystem;
        this.logger = logger;
    }

    /**
     * Download source file. Data is downloaded from {@link #SOURCE_LOCATION} and written to
     * {@link #SOURCE_TMP_FILE}.
     * 
     * @return true if download was successful.
     */
    public boolean downloadSource()
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
     * Parse source file. Source file is parsed and on success {@link #cachedContent} variable is
     * updated.
     * 
     * @return true if parsing was successful.
     */
    public List<String[]> parseSource()
    {
        try
        {
            PNASourceParser parser = new PNASourceParser(fileSystem, logger);
            parser.parse(CACHE_DIRECTORY + SOURCE_FILE);
            return parser.getContent();
        }
        catch(IOException e)
        {
            logger.error("failed to parse source file " + SOURCE_FILE, e);
            return null;
        }
    }
}
