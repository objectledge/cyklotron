package net.cyklotron.cms.locations.poland;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.jcontainer.dna.Logger;
import org.objectledge.btm.BitronixDataSource;
import org.objectledge.database.Database;
import org.objectledge.database.DatabaseUtils;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.UnsupportedCharactersInFilePathException;
import org.objectledge.html.HTMLException;
import org.objectledge.html.HTMLService;
import org.objectledge.utils.Timer;

import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.files.util.CSVFileReader;
import net.cyklotron.cms.locations.Location;
import net.cyklotron.cms.locations.LocationsProvider;
import net.cyklotron.cms.organizations.Organization;

/**
 * LocationProvider implementation for Poland using Główny Urząd Statystyczny (terc area codes)
 * published by GUS.
 */
public class TERCLocationsProvider
    implements LocationsProvider
{
    private static final String ENCODING = "UTF-8";

    private static final String CACHE_DIRECTORY = "/ngo/locations/";

    private static final String SOURCE_LOCATION_DIRECTORY = "http://www.stat.gov.pl/broker/access/prefile/";

    private static final String WEB_SOURCE_LOCATION = "http://www.stat.gov.pl/broker/access/prefile/listPreFiles.jspa";

    private static String[] WEB_SOURCE_XPATH_LOCATIONS = {
                    "//TABLE[@id='row']//TD[text()='TERC']/../TD/A[contains(@href,'downloadPreFile.jspa')]",
                    "//TABLE[@id='row']//TD[text()='SIMC']/../TD/A[contains(@href,'downloadPreFile.jspa')]",
                    "//TABLE[@id='row']//TD[text()='WMRODZ']/../TD/A[contains(@href,'downloadPreFile.jspa')]" };

    private static final String[] SOURCE_TMP_FILES = { "TERC.xml", "SIMC.xml", "WMRODZ.xml" };

    private static final String[] SOURCE_FILES = { "spisterc.xml", "spissimc.xml", "spiswmrodz.xml" };

    private final Database database;

    private final Logger logger;

    private final FileSystem fileSystem;

    private final HTMLService htmlService;

    private List<String> source_locations;

    public TERCLocationsProvider(Logger logger, FileSystem fileSystem, HTMLService htmlService,
        Database database)
    {
        this.logger = logger;
        this.fileSystem = fileSystem;
        this.htmlService = htmlService;
        this.database = database;
    }

    private boolean parseSourceUrls()
    {
        try
        {
            Timer timer = new Timer();
            source_locations = new ArrayList<String>();
            HttpClient client = new HttpClient();
            HttpMethod method = new GetMethod(WEB_SOURCE_LOCATION);
            client.executeMethod(method);
            Document doc = htmlService.textToDom4j(method.getResponseBodyAsString());
            for(String xpathLocation : WEB_SOURCE_XPATH_LOCATIONS)
            {
                Element element = (Element)doc.selectSingleNode(xpathLocation);
                source_locations.add(SOURCE_LOCATION_DIRECTORY
                    + element.attribute("href").getValue());
            }
            logger.info("parsed downlod source urls from " + WEB_SOURCE_LOCATION
                + timer.getElapsedSeconds() + "s");
            return true;
        }
        catch(IOException | HTMLException e)
        {
            logger.error("failed to download web source from " + WEB_SOURCE_LOCATION, e);
            return false;
        }
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
        try
        {
            for(; i < source_locations.size(); i++)
            {
                Timer timer = new Timer();
                HttpClient client = new HttpClient();
                HttpMethod method = new GetMethod(source_locations.get(i));
                client.executeMethod(method);
                if(!fileSystem.isDirectory(CACHE_DIRECTORY))
                {
                    fileSystem.mkdirs(CACHE_DIRECTORY);
                }
                fileSystem.unpackZipFile(method.getResponseBodyAsStream(), CACHE_DIRECTORY);
                method.releaseConnection();
                rename(SOURCE_TMP_FILES[i], SOURCE_FILES[i]);
                logger.info("downloaded " + fileSystem.length(CACHE_DIRECTORY + SOURCE_FILES[i])
                    + " bytes in " + timer.getElapsedSeconds() + "s");
            }
            return true;
        }
        catch(IOException e)
        {
            logger.error("failed to download source from " + source_locations.get(i), e);
            return false;
        }
    }

    private void parseSource()
    {
        TERCSourceParser parser = new TERCSourceParser(fileSystem, logger);
        List<String[]> tercContent = parser.parse(CACHE_DIRECTORY + SOURCE_FILES[0]);
        List<String[]> simcContent = parser.parse(CACHE_DIRECTORY + SOURCE_FILES[1]);
        List<String[]> wmrodzContent = parser.parse(CACHE_DIRECTORY + SOURCE_FILES[2]);

        Connection conn = null;
        try
        {
            conn = database.getConnection();
            conn.setAutoCommit(false);
            writeDbTerc(conn, tercContent);
            writeDbSimc(conn, simcContent);
            writeDbWmrodz(conn, wmrodzContent);
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

    private void writeDbTerc(Connection conn, List<String[]> content)
        throws SQLException
    {
        Timer timer = new Timer();
        PreparedStatement pstmt = null;
        int[] inserted = new int[0];
        try
        {
            pstmt = conn.prepareStatement("DELETE FROM locations_terc");
            pstmt.execute();
            pstmt = conn
                .prepareStatement("INSERT INTO locations_terc(woj, pow, gmi, rodz, nazwa, nazdod) VALUES( ?, ?, ?, ?, ?, ?)");
            for(String[] lines : content)
            {
                pstmt.setString(1, lines[0]);
                pstmt.setString(2, lines[1]);
                pstmt.setString(3, lines[2]);
                pstmt.setString(4, lines[3]);
                pstmt.setString(5, lines[4]);
                pstmt.setString(6, lines[5]);
                pstmt.addBatch();
            }
            inserted = pstmt.executeBatch();
        }
        finally
        {
            DatabaseUtils.close(pstmt);
            logger.info("INSERT " + inserted.length + "with " + content.size()
                + " items to locations_terc DB in " + timer.getElapsedSeconds() + "s");
        }
    }

    private void writeDbWmrodz(Connection conn, List<String[]> content)
        throws SQLException
    {
        Timer timer = new Timer();
        PreparedStatement pstmt = null;
        int[] inserted = new int[0];
        try
        {
            pstmt = conn.prepareStatement("DELETE FROM locations_wmrodz");
            pstmt.execute();
            pstmt = conn
                .prepareStatement("INSERT INTO locations_wmrodz(rm, nazwa_rm) VALUES( ?, ?)");
            for(String[] lines : content)
            {
                pstmt.setString(1, lines[0]);
                pstmt.setString(2, lines[1]);
                pstmt.addBatch();
            }
            inserted = pstmt.executeBatch();
        }
        finally
        {
            DatabaseUtils.close(pstmt);
            logger.info("INSERT " + inserted.length + "with " + content.size()
                + " items to locations_wmrodz DB in " + timer.getElapsedSeconds() + "s");
        }
    }

    private void writeDbSimc(Connection conn, List<String[]> content)
        throws SQLException
    {
        Timer timer = new Timer();
        PreparedStatement pstmt = null;
        int[] inserted = new int[0];
        try
        {
            pstmt = conn.prepareStatement("DELETE FROM locations_simc");
            pstmt.execute();
            pstmt = conn
                .prepareStatement("INSERT INTO locations_simc(woj, pow, gmi, rodz_gmi, rm, mz, nazwa, sym, sympod) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            for(String[] lines : content)
            {
                pstmt.setString(1, lines[0]);
                pstmt.setString(2, lines[1]);
                pstmt.setString(3, lines[2]);
                pstmt.setString(4, lines[3]);
                pstmt.setString(5, lines[4]);
                pstmt.setString(6, lines[5]);
                pstmt.setString(7, lines[6]);
                pstmt.setString(8, lines[7]);
                pstmt.setString(9, lines[8]);
                pstmt.addBatch();
            }
            inserted = pstmt.executeBatch();
        }
        finally
        {
            DatabaseUtils.close(pstmt);
            logger.info("INSERT " + inserted.length + "with " + content.size()
                + " items to locations_simc DB in " + timer.getElapsedSeconds() + "s");
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

    @Override
    public Collection<Location> fromCache()
    {
        return null;
    }

    @Override
    public Collection<Location> fromSource()
    {
        if(parseSourceUrls() && downloadSource())
        {
            parseSource();
        }
        return null;
    }
}
