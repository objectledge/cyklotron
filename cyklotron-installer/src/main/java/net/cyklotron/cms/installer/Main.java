package net.cyklotron.cms.installer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Main
{
    public static void main(String[] args)
    {
        Installer installer = new Installer();
        Properties properties = new Properties();
        File workdir;
        if(args.length > 0)
        {
            File f = new File(args[0]);

            try
            {
                properties.load(new FileInputStream(f));
            }
            catch(FileNotFoundException e)
            {
                die(f.getAbsolutePath() + " does not exist");
            }
            catch(IOException e)
            {
                die("can't read " + f.getAbsolutePath());
            }
        }
        else
        {
            try
            {
                properties.load(Main.class.getResourceAsStream("/installer.properties"));
            }
            catch(IOException e)
            {
                die("can't load /installer.properties from classpath");
            }
        }
        if(args.length > 1)
        {
            workdir = new File(args[1]);
        }
        else
        {
            workdir = new File("workdir");
        }
        if(workdir.exists() && !workdir.isDirectory())
        {
            die(workdir.getPath() + " exists but is not a directory. "
                + "Please remove it or provide another path");
        }
        else
        {
            workdir.mkdirs();
        }

        installer.init(properties, workdir);
        installer.run();
    }

    private static void die(String msg)
    {
        System.err.println(msg);
        System.exit(1);
    }
}
