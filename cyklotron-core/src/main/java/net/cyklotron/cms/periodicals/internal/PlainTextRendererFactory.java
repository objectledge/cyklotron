package net.cyklotron.cms.periodicals.internal;

import org.jcontainer.dna.Logger;
import org.objectledge.i18n.DateFormatter;
import org.objectledge.mail.MailSystem;
import org.objectledge.templating.Templating;

import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.periodicals.PeriodicalRenderer;
import net.cyklotron.cms.periodicals.PeriodicalRendererFactory;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.periodicals.PeriodicalsTemplatingService;
import net.cyklotron.cms.site.SiteService;

/**
 * @author pablo@caltha.pl
 *
 */
public class PlainTextRendererFactory 
    implements PeriodicalRendererFactory
{
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

    protected PeriodicalsTemplatingService periodicalsTemplatingService;

    private final MailSystem mailSystem;

    
    public PlainTextRendererFactory(Logger log, Templating templating, MailSystem mailSystem,
        CategoryQueryService categoryQueryService, 
        FilesService cmsFilesService, DateFormatter dateFormatter,
        IntegrationService integrationService, SiteService siteService,
        PeriodicalsTemplatingService periodicalsTemplatingService)
    {
        this.log = log;
        this.templating = templating;
        this.mailSystem = mailSystem;
        this.categoryQueryService = categoryQueryService;
        this.cmsFilesService = cmsFilesService;
        this.dateFormatter = dateFormatter;
        this.siteService = siteService;
        this.periodicalsTemplatingService = periodicalsTemplatingService;
    }
    
    
    /** 
     * {@inheritDoc}
     */
    public PeriodicalRenderer getRenderer(PeriodicalsService periodicalsService)
    {
        return new PlainTextRenderer(log,templating, mailSystem,
            categoryQueryService, periodicalsService, periodicalsTemplatingService,
            cmsFilesService, dateFormatter, integrationService,siteService);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getRendererName()
    {
        return PlainTextRenderer.RENDERER_NAME;
    }
}
