/*
 * Created on Oct 27, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.periodicals.internal;

import java.util.Date;

import net.labeo.services.InitializationError;
import net.labeo.services.ServiceBroker;
import net.labeo.services.logging.Logger;
import net.labeo.services.resource.AmbigousNameException;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.CoralSession;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;

import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.periodicals.EmailPeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalResource;

/**
 * @author fil
 *
 */
public class PlainTextNotificationRenderer extends PlainTextRenderer
{
    protected CoralSession resourceService;
    
    protected Subject rootSubject;

    // hack - this object is used exclusively by a single thread
    private FileResource contentFile;
    
    public void init(Configuration config, ServiceBroker broker, Logger log)
    {
        super.init(config, broker, log);
        resourceService = (CoralSession)broker.getService(CoralSession.SERVICE_NAME);
        try
        {
            rootSubject = resourceService.getSecurity().getSubject(Subject.ROOT);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new InitializationError("failed to lookup root subject", e);
        }
    }

    public boolean render(PeriodicalResource periodical, Date time, FileResource file)
    {
        contentFile = file;
        DirectoryResource parent = (DirectoryResource)file.getParent();
        String fileName = file.getName()+"-notification";
        FileResource notification;
        try
        {
            notification = (FileResource)parent.getChild(fileName);
        }
        catch(EntityDoesNotExistException e)
        {
            try
            {
                 notification = cmsFilesService.createFile(fileName, null, getMimeType(),
                    periodical.getEncoding(), parent, rootSubject);
            }
            catch (FilesException ee)
            {
                log.error("failed to create notification file for "+periodical.getPath(), ee);
                return false;
            }                
        }
        catch(AmbigousNameException e)
        {
            log.error("inconsistend data in cms files application", e);
            return false;
        }
        return super.render(periodical, time, notification);
    }
    
    protected String getRendererName(PeriodicalResource r)
    {
        if(r instanceof EmailPeriodicalResource)
        {
            return ((EmailPeriodicalResource)r).getNotificationRenderer();
        }
        else
        {
            log.warning("using notification renderer for non-email periodical "+r.getPath());
            return r.getRenderer();
        }
    }

    protected String getTemplateName(PeriodicalResource r)
    {
        if(r instanceof EmailPeriodicalResource)
        {
            return ((EmailPeriodicalResource)r).getNotificationTemplate();
        }
        else
        {
            return r.getTemplate();
        }
    }
    
    protected Context setupContext(PeriodicalResource periodical, Date time, FileResource file)
    {
        Context context = super.setupContext(periodical, time, file);
        context.put("contentFile", contentFile);
        return context;
    }
    
    protected void releaseContext(Context context)
    {
        super.releaseContext(context);
        contentFile = null;
    }
}
