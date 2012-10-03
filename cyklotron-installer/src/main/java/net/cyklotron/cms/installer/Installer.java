package net.cyklotron.cms.installer;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.log4j.BasicConfigurator;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.objectledge.coral.tools.DataSourceFactory;
import org.objectledge.coral.tools.init.InitComponent;
import org.objectledge.filesystem.FileSystem;

public class Installer
{
    private String dbDriverClasspath;

    private String dbDriverClass;

    private String dbUrl;

    private String dbUser;

    private String dbPassword;

    private boolean initForce;

    private FileSystem fileSystem;

    private Logger log;

    private DataSource dataSource;

    public void init(Properties properties)
    {
        dbDriverClasspath = properties.getProperty("db.driver.classpath");
        dbDriverClass = properties.getProperty("db.driver.class");
        dbUrl = properties.getProperty("db.url");
        dbUser = properties.getProperty("db.user");
        dbPassword = properties.getProperty("db.password");

        initForce = Boolean.getBoolean(properties.getProperty("init.force", "false"));
    }

    public void run()
    {
        initLogger();
        initFileSystem();
        initDataSource();

        initSchema();
    }

    private void initLogger()
    {
        BasicConfigurator.configure();
        log = new Log4JLogger(org.apache.log4j.Logger.getRootLogger());
    }

    private void initFileSystem()
    {
        fileSystem = FileSystem.getStandardFileSystem(".");
    }

    private void initDataSource()
    {
        try
        {
            ClassLoader cl = DataSourceFactory.getDriverClassLoader(dbDriverClasspath);
            Thread.currentThread().setContextClassLoader(cl);
        }
        catch(Exception e)
        {
            die("failed to initialize database driver classloader", e);
        }
        try
        {
            dataSource = DataSourceFactory.newDataSource(dbDriverClass, dbUrl, dbUser, dbPassword);
        }
        catch(SQLException e)
        {
            die("failed to initialized datasource", e);
        }
    }

    private void initSchema()
    {
        try
        {
            InitComponent init = new InitComponent(dataSource, fileSystem, initForce, log);
            try
            {
                init.run();
            }
            catch(Exception e)
            {
                die("schema initalization failed", e);
            }
        }
        catch(SQLException e)
        {
            die("failed to initialize Coral schema init component", e);
        }
    }

    private void die(String msg)
    {
        System.err.println(msg);
        System.exit(1);
    }

    private void die(String msg, Throwable t)
    {
        System.err.println(msg);
        t.printStackTrace(System.err);
        System.exit(1);
    }
}
