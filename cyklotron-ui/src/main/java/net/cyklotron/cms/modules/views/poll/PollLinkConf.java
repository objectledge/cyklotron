package net.cyklotron.cms.modules.views.poll;

import java.util.ArrayList;
import java.util.List;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.poll.PollsResource;
import net.cyklotron.cms.poll.PoolResource;
import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;


/**
 *
 *
 */
public class PollLinkConf
    extends BasePollScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {    	
        CmsData cmsData = getCmsData();

        Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
		Resource parent = cmsData.getHomePage().getParent();
		String path = componentConfig.get("pollNodePath",null);
		if(path != null)
		{
			Resource[] nodes = coralSession.getStore().getResourceByPath(parent, path);
			if(nodes.length > 1)
			{
				// ???
				throw new ProcessingException("too many print nodes with the same path");
			}
			templatingContext.put("poll_node", nodes[0]);
		}
        String instance = parameters.get("component_instance","");

        httpContext.setSessionAttribute(FROM_COMPONENT,new Boolean(true));
        httpContext.setSessionAttribute(COMPONENT_INSTANCE,instance);
        if(cmsData.getNode() != null)
        {
            httpContext.setSessionAttribute(COMPONENT_NODE, cmsData.getNode().getIdObject());
        }
        long poolId = componentConfig.get("pool_id").asLong(-1);
        templatingContext.put("pool_id", new Long(poolId));
        
        try
        {
            PollsResource pollsRoot = pollService.getPollsRoot(cmsData.getSite());
            templatingContext.put("pollsRoot",pollsRoot);
            Resource[] resources = coralSession.getStore().getResource(pollsRoot);
            List pools = new ArrayList();
            for(int i = 0; i < resources.length; i++)
            {
                if(resources[i] instanceof PoolResource)
                {
                    pools.add(resources[i]);
                }
            }
            templatingContext.put("pools",pools);
        	//
        }
        catch(Exception e)
        {
        	throw new ProcessingException("Exception occured", e);
        }
    }
}
