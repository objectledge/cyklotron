package net.cyklotron.cms.locations.poland;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.UnsupportedCharactersInFilePathException;
import org.objectledge.utils.Timer;

import net.cyklotron.cms.files.util.CSVFileReader;

public class PNAProvider
{
    private static final String SOURCE_LOCATION = "http://www.poczta-polska.pl/spispna/spispna.pdf";

    public static final String CACHE_DIRECTORY = "/ngo/locations/";

    private static final String SOURCE_TMP_FILE = "spispna.pdf.tmp";

    public static final String SOURCE_FILE = "spispna.pdf";

    private static final String CACHE_TMP_FILE = "spispna.csv.tmp";

    public static final String CACHE_FILE = "spispna.csv";

    private static final String ENCODING = "UTF-8";

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
        Timer timer = new Timer();
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(SOURCE_LOCATION);
        try
        {
            client.executeMethod(method);
            if(method.getStatusCode() == HttpStatus.SC_OK)
            {
                if(!fileSystem.isDirectory(CACHE_DIRECTORY))
                {
                    fileSystem.mkdirs(CACHE_DIRECTORY);
                }
                fileSystem.write(CACHE_DIRECTORY + SOURCE_TMP_FILE,
                    method.getResponseBodyAsStream());
                rename(SOURCE_TMP_FILE, SOURCE_FILE);
                logger.info("downloaded " + fileSystem.length(CACHE_DIRECTORY + SOURCE_FILE)
                    + " bytes in " + timer.getElapsedSeconds() + "s");
                return true;
            }
            else
            {
                if(fileSystem.exists(CACHE_DIRECTORY + SOURCE_FILE))
                {
                    logger
                        .error("failed to download data from "
                            + SOURCE_LOCATION
                            + " HTTP status "
                            + method.getStatusCode()
                            + " but previously downloaded source file exists, proceeding with what we got.");
                    return true;
                }
                else
                {
                    logger.error("failed to download data from " + SOURCE_LOCATION
                        + " HTTP status " + method.getStatusCode());
                    return false;
                }
            }
        }
        catch(IOException e)
        {
            logger.error("failed to download source from " + SOURCE_LOCATION, e);
            return false;
        }
        finally
        {
            method.releaseConnection();
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
        throws IOException
    {
        if(fileSystem.exists(CACHE_DIRECTORY + SOURCE_FILE))
        {
            try
            {
                PNASourceParser parser = new PNASourceParser(fileSystem, logger);
                parser.parse(CACHE_DIRECTORY + SOURCE_FILE);
                final List<String[]> content = parser.getContent();
                writeCache(parser.getHeadings(), content);
                return content;
            }
            catch(IOException e)
            {
                throw new IOException("failed to parse source file " + SOURCE_FILE, e);
            }
        }
        else if(fileSystem.exists(CACHE_DIRECTORY + CACHE_FILE))
        {
            logger.error("PDF source file not available, proceeding with cached CSV file");
            try
            {
                return parseCache();
            }
            catch(Exception e)
            {
                throw new IOException("failed to parse cached CSV file " + SOURCE_FILE, e);
            }
        }
        else
        {
            throw new IOException("neiter source PDF file nor cached CSV file is available");
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

    public List<String[]> parseCache()
        throws IOException
    {
        try(InputStream is = fileSystem.getInputStream(CACHE_DIRECTORY + CACHE_FILE))
        {
            Timer timer = new Timer();
            CSVFileReader csvReader = new CSVFileReader(is, ENCODING, ';');
            Map<String, String> line;
            List<String[]> locations = new ArrayList<>();
            while((line = csvReader.getNextLine()) != null)
            {
                String[] location = new String[7];
                location[0] = line.get("PNA");
                location[1] = line.get("Miejscowość");
                location[2] = line.get("Ulica");
                location[3] = line.get("Numery");
                location[4] = line.get("Gmina");
                location[5] = line.get("Powiat");
                location[6] = line.get("Województwo");
                locations.add(location);
            }
            logger.info("loaded " + locations.size() + " items from cache in "
                + timer.getElapsedSeconds() + "s");
            return locations;
        }
    }
}
