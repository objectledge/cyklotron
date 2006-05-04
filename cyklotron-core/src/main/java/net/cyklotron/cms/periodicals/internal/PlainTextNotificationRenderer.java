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
import net.cyklotron.cms.periodicals.PeriodicalsTemplatingService;
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
        PeriodicalsTemplatingService periodicalsTemplatingService, FilesService cmsFilesService,
        DateFormatter dateFormatter, IntegrationService integrationService, SiteService siteService)
    {
        super(log, templating, categoryQueryService, periodicalsService,
                        periodicalsTemplatingService, cmsFilesService, dateFormatter,
                        integrationService, siteService);
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
    
    // inherit doc
    public String getFilenameSuffix()
    {
        return "txt-notification";
    }
    
    // inherit doc
    public String getName()
    {
        return PlainTextNotificationRendererFactory.RENDERER_NAME;
    }    
}
