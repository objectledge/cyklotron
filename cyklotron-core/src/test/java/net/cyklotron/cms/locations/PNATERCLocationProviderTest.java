package net.cyklotron.cms.locations;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.jcontainer.dna.impl.DefaultConfiguration;
import org.objectledge.html.HTMLServiceImpl;

import net.cyklotron.cms.locations.poland.PNATERYTLocationsProvider;

public class PNATERCLocationProviderTest
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
            try(Connection conn = database.getConnection(); Statement stmt = conn.createStatement())
            {
                // JUnit can execute tests in arbitrary order.
                if(count(stmt, "locations_pna") < 100000)
                {
                    provider.fromSource();
                }
            }
            final Collection<Location> locations = provider.fromCache();
            assertTrue(locations.size() > 100000);
        }
    }

    public void testDataQuality()
        throws Exception
    {
        if(enabled)
        {
            initLog4J("ERROR");
            LogManager.getLogger(getClass()).setLevel(Level.INFO);
            try(Connection conn = database.getConnection(); Statement stmt = conn.createStatement())
            {
                // JUnit can execute tests in arbitrary order.
                if(count(stmt, "locations_pna") < 100000)
                {
                    provider.fromSource();
                }
                Map<Integer, Integer> rowCount = new HashMap<>();
                for(int i = 1; i <= 6; i++)
                {
                    rowCount.put(i, 0);
                }
                try(ResultSet rset = stmt
                    .executeQuery("select score, count(*) from locations_vpna group by score order by 1"))
                {
                    while(rset.next())
                    {
                        rowCount.put(rset.getInt(1), rset.getInt(2));
                    }
                }
                assertTrue(rowCount.get(2) < 20);
                assertTrue(rowCount.get(3) + rowCount.get(5) < 20);
                assertTrue(rowCount.get(4) < 150);
                assertTrue(rowCount.get(6) < 60);
                assertTrue(rowCount.get(7) < 5);
            }
        }
    }
}
