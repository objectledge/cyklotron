package net.cyklotron.cms.locations;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.objectledge.test.LedgeTestCase;

import net.cyklotron.cms.locations.poland.PNALocationsProvider;

public class PNALocationProviderTest
    extends LedgeTestCase
{
    private PNALocationsProvider provider;

    public void setUp()
        throws Exception
    {
        provider = new PNALocationsProvider(getLogger(), getFileSystem());
    }

    public void testFromSource()
        throws IOException
    {
        initLog4J("ERROR");
        LogManager.getLogger(getClass()).setLevel(Level.INFO);
        provider.fromSource();
    }

    public void testFromCache()
        throws Exception
    {
        initLog4J("ERROR");
        LogManager.getLogger(getClass()).setLevel(Level.INFO);
        provider.fromCache();
    }
}
