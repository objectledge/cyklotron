package net.cyklotron.cms.installer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.objectledge.coral.tools.extract.FileExtractionComponent;
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

    private RmlRunnerComponent rmlRunner;

    private SqlRunnerComponent sqlRunner;

    private FileExtractionComponent fileExtractor;

    private Map<String, Object> templateVars;

    private File workdir;

    private File workdirConfig;

    private List<String> templateMacroLibraries = Collections.<String> emptyList();

    private String naming;

    private File war;

    public void init(Properties properties, File workdir, File war)
    {
        this.war = war;
        dbDriverClasspath = properties.getProperty("db.classpath");
        dbDriverClass = properties.getProperty("db.dsclass");
        dbProperties = extract(properties, "db.property.");
        initForce = Boolean.valueOf(properties.getProperty("init.force", "false"));
        templateVars = toMap(properties);
        templateVars.put("db_properties", dbProperties);
        naming = properties.getProperty("naming");
        this.workdir = workdir;
        workdirConfig = new File(workdir, "config");
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
            customizeWAR();
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
        fileExtractor = new FileExtractionComponent(fileSystem);
        workdirConfig.mkdirs();
        try
        {
            final String workdirPath = workdir.getCanonicalPath();
            log.info("work directory path: " + workdirPath);
            templateVars.put("workdir", workdirPath);
        }
        catch(IOException e)
        {
            die("failed to locate workdir", e);
        }
        if(workdirConfig.list().length > 0)
        {
            if(initForce)
            {
                log.info("purging configuration directory");
                deleteRecursively(workdirConfig, false);
            }
            else
            {
                die(workdirConfig.getPath() + " directory is not empty but force mode is disabled");
            }
        }
    }

    private void deleteRecursively(File file, boolean delete)
    {
        if(file.isDirectory())
        {
            for(File f : file.listFiles())
            {
                deleteRecursively(f, true);
            }
        }
        if(delete)
        {
            file.delete();
        }
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
            log.info("connecting to the database");
            dataSourceFactory = new DataSourceFactory(dbDriverClasspath, dbDriverClass,
                dbProperties, fileSystem, log);
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
            log.info("setting up Coral database schema");
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
        log.info("intalling base system");
        rmlRunner.run(".", "config", "root", "rml/cyklotron/install.list", "UTF-8", templateVars,
            templateMacroLibraries);

        log.info("applying customizations");
        rmlRunner.run(".", "config", "root", "rml/cyklotron/customization.list", "UTF-8",
            templateVars, templateMacroLibraries);

        sqlRunner.run("sql/cyklotron/customization.list", "UTF-8", templateVars,
            templateMacroLibraries);

        log.info("generating configuration files for the base system");
        fileExtractor.run("config_templates/base", workdirConfig, "UTF-8", templateVars,
            templateMacroLibraries);
    }

    private void installDbNaming()
        throws Exception
    {
        log.info("installing DB naming provider");
        sqlRunner.run("sql/cyklotron/db_naming.list", "UTF-8", templateVars,
            Collections.<String> emptyList());

        fileExtractor.run("config_templates/db_naming", workdirConfig, "UTF-8", templateVars,
            templateMacroLibraries);
    }

    private void installLDAPNaming()
        throws Exception
    {
        log.info("installing LDAP naming provider");
        fileExtractor.run("config_templates/ldap", workdirConfig, "UTF-8", templateVars,
            templateMacroLibraries);
    }

    private void customizeWAR()
        throws IOException
    {
        if(war != null)
        {
            log.info("creating customized WAR");
            File temp = Files.createTempDirectory("cyklotron-installer").toFile();
            final Path wp = war.toPath();
            File target = wp.resolveSibling(
                "customized-" + wp.getName(wp.getNameCount() - 1).toString()).toFile();
            JarProcessor.process(war, workdir, target, temp);
        }
    }

    private Map<String, Object> toMap(Properties properties)
    {
        Enumeration<String> names = (Enumeration<String>)properties.propertyNames();
        Map<String, Object> result = new HashMap<>();
        while(names.hasMoreElements())
        {
            String name = names.nextElement();
            result.put(name.replace('.', '_'), properties.get(name));
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
