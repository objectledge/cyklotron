package net.cyklotron.cms.modules.views.banner;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.banner.PoolResource;
import net.cyklotron.cms.banner.PoolResourceImpl;

/**
 *
 */
public class EditPool
    extends BaseBannerScreen
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

        int pid = parameters.getInt("pid", -1);
        if(pid == -1)
        {
            throw new ProcessingException("Pool id not found");
        }
        try
        {
            PoolResource pool = PoolResourceImpl.getPoolResource(coralSession, pid);
            templatingContext.put("pool",pool);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("LinkException: ",e);
            return;
        }
    }
}
