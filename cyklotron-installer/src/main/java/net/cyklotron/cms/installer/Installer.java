package net.cyklotron.cms.installer;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.jcontainer.dna.Logger;
import org.jcontainer.dna.impl.Log4JLogger;
import org.objectledge.coral.tools.DataSourceFactory;
import org.objectledge.coral.tools.init.InitComponent;
import org.objectledge.coral.tools.rml.RmlRunnerComponent;
import org.objectledge.coral.tools.sql.SqlRunnerComponent;
import org.objectledge.filesystem.FileSystem;

public class Installer
{
    private String dbDriverClasspath;

    private String dbDriverClass;

    private Properties dbProperties;

    private boolean initForce;

    private FileSystem fileSystem;

    private Logger log;

    private DataSourceFactory dataSourceFactory;

    private Properties properties;

    private RmlRunnerComponent rmlRunner;

    private SqlRunnerComponent sqlRunner;

    private Map<String, Object> templateVars;

    private List<String> templateMacroLibraries = Collections.<String> emptyList();

    private String naming;

    public void init(Properties properties)
    {
        this.properties = properties;
        dbDriverClasspath = properties.getProperty("db.classpath");
        dbDriverClass = properties.getProperty("db.dsclass");
        dbProperties = extract(properties, "db.property.");
        initForce = Boolean.valueOf(properties.getProperty("init.force", "false"));
        templateVars = toMap(properties);
        naming = properties.getProperty("naming");
    }

    public void run()
    {
        initLogger();
        initFileSystem();
        initDataSource();
        initScriptRunners();

        try
        {
            initSchema();
            installBase();
            if(naming.equals("db"))
            {
                installDbNaming();
            }
            else
            {
                installLDAPNaming();
            }
        }
        catch(Exception e)
        {
            die("failed to execute installation scripts", e);
        }
        finally
        {
            shutdownDataSource();
        }
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

    private Properties extract(Properties in, String prefix)
    {
        Properties out = new Properties();
        Enumeration<String> pe = (Enumeration<String>)in.propertyNames();
        while(pe.hasMoreElements())
        {
            String p = pe.nextElement();
            if(p.startsWith(prefix))
            {
                String k = p.substring(prefix.length());
                String v = in.getProperty(p);
                out.setProperty(k, v);
            }
        }
        return out;
    }

    private void initDataSource()
    {
        try
        {
            dataSourceFactory = new DataSourceFactory(dbDriverClasspath, dbDriverClass,
                dbProperties, log);
        }
        catch(Exception e)
        {
            die("failed to initialized datasource", e);
        }
    }

    private void initScriptRunners()
    {
        rmlRunner = new RmlRunnerComponent(dataSourceFactory.getDataSource(),
            dataSourceFactory.getTransaction(), log);
        sqlRunner = new SqlRunnerComponent(fileSystem, dataSourceFactory.getDataSource(), log);
    }

    private void shutdownDataSource()
    {
        dataSourceFactory.close();
    }

    private void initSchema()
    {
        try
        {
            InitComponent init = new InitComponent(dataSourceFactory.getDataSource(), fileSystem,
                initForce, log);
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

    private void installBase()
        throws Exception
    {
        rmlRunner.run(".", "config", "root", "rml/cyklotron/install.list", "UTF-8", templateVars,
            templateMacroLibraries);

        rmlRunner.run(".", "config", "root", "rml/cyklotron/customization.list", "UTF-8",
            templateVars, templateMacroLibraries);

        sqlRunner.run("sql/cyklotron/customization.list", "UTF-8", templateVars,
            templateMacroLibraries);
    }

    private void installDbNaming()
        throws Exception
    {
        sqlRunner.run("sql/cyklotron/db_naming.list", "UTF-8", templateVars,
            Collections.<String> emptyList());
    }

    private void installLDAPNaming()
        throws Exception
    {
    }

    private Map<String, Object> toMap(Properties properties)
    {
        Enumeration<String> names = (Enumeration<String>)properties.propertyNames();
        Map<String, Object> result = new HashMap<>();
        while(names.hasMoreElements())
        {
            String name = names.nextElement();
            result.put(name, properties.get(name));
        }
        return result;
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
