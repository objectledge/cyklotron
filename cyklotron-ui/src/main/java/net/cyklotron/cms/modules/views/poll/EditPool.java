package net.cyklotron.cms.modules.views.poll;

import java.util.Arrays;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.poll.PollsResource;
import net.cyklotron.cms.poll.PoolResource;
import net.cyklotron.cms.poll.PoolResourceImpl;



/**
 *
 */
public class EditPool
    extends BasePollScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        Boolean fromComponent = (Boolean)httpContext.getSessionAttribute(FROM_COMPONENT);
        if(fromComponent != null && fromComponent.booleanValue())
        {
            templatingContext.put("from_component",fromComponent);
            templatingContext.put("component_node",(Long)httpContext.getSessionAttribute(COMPONENT_NODE));
            templatingContext.put("component_instance",(String)httpContext.getSessionAttribute(COMPONENT_INSTANCE));
        }

        int poolId = parameters.getInt("pool_id", -1);
        if(poolId == -1)
        {
            throw new ProcessingException("Pool id not found");
        }
        try
        {
            PoolResource pool = PoolResourceImpl.getPoolResource(coralSession, poolId);
            templatingContext.put("pool",pool);
            PollsResource pollsRoot = (PollsResource)pool.getParent();
            templatingContext.put("pollsRoot",pollsRoot);

            Resource[] pollResources = pollsRoot.getBindings().get(pool);
            templatingContext.put("polls",Arrays.asList(pollResources));
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("PollException: ",e);
            return;
        }
    }
}
