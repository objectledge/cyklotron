package net.cyklotron.cms.skins;

import java.io.IOException;

import net.labeo.Labeo;
import net.labeo.services.ServiceBroker;
import net.labeo.services.file.FileService;
import net.labeo.services.logging.LoggingFacility;
import net.labeo.services.logging.LoggingService;

import net.cyklotron.cms.site.SiteCreationListener;

public class SkinsSiteCreationListener
    implements SiteCreationListener
{
    private boolean initialized;

    protected FileService fileService;

    protected LoggingFacility log;

    public synchronized void init()
    {
        if(!initialized)
        {
            ServiceBroker broker = Labeo.getBroker();
            LoggingService loggingService = (LoggingService)broker.
                getService(LoggingService.SERVICE_NAME);
            log = loggingService.getFacility("cms");
            fileService = (FileService)broker.
                getService(FileService.SERVICE_NAME);
            initialized = true;
        }
    }

    /**
     * Called when a new site is created.
     *
     * <p>The method will be called after the site Resources are successfully
     * copied from the template.</p>
     *
     * @param template the site template name.
     * @param name the site name.
     */
    public void createSite(String template, String name)
    {
        init();

        // copy templates
        try
        {
            String src = "/templates/cms/sites/"+template;
            String dst = "/templates/cms/sites/"+name;
            copyDir(src, dst);
        }
        catch(Exception e)
        {
            log.error("failed to copy templates", e);
        }

        // copy content
        try
        {
            String src = "/content/cms/sites/"+template;
            String dst = "/content/cms/sites/"+name;
            copyDir(src, dst);
        }
        catch(Exception e)
        {
            log.error("failed to copy content", e);
        }
    }

    // implementation ////////////////////////////////////////////////////////

    protected void copyDir(String src, String dst)
        throws IOException
    {
        if(!fileService.exists(src))
        {
            throw new IOException("source directory "+src+" does not exist");
        }
		if(!fileService.canRead(src))
		{
			throw new IOException("source directory "+src+" is not readable");
		}
		if(!fileService.isDirectory(src))
		{
			throw new IOException(src+" is not a directory");
		}
        fileService.mkdirs(dst);
        String[] srcFiles = fileService.list(src);
        for(int i=0; i<srcFiles.length; i++)
        {
            String name = srcFiles[i];
            if(name.startsWith(".") || name.equals("CVS"))
            {
                continue;
            }
            if(fileService.isDirectory(src+"/"+name))
            {
                copyDir(src+"/"+name, dst+"/"+name);
            }
            else
            {
                fileService.copyFile(src+"/"+name, dst+"/"+name);
            }
        }
    }
}
