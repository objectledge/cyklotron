package net.cyklotron.cms.modules.views.banner;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.banner.BannerService;
import net.cyklotron.cms.banner.BannersResource;
import net.cyklotron.cms.banner.PoolResource;
import net.cyklotron.cms.preferences.PreferencesService;


/**
 *
 *
 */
public class BannerConf
    extends BaseBannerScreen
{
    
    public BannerConf(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        BannerService bannerService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, bannerService);
        
    }
    
    /**
     * {@inheritDoc}
     */
    public String route(String thisViewName)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        TemplatingContext templatingContext =
            TemplatingContext.getTemplatingContext(context);
        CmsData cmsData = getCmsData();
        Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
        String instance = parameters.get("component_instance","");

        httpContext.setSessionAttribute(FROM_COMPONENT,new Boolean(true));
        httpContext.setSessionAttribute(COMPONENT_INSTANCE,instance);
        if(cmsData.getNode() != null)
        {
            httpContext.setSessionAttribute(COMPONENT_NODE, cmsData.getNode().getIdObject());
        }
        long poolId = componentConfig.getLong("pid",-1);
        if(poolId != -1)
        {
            try
            {
                Resource pool = coralSession.getStore().getResource(poolId);
                if(pool instanceof PoolResource)
                {
                    parameters.set("pid",poolId);
                    return "banner.EditPool";
                }
            }
            catch(EntityDoesNotExistException e)
            {
                // something wrong with pid in configuration
                // probably pints to not existing resource or not pool
                // resource, simply process to pool list screen to choose
                // correct pool.
            }
        }
        BannersResource bannersRoot = getBannersRoot(coralSession);
        parameters.set("bsid",bannersRoot.getIdString());
        return "banner.PoolList";
    }
    
    /* (non-Javadoc)
     * @see net.cyklotron.cms.modules.views.BaseCMSScreen#process(org.objectledge.parameters.Parameters, org.objectledge.web.mvc.MVCContext, org.objectledge.templating.TemplatingContext, org.objectledge.web.HttpContext, org.objectledge.i18n.I18nContext, org.objectledge.coral.session.CoralSession)
     */
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession) throws ProcessingException
    {
        // TODO Auto-generated method stub

    }
}
