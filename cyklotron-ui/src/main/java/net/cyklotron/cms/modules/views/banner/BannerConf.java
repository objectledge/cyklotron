package net.cyklotron.cms.modules.views.banner;

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
import net.cyklotron.cms.banner.BannerService;
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
        // TODO Auto-generated constructor stub
    }
    
    
    
    // TODO ??? what to do with route!
    /**
    public Screen route(RunData data)
        throws NotFoundException, ProcessingException
    {
        CmsData cmsData = getCmsData();
        Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
        String instance = parameters.get("component_instance","");

        httpContext.setSessionAttribute(FROM_COMPONENT,new Boolean(true));
        httpContext.setSessionAttribute(COMPONENT_INSTANCE,instance);
        if(cmsData.getNode() != null)
        {
            httpContext.setSessionAttribute(COMPONENT_NODE, cmsData.getNode().getIdObject());
        }
        long poolId = componentConfig.get("pid").asLong(-1);
        if(poolId != -1)
        {
            try
            {
                Resource pool = coralSession.getStore().getResource(poolId);
                if(pool instanceof PoolResource)
                {
                    parameters.set("pid",poolId);
                    mvcContext.setView("banner,EditPool");
                    return (Screen)data.getScreenAssembler();
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
        BannersResource bannersRoot = getBannersRoot(context);
        parameters.set("bsid",bannersRoot.getIdString());
        mvcContext.setView("banner,PoolList");
        return (Screen)data.getScreenAssembler();
    }
    */
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
