package net.cyklotron.cms.locations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

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

public abstract class LocationTestBase
    extends LedgeTestCase
{
    private BitronixTransactionManager btm;

    protected Database database;

    protected boolean enabled = false;

    public void setUp()
        throws Exception
    {
        Logger logger = new Log4JLogger(org.apache.log4j.Logger.getLogger(getClass()));
        try
        {
            btm = new BitronixTransactionManager("pg", "org.postgresql.xa.PGXADataSource",
                getDsProperties(), logger);

            DataSource dataSource = new BitronixDataSource("pg", btm);
            prepareDataSource(dataSource);

            Transaction transaction = new BitronixTransaction(btm, new Context(), logger, null);
            IdGenerator idGenerator = new SequenceIdGenerator(dataSource);
            database = new DefaultDatabase(dataSource, idGenerator, transaction);
        }
        catch(Exception e)
        {
            getLogger().warn("postgres database localhost/locations not available - skipping test");
            return;
        }
        enabled = true;
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
        FileSystem fs = FileSystem.getStandardFileSystem("src/main/resources");
        if(!DatabaseUtils.hasTable(ds, "locations_pna")
            && !DatabaseUtils.hasTable(ds, "locations_terc")
            && !DatabaseUtils.hasTable(ds, "locations_simc")
            && !DatabaseUtils.hasTable(ds, "locations_wmrodz"))
        {
            DatabaseUtils.runScript(ds,
                fs.getReader("sql/locations/LocationsDataTables.sql", "UTF-8"));
        }
    }

    protected int count(Statement stmt, String table)
        throws SQLException
    {
        try(ResultSet rset = stmt.executeQuery("SELECT COUNT(*) FROM " + table))
        {
            rset.next();
            return rset.getInt(1);
        }
    }

    protected void assertMinCount(Statement stmt, String table, int minCount)
        throws SQLException
    {
        assertTrue(count(stmt, table) >= minCount);
    }
}
