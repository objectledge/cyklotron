/*
 * Created on Oct 27, 2003
 */
package net.cyklotron.cms.periodicals.internal;

import org.jcontainer.dna.Logger;
import org.objectledge.i18n.DateFormatter;
import org.objectledge.templating.Templating;

import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.site.SiteService;


/**
 * HTML Document renderer for periodicals.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HTMLRenderer.java,v 1.3 2005-02-02 23:08:00 pablo Exp $
 */
public class HTMLRenderer extends AbstractRenderer
{
    public HTMLRenderer(Logger log, Templating templating,
        CategoryQueryService categoryQueryService, PeriodicalsService periodicalsService,
        FilesService cmsFilesService, DateFormatter dateFormatter,
        IntegrationService integrationService, SiteService siteService)
    {
        super(log, templating, categoryQueryService, periodicalsService,
            cmsFilesService, dateFormatter, integrationService, siteService);
    }
    
    // inherit doc
    public String getFilenameSuffix()
    {
        return "html";
    }

    // inherit doc
    public String getMimeType()
    {
        return "text/html";
    }
    
    // inherit doc
    public String getMedium()
    {
        return "HTML";
    }
}
