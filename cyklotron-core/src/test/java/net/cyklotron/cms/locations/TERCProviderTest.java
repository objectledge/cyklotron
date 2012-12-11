package net.cyklotron.cms.locations;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.jcontainer.dna.impl.DefaultConfiguration;
import org.objectledge.html.HTMLServiceImpl;

import net.cyklotron.cms.locations.poland.TERCProvider;

public class TERCProviderTest
    extends LocationTestBase
{
    private TERCProvider tercProvider;

    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();

        if(enabled)
        {
            tercProvider = new TERCProvider(getLogger(), getFileSystem(),
                new HTMLServiceImpl(new DefaultConfiguration("config", "", "")), database);
        }
    }

    public void testTERCProvider()
        throws SQLException
    {
        if(enabled)
        {
            initLog4J("ERROR");
            LogManager.getLogger(getClass()).setLevel(Level.INFO);
            tercProvider.fetch();
            try(Connection conn = database.getConnection(); Statement stmt = conn.createStatement())
            {
                assertMinCount(stmt, "locations_terc", 400);
                assertMinCount(stmt, "locations_simc", 100000);
                assertMinCount(stmt, "locations_wmrodz", 40);
            }
        }
    }
}