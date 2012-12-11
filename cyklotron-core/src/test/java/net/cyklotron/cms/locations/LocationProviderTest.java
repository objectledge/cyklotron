package net.cyklotron.cms.locations;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Collection;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.jcontainer.dna.impl.DefaultConfiguration;
import org.objectledge.html.HTMLServiceImpl;

import net.cyklotron.cms.locations.poland.PNATERYTLocationsProvider;

public class LocationProviderTest
    extends LocationTestBase
{
    private PNATERYTLocationsProvider provider;

    public void setUp()
        throws Exception
    {
        super.setUp();
        if(enabled)
        {
            provider = new PNATERYTLocationsProvider(getLogger(), getFileSystem(), new HTMLServiceImpl(
                new DefaultConfiguration("config", "", "")), database);
        }
    }

    public void testFromSource()
        throws IOException
    {
        if(enabled)
        {
            initLog4J("ERROR");
            LogManager.getLogger(getClass()).setLevel(Level.INFO);
            final Collection<Location> locations = provider.fromSource();
            assertTrue(locations.size() > 100000);
        }
    }

    public void testFromCache()
        throws Exception
    {
        if(enabled)
        {
            initLog4J("ERROR");
            LogManager.getLogger(getClass()).setLevel(Level.INFO);
            // JUnit can execute tests in arbitrary order. When db is empty and testFromCache runs
            // first, load the data twice rather than fail
            try(Connection conn = database.getConnection(); Statement stmt = conn.createStatement())
            {
                if(count(stmt, "locations_pna") < 100000)
                {
                    provider.fromSource();
                }
            }
            final Collection<Location> locations = provider.fromCache();
            assertTrue(locations.size() > 100000);
        }
    }
}
