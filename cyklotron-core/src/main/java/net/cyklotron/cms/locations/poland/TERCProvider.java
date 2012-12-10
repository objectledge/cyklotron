package net.cyklotron.cms.locations.poland;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jcontainer.dna.Logger;
import org.objectledge.database.Database;
import org.objectledge.database.DatabaseUtils;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.html.HTMLException;
import org.objectledge.html.HTMLService;
import org.objectledge.utils.Timer;

/**
 * LocationProvider implementation for Poland using Główny Urząd Statystyczny (terc area codes)
 * published by GUS.
 */
public class TERCProvider
{
    private static final String CACHE_DIRECTORY = "/ngo/locations/";

    private static final String SOURCE_LOCATION = "http://www.stat.gov.pl/broker/access/prefile/listPreFiles.jspa";

    private static String WEB_SOURCE_XPATH_LOCATION = "//TABLE[@id='row']//TD[text()='%s']/../TD/A[contains(@href,'downloadPreFile.jspa')]";

    private static final String[] DATA_NAMES = { "terc", "simc", "wmrodz" };

    private final Database database;

    private final Logger logger;

    private final FileSystem fileSystem;

    private final HTMLService htmlService;

    public TERCProvider(Logger logger, FileSystem fileSystem, HTMLService htmlService,
        Database database)
    {
        this.logger = logger;
        this.fileSystem = fileSystem;
        this.htmlService = htmlService;
        this.database = database;
    }

    private Map<String, String> fetchSourceUrls()
    {
        Map<String, String> sourceUrls = new HashMap<>();
        try
        {
            Timer timer = new Timer();

            HttpClient client = new HttpClient();
            HttpMethod method = new GetMethod(SOURCE_LOCATION);
            client.executeMethod(method);
            Document doc = htmlService.textToDom4j(method.getResponseBodyAsString());
            URI baseURI = new URI(SOURCE_LOCATION);
            for(String name : DATA_NAMES)
            {
                String xpathLocation = WEB_SOURCE_XPATH_LOCATION.replace("%s", name.toUpperCase());
                Element element = (Element)doc.selectSingleNode(xpathLocation);
                sourceUrls.put(name, baseURI.resolve(element.attribute("href").getValue())
                    .toString());
            }
            logger.info("fetched downlod source urls from " + SOURCE_LOCATION + " in "
                + timer.getElapsedSeconds() + "s");
        }
        catch(IOException | HTMLException | URISyntaxException e)
        {
            logger.error("failed to download web source from " + SOURCE_LOCATION, e);

        }
        return sourceUrls;
    }

    /**
     * Download source file. Data is downloaded from {@link #SOURCE_LOCATION} and written to
     * {@link #SOURCE_TMP_FILE}.
     * 
     * @return true if download was successful.
     */
    private boolean downloadSource()
    {
        Map<String, String> sourceUrls = fetchSourceUrls();
        HttpClient client = new HttpClient();
        try
        {
            if(!fileSystem.isDirectory(CACHE_DIRECTORY))
            {
                fileSystem.mkdirs(CACHE_DIRECTORY);
            }
        }
        catch(IOException e)
        {
            logger.error("failed to create cache directory");
        }
        for(Map.Entry<String, String> entry : sourceUrls.entrySet())
        {
            final String unpackName = CACHE_DIRECTORY + entry.getKey().toUpperCase() + ".xml";
            final String targetName = CACHE_DIRECTORY + entry.getKey() + ".xml";
            final String sourceUrl = entry.getValue();
            try
            {
                Timer timer = new Timer();
                HttpMethod method = new GetMethod(sourceUrl);
                client.executeMethod(method);
                if(fileSystem.exists(unpackName))
                {
                    fileSystem.delete(unpackName);
                }
                fileSystem.unpackZipFile(method.getResponseBodyAsStream(), CACHE_DIRECTORY);
                method.releaseConnection();
                if(fileSystem.isFile(unpackName))
                {
                    fileSystem.rename(unpackName, targetName);
                    logger.info("downloaded and unpacked " + fileSystem.length(targetName)
                        + " bytes of " + entry.getKey() + " archive in " + timer.getElapsedMillis()
                        + "ms");
                }
                else
                {
                    logger.error("archive downloaded " + sourceUrl
                        + " does not contain expected file " + unpackName);
                    return false;
                }
            }
            catch(IOException e)
            {
                logger.error("failed to download or unpack archive from " + sourceUrl, e);
                return false;
            }
        }
        return true;
    }

    private void parseSource()
    {
        TERCSourceParser parser = new TERCSourceParser(fileSystem, logger);

        try(Connection conn = database.getConnection())
        {
            conn.setAutoCommit(false);
            try
            {
                for(String name : DATA_NAMES)
                {
                    Timer timer = new Timer();
                    List<String[]> content = parser.parse(CACHE_DIRECTORY + name + ".xml");
                    String[] headings = parser.getHeadings();
                    logger.info("parsed " + content.size() + " " + name + " items in "
                        + timer.getElapsedMillis() + "ms");
                    writeDb(conn, content, headings, "locations_" + name);
                }
                conn.commit();
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

    private void writeDb(Connection conn, List<String[]> content, String[] headings,
        String tableName)
        throws SQLException
    {
        Timer timer = new Timer();
        PreparedStatement pstmt = null;
        int[] inserted = new int[0];
        try
        {
            String tableHeadings = Arrays.asList(headings).toString().replaceAll("^\\[|\\]$", "");
            String tableFields = tableHeadings.replaceAll("[^,]+", " ?");

            pstmt = conn.prepareStatement("DELETE FROM " + tableName);
            pstmt.execute();
            pstmt = conn.prepareStatement("INSERT INTO " + tableName + "(" + tableHeadings
                + ") VALUES(" + tableFields + ")");
            for(String[] lines : content)
            {
                for(int i = 0; i < headings.length - 1; i++)
                {
                    pstmt.setString(i + 1, lines[i]);
                }
                pstmt.setDate(headings.length, Date.valueOf(lines[headings.length - 1]));
                pstmt.addBatch();
            }
            inserted = pstmt.executeBatch();
        }
        finally
        {
            DatabaseUtils.close(pstmt);
            logger.info("stored " + inserted.length + " " + tableName + " items in "
                + timer.getElapsedMillis() + "ms");
        }
    }

    public void fetch()
    {
        if(downloadSource())
        {
            parseSource();
        }
    }
}
