/*
 * Created on Oct 27, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.periodicals.internal;

import org.jcontainer.dna.Logger;
import org.objectledge.i18n.DateFormatter;
import org.objectledge.templating.Templating;

import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.periodicals.PeriodicalsTemplatingService;
import net.cyklotron.cms.site.SiteService;


/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PlainTextRenderer extends AbstractRenderer
{
    public PlainTextRenderer(Logger log, Templating templating,
        CategoryQueryService categoryQueryService, PeriodicalsService periodicalsService,
        PeriodicalsTemplatingService periodicalsTemplatingService, FilesService cmsFilesService,
        DateFormatter dateFormatter, IntegrationService integrationService, SiteService siteService)
    {
        super(log, templating, categoryQueryService, periodicalsService,
                        periodicalsTemplatingService, cmsFilesService, dateFormatter,
                        integrationService, siteService);
    }
    
    // inherit doc
    public String getName()
    {
        return PlainTextRendererFactory.RENDERER_NAME;
    }
    
    // inherit doc
    public String getFilenameSuffix()
    {
        return "txt";
    }

    // inherit doc
    public String getMimeType()
    {
        return "text/plain";
    }
    
    // inherit doc
    public String getMedium()
    {
        return "PLAIN";
    }
}
