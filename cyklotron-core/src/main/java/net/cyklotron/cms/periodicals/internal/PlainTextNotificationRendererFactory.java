package net.cyklotron.cms.periodicals.internal;

import org.jcontainer.dna.Logger;
import org.objectledge.i18n.DateFormatter;
import org.objectledge.templating.Templating;

import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.periodicals.PeriodicalRenderer;
import net.cyklotron.cms.periodicals.PeriodicalRendererFactory;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.site.SiteService;

/**
 * @author pablo@caltha.pl
 *
 */
public class PlainTextNotificationRendererFactory 
    implements PeriodicalRendererFactory
{
    /** renderer name */
    public static final String RENDERER_NAME = "plain_text_notification";
    
    /** the logging facility. */
    protected Logger log;

    /** templating service. */
    protected Templating templating;

    /** category query service. */
    protected CategoryQueryService categoryQueryService;

    /** file service. */
    protected FilesService cmsFilesService;

    /** date formater */
    protected DateFormatter dateFormatter;

    protected IntegrationService integrationService;

    protected SiteService siteService;

    
    public PlainTextNotificationRendererFactory(Logger log, Templating templating,
        CategoryQueryService categoryQueryService, 
        FilesService cmsFilesService, DateFormatter dateFormatter,
        IntegrationService integrationService, SiteService siteService)
    {
        this.log = log;
        this.templating = templating;
        this.categoryQueryService = categoryQueryService;
        this.cmsFilesService = cmsFilesService;
        this.dateFormatter = dateFormatter;
        this.siteService = siteService;
    }
    
    
    /** 
     * {@inheritDoc}
     */
    public PeriodicalRenderer getRenderer(PeriodicalsService periodicalsService)
    {
        return new PlainTextNotificationRenderer(log,templating, 
            categoryQueryService, periodicalsService, 
            cmsFilesService, dateFormatter, integrationService,siteService);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getRendererName()
    {
        return RENDERER_NAME;
    }
}
