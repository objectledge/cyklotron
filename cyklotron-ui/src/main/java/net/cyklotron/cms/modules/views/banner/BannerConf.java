package net.cyklotron.cms.modules.views.banner;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Screen;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.banner.BannersResource;
import net.cyklotron.cms.banner.PoolResource;


/**
 *
 *
 */
public class BannerConf
    extends BaseBannerScreen
{
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
                    data.setView("banner,EditPool");
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
        BannersResource bannersRoot = getBannersRoot(data);
        parameters.set("bsid",bannersRoot.getIdString());
        data.setView("banner,PoolList");
        return (Screen)data.getScreenAssembler();
    }
}
