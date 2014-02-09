package net.cyklotron.cms.modules.views.site;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.sitemap.SitemapGenerationParticipant;
import net.cyklotron.cms.sitemap.SitemapService;
import net.cyklotron.cms.sitemap.internal.SitemapConfiguration;

public class SitemapSettings
    extends BaseCMSScreen
{
    private final SitemapService sitemapService;

    public SitemapSettings(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        SitemapService sitemapService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.sitemapService = sitemapService;
    }

    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        final SitemapConfiguration configuration = sitemapService.configuration();
        final Parameters participantsConfig = configuration.getParticipantsConfig();
        final Map<String, Parameters> participantConfig = new LinkedHashMap<>();
        for(SitemapGenerationParticipant participant : sitemapService.participants())
        {
            if(participant.supportsConfiguration())
            {
                final String name = participant.name();
                participantConfig.put(name, participantsConfig.getChild(name + "."));
            }
        }
        templatingContext.put("config", configuration);
        templatingContext.put("partipantsConfig", participantConfig);
    }
}
