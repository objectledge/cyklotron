/*
 * Created on Oct 27, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.periodicals.internal;

import java.util.Date;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.AmbigousEntityNameException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.DateFormatter;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;

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

/**
 * @author fil
 *
 */
public class PlainTextNotificationRenderer extends PlainTextRenderer
{
    private static final String FILE_NAME_SUFFIX = "-notification";
    
    public PlainTextNotificationRenderer(Logger log, Templating templating,
        CategoryQueryService categoryQueryService, PeriodicalsService periodicalsService,
        FilesService cmsFilesService, DateFormatter dateFormatter,
        IntegrationService integrationService, SiteService siteService)
    {
        super(log, templating, categoryQueryService, periodicalsService,
            cmsFilesService, dateFormatter, integrationService, siteService);
    }
    
    public boolean render(CoralSession coralSession, PeriodicalResource periodical, Date time, FileResource file)
    {
        DirectoryResource parent = (DirectoryResource)file.getParent();
        String fileName = file.getName()+FILE_NAME_SUFFIX;
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
        
        if(file.getName().endsWith(FILE_NAME_SUFFIX))
        {
            String fileName = file.getName().substring(0, file.getName().length() - FILE_NAME_SUFFIX.length());
            FileResource contentFile;
            try
            {
                DirectoryResource parent = (DirectoryResource)file.getParent();
                contentFile = (FileResource)parent.getChild(coralSession, fileName);
                tContext.put("contentFile", contentFile);
            }
            catch(EntityDoesNotExistException e)
            {
                log.error("missing content file for notification of "+periodical, e);
            }
            catch(AmbigousEntityNameException e)
            {
                log.error("ambigous content file name of "+periodical, e);
            }
        }
        else
        {
            log.error("unexepected name of the notificatio file "+file.getPath()+" of "+periodical);
        }
        
        return tContext;
    }
}
