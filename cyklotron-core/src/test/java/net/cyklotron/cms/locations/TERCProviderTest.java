package net.cyklotron.cms.locations;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.jcontainer.dna.impl.DefaultConfiguration;
import org.objectledge.html.HTMLServiceImpl;

import net.cyklotron.cms.locations.poland.TERCLocationsProvider;

public class TERCProviderTest
    extends LocationTestBase
{
    private TERCLocationsProvider tercProvider;

    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();

        if(enabled)
        {
            tercProvider = new TERCLocationsProvider(getLogger(), getFileSystem(),
                new HTMLServiceImpl(new DefaultConfiguration("config", "", "")), database);
        }
    }

    public void testTERCProvider()
    {
        if(enabled)
        {
            initLog4J("ERROR");
            LogManager.getLogger(getClass()).setLevel(Level.INFO);
            tercProvider.fromSource();
        }
    }
}
