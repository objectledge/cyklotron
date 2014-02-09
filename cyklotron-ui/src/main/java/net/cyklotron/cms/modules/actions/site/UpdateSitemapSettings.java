package net.cyklotron.cms.modules.actions.site;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.sitemap.SitemapGenerationParticipant;
import net.cyklotron.cms.sitemap.SitemapService;
import net.cyklotron.cms.sitemap.internal.SitemapConfiguration;
import net.cyklotron.cms.structure.StructureService;

public class UpdateSitemapSettings
    extends BaseSiteAction
{
    private final SitemapService sitemapService;

    public UpdateSitemapSettings(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SiteService siteService, SitemapService sitemapService)
    {
        super(logger, structureService, cmsDataFactory, siteService);
        this.sitemapService = sitemapService;
    }

    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        SitemapConfiguration config = sitemapService.configuration();
        final String basePath = parameters.get("basePath", null);
        config.setBasePath(basePath != null && basePath.trim().length() > 0 ? basePath : null);
        config.setCompress(parameters.getBoolean("compress", false));
        Parameters pariticipantsConfig = new DefaultParameters();
        for(SitemapGenerationParticipant participant : sitemapService.participants())
        {
            if(participant.supportsConfiguration())
            {
                DefaultParameters participantConfig = new DefaultParameters(
                    parameters.get("config_" + participant.name()));
                pariticipantsConfig.getChild(participant.name() + ".").set(participantConfig);
            }
        }
        try
        {
            config.setParticipantsConfig(pariticipantsConfig);
        }
        catch(ValueRequiredException e)
        {
            throw new ProcessingException("internal error", e);
        }
        config.update();
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Role role = coralSession.getSecurity().getUniqueRole("cms.administrator");
        return coralSession.getUserSubject().hasRole(role);
    }
}
