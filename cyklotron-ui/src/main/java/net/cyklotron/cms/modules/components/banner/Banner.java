package net.cyklotron.cms.modules.components.banner;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.banner.BannerResource;
import net.cyklotron.cms.banner.BannerService;
import net.cyklotron.cms.banner.BannersResource;
import net.cyklotron.cms.banner.BannersResourceImpl;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.skins.SkinService;


/**
 * Banner component.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: Banner.java,v 1.3 2005-04-15 18:44:58 pablo Exp $
 */

public class Banner extends SkinableCMSComponent
{
    private BannerService bannerService;
    
    public Banner(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder,
        BannerService bannerService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder);
        this.bannerService = bannerService;
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) throws ProcessingException
    {
        try
        {
            Parameters componentConfig = getConfiguration();
            BannersResource bannersRoot = null;

            long bannersRootId = componentConfig.getLong("banner.rootId",-1);
            if(bannersRootId != -1)
            {
                bannersRoot = BannersResourceImpl.getBannersResource(coralSession, bannersRootId);
            }
            else
            {
                if(getSite(context) != null)
                {
                    bannersRoot = bannerService.getBannersRoot(coralSession, getSite(context));
                }
            }

            if(bannersRoot != null)
            {
                BannerResource bannerResource = bannerService.getBanner(coralSession, bannersRoot, componentConfig);
                if(bannerResource == null)
                {
                    templatingContext.remove("banner");
                }
                else
                {
                    templatingContext.put("banner",bannerResource);
                }
            }
            else
            {
                componentError(context, "No site selected");
            }
        }
        catch(Exception e)
        {
            componentError(context, "Exception", e);
        }
    }
}
