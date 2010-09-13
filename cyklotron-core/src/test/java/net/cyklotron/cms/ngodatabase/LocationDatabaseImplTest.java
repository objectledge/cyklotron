package net.cyklotron.cms.ngodatabase;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.jcontainer.dna.Configuration;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.test.LedgeTestCase;

public class LocationDatabaseImplTest
    extends LedgeTestCase
{
    private LocationDatabaseServiceImpl pnaDatabase;
    
    public void setUp()
        throws Exception
    {
        FileSystem fs = getFileSystem();
        Configuration config = getConfig(fs,
            "data/config/net.cyklotron.cms.ngodatabase.LocationDatabaseService.xml");
        pnaDatabase = new LocationDatabaseServiceImpl(config, getLogger(), fs);
    }

    public void testDownloadSource() throws IOException
    {
        pnaDatabase.downloadSource();
    }
    
    public void testParseSource() throws Exception
    {
        initLog4J("ERROR");
        LogManager.getLogger(getClass()).setLevel(Level.INFO);
        pnaDatabase.parseSource();
    }
}
