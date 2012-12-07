package net.cyklotron.cms.locations.poland;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jcontainer.dna.Logger;
import org.objectledge.database.Database;
import org.objectledge.database.DatabaseUtils;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.UnsupportedCharactersInFilePathException;
import org.objectledge.html.HTMLException;
import org.objectledge.html.HTMLService;

import org.objectledge.utils.Timer;

import net.cyklotron.cms.locations.Location;
import net.cyklotron.cms.locations.LocationsProvider;

/**
 * LocationProvider implementation for Poland using Główny Urząd Statystyczny (terc area codes)
 * published by GUS.
 */
public class TERCLocationsProvider
    implements LocationsProvider
{
    private static final String ENCODING = "UTF-8";

    private static final String SOURCE_MIME_TYPE = "application/zip";

    private static final String CACHE_DIRECTORY = "/ngo/locations/";

    private static final String SOURCE_LOCATION_DIRECTORY = "http://www.stat.gov.pl/broker/access/prefile/";

    private static final String WEB_SOURCE_LOCATION = "http://www.stat.gov.pl/broker/access/prefile/listPreFiles.jspa";

    private static String WEB_SOURCE_XPATH_LOCATION = "//TABLE[@id='row']//TD[text()='%s']/../TD/A[contains(@href,'downloadPreFile.jspa')]";

    private static final String[] DATA_NAMES = { "terc", "simc", "wmrodz" };

    private final Database database;

    private final Logger logger;

    private final FileSystem fileSystem;

    private final HTMLService htmlService;

    public TERCLocationsProvider(Logger logger, FileSystem fileSystem, HTMLService htmlService,
        Database database)
    {
        this.logger = logger;
        this.fileSystem = fileSystem;
        this.htmlService = htmlService;
        this.database = database;
    }

    private List<String> parseSourceUrls()
    {
        List<String> sourceUrls = new ArrayList<String>();
        try
        {
            Timer timer = new Timer();

            HttpClient client = new HttpClient();
            HttpMethod method = new GetMethod(WEB_SOURCE_LOCATION);
            client.executeMethod(method);
            Document doc = htmlService.textToDom4j(method.getResponseBodyAsString());
            for(String name : DATA_NAMES)
            {
                String xpathLocation = WEB_SOURCE_XPATH_LOCATION.replace("%s", name.toUpperCase());
                Element element = (Element)doc.selectSingleNode(xpathLocation);
                sourceUrls.add(SOURCE_LOCATION_DIRECTORY + element.attribute("href").getValue());
            }
            logger.info("parsed downlod source urls from " + WEB_SOURCE_LOCATION
                + timer.getElapsedSeconds() + "s");
        }
        catch(IOException | HTMLException e)
        {
            logger.error("failed to download web source from " + WEB_SOURCE_LOCATION, e);

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
        int i = 0;
        List<String> sourceUrls = parseSourceUrls();
        try
        {
            for(; i < sourceUrls.size(); i++)
            {
                Timer timer = new Timer();
                HttpClient client = new HttpClient();
                HttpMethod method = new GetMethod(sourceUrls.get(i));
                client.executeMethod(method);
                if(!fileSystem.isDirectory(CACHE_DIRECTORY))
                {
                    fileSystem.mkdirs(CACHE_DIRECTORY);
                }
                fileSystem.unpackZipFile(method.getResponseBodyAsStream(), CACHE_DIRECTORY);
                method.releaseConnection();

                if(fileSystem.isFile(CACHE_DIRECTORY + DATA_NAMES[i].toUpperCase() + ".xml"))
                {
                    fileSystem.rename(CACHE_DIRECTORY + DATA_NAMES[i].toUpperCase() + ".xml",
                        CACHE_DIRECTORY + DATA_NAMES[i] + ".xml");
                    logger.info("downloaded "
                        + fileSystem.length(CACHE_DIRECTORY + DATA_NAMES[i] + ".xml")
                        + " bytes in " + timer.getElapsedSeconds() + "s");
                }
                else
                {
                    logger.info("Cannot find expected file: " + CACHE_DIRECTORY + DATA_NAMES[i]
                        + ".xml");
                }
            }
            return true;
        }
        catch(UnsupportedCharactersInFilePathException e)
        {
            logger.error("failed to rename file " + DATA_NAMES[i].toUpperCase() + ".xml to"
                + DATA_NAMES[i] + ".xml", e);
            return false;
        }
        catch(IOException e)
        {
            logger.error("failed to download source from " + sourceUrls.get(i), e);
            return false;
        }
    }

    private void parseSource()
    {
        TERCSourceParser parser = new TERCSourceParser(fileSystem, logger);
        List<String[]> tercContent = parser.parse(CACHE_DIRECTORY + DATA_NAMES[0] + ".xml");
        String[] tercHeadings = parser.getHeadings();
        List<String[]> simcContent = parser.parse(CACHE_DIRECTORY + DATA_NAMES[1] + ".xml");
        String[] simcHeadings = parser.getHeadings();
        List<String[]> wmrodzContent = parser.parse(CACHE_DIRECTORY + DATA_NAMES[2] + ".xml");
        String[] wmrodzHeadings = parser.getHeadings();

        Connection conn = null;
        try
        {
            conn = database.getConnection();
            conn.setAutoCommit(false);
            writeDb(conn, tercContent, tercHeadings, "locations_terc");
            writeDb(conn, simcContent, simcHeadings, "locations_simc");
            writeDb(conn, wmrodzContent, wmrodzHeadings, "locations_wmrodz");
            conn.commit();
        }
        catch(SQLException e)
        {
            try
            {
                conn.rollback();
            }
            catch(SQLException ex)
            {
                logger.error("error on rollback items to DB in ", ex);
                throw new RuntimeException(e);
            }
            finally
            {
                logger.error("error on writing items to DB in ", e);
                throw new RuntimeException(e);
            }
        }
        finally
        {
            DatabaseUtils.close(conn);
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
            java.sql.Date date = new java.sql.Date(0);
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
                pstmt.setDate(headings.length, date.valueOf(lines[headings.length - 1]));
                pstmt.addBatch();
            }
            inserted = pstmt.executeBatch();
        }
        finally
        {
            DatabaseUtils.close(pstmt);
            logger.info("INSERT " + inserted.length + "with " + content.size() + " items to "
                + tableName + " DB in " + timer.getElapsedSeconds() + "s");
        }
    }

    @Override
    public Collection<Location> fromCache()
    {
        return null;
    }

    @Override
    public Collection<Location> fromSource()
    {
        if(downloadSource())
        {
            parseSource();
        }
        return null;
    }
}
