package net.cyklotron.cms.modules.views.poll;

import java.util.ArrayList;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.PollsResource;
import net.cyklotron.cms.poll.PoolResource;
import net.cyklotron.cms.preferences.PreferencesService;


/**
 *
 *
 */
public class PollLinkConf
    extends BasePollScreen
{
    public PollLinkConf(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, PollService pollService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, pollService);
        
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
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
        long poolId = componentConfig.getLong("pool_id",-1);
        templatingContext.put("pool_id", new Long(poolId));
        
        try
        {
            PollsResource poolsRoot = pollService.getPollsParent(coralSession, cmsData.getSite(),
                pollService.POOLS_ROOT_NAME);
            templatingContext.put("pollsRoot", poolsRoot);
            Resource[] resources = coralSession.getStore().getResource(poolsRoot);
            List pools = new ArrayList();
            for(int i = 0; i < resources.length; i++)
            {
                if(resources[i] instanceof PoolResource)
                {
                    pools.add(resources[i]);
                }
            }
            templatingContext.put("pools", pools);
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occured", e);
        }
    }
}
