/*
 * Created on Oct 27, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.periodicals.internal;

import java.util.Date;

import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.periodicals.EmailPeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.site.SiteService;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.AmbigousEntityNameException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.DateFormatter;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;

/**
 * @author fil
 *
 */
public class PlainTextNotificationRenderer extends PlainTextRenderer
{
    public PlainTextNotificationRenderer(Configuration config, Logger log, Templating templating,
        CategoryQueryService categoryQueryService, PeriodicalsService periodicalsService,
        FilesService cmsFilesService, DateFormatter dateFormatter,
        IntegrationService integrationService, SiteService siteService)
    {
        super(config, log, templating, categoryQueryService, periodicalsService,
            cmsFilesService, dateFormatter, integrationService, siteService);
    }
    
    // TODO hack - this object is used exclusively by a single thread
    // the notification renederer....is not used by single thread... think it over again!!!
    
    //private FileResource contentFile;
    
    public boolean render(CoralSession coralSession, PeriodicalResource periodical, Date time, FileResource file)
    {
        FileResource contentFile = file;
        DirectoryResource parent = (DirectoryResource)file.getParent();
        String fileName = file.getName()+"-notification";
        FileResource notification;
        try
        {
            notification = (FileResource)parent.getChild(coralSession, fileName);
        }
        catch(EntityDoesNotExistException e)
        {
            try
            {
                 notification = cmsFilesService.createFile(coralSession, fileName, null, getMimeType(),
                    periodical.getEncoding(), parent);
            }
            catch (FilesException ee)
            {
                log.error("failed to create notification file for "+periodical.getPath(), ee);
                return false;
            }                
        }
        catch(AmbigousEntityNameException e)
        {
            log.error("inconsistend data in cms files application", e);
            return false;
        }
        return super.render(coralSession, periodical, time, notification);
    }
    
    protected String getRendererName(PeriodicalResource r)
    {
        if(r instanceof EmailPeriodicalResource)
        {
            return ((EmailPeriodicalResource)r).getNotificationRenderer();
        }
        else
        {
            log.warn("using notification renderer for non-email periodical "+r.getPath());
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
    
    protected TemplatingContext setupContext(CoralSession coralSession, PeriodicalResource periodical, Date time, FileResource file)
    {
        TemplatingContext tContext = super.setupContext(coralSession, periodical, time, file);
        //TODO 
        //tContext.put("contentFile", contentFile);
        return tContext;
    }
}
