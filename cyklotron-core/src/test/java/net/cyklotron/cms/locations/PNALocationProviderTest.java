package net.cyklotron.cms.locations;

import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.objectledge.btm.BitronixDataSource;
import org.objectledge.btm.BitronixTransaction;
import org.objectledge.btm.BitronixTransactionManager;

import org.objectledge.context.Context;
import org.objectledge.database.Database;
import org.objectledge.database.DatabaseUtils;
import org.objectledge.database.DefaultDatabase;
import org.objectledge.database.IdGenerator;
import org.objectledge.database.SequenceIdGenerator;
import org.objectledge.database.Transaction;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.test.LedgeTestCase;
import org.picocontainer.defaults.DefaultPicoContainer;

import net.cyklotron.cms.locations.poland.PNALocationsProvider;

public class PNALocationProviderTest
    extends LedgeTestCase
{
    private FileSystem fileSystem;

    private BitronixTransactionManager btm;

    private PNALocationsProvider provider;

    private boolean pg_exist = false;

    public void setUp()
        throws Exception
    {
        DefaultPicoContainer container = new DefaultPicoContainer();
        Logger logger = new Log4JLogger(org.apache.log4j.Logger.getLogger(getClass()));
        try
        {
            BitronixTransactionManager btm = new BitronixTransactionManager("pg",
                "org.postgresql.xa.PGXADataSource", getDsProperties(), logger);

            DataSource dataSource = new BitronixDataSource("pg", btm);
            prepareDataSource(dataSource);

            Transaction transaction = new BitronixTransaction(btm, new Context(), logger, null);
            IdGenerator idGenerator = new SequenceIdGenerator(dataSource);
            Database database = new DefaultDatabase(dataSource, idGenerator, transaction);
            provider = new PNALocationsProvider(getLogger(), getFileSystem(), database);
        }
        catch(Exception e)
        {
            getLogger().warn("postgres database localhost/btm not available - skipping test");
            return;
        }
        pg_exist = true;
    }

    private Properties getDsProperties()
    {
        Properties properties = new Properties();
        properties.put("serverName", "localhost");
        properties.put("databaseName", "locations");
        properties.put("user", "cyklotron");
        properties.put("password", "");
        return properties;
    }

    public void tearDown()
    {
        if(btm != null)
        {
            btm.stop();
        }
    }

    private void prepareDataSource(DataSource ds)
        throws Exception
    {
        FileSystem fs = getFileSystem();
        if(!DatabaseUtils.hasTable(ds, "locations_pna")
            && !DatabaseUtils.hasTable(ds, "locations_terc")
            && !DatabaseUtils.hasTable(ds, "locations_simc")
            && !DatabaseUtils.hasTable(ds, "locations_ulic")
            && !DatabaseUtils.hasTable(ds, "locations_wmrodz"))
        {

            DatabaseUtils.runScript(ds,
                FileSystem.getStandardFileSystem("src/main/resources").getReader("sql/locations/LocationsDataTables.sql", "UTF-8"));

        }
    }

    public void testFromSource()
        throws IOException
    {
        if(pg_exist)
        {
            initLog4J("ERROR");
            LogManager.getLogger(getClass()).setLevel(Level.INFO);
            provider.fromSource();
        }
    }

    public void testFromCache()
        throws Exception
    {
        if(pg_exist)
        {
            initLog4J("ERROR");
            LogManager.getLogger(getClass()).setLevel(Level.INFO);
            provider.fromCache();
        }
    }

}
