package net.cyklotron.cms.modules.views.link;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Screen;

import net.cyklotron.cms.link.LinkRootResource;
import net.cyklotron.cms.link.PoolResource;


/**
 *
 */
public class LinksConf
    extends BaseLinkScreen
{

    public Screen route(RunData data)
        throws NotFoundException, ProcessingException
    {
        String instance = parameters.get("component_instance","");
        long nodeId = parameters.getLong("node_id", -1);
        httpContext.setSessionAttribute(FROM_COMPONENT,new Boolean(true));
        httpContext.setSessionAttribute(COMPONENT_INSTANCE,instance);
        if(nodeId != -1)
        {
            httpContext.setSessionAttribute(COMPONENT_NODE,new Long(nodeId));
        }
        try
        {
            Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
            long pid = componentConfig.get("pid").asLong(-1);
            if(pid != -1)
            {
                try
                {
                    Resource pool = coralSession.getStore().getResource(pid);
                    if(pool instanceof PoolResource)
                    {
                        parameters.set("pid",pid);
                        mvcContext.setView("link,EditPool");
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
            LinkRootResource linkRoot = getLinkRoot(data);
            parameters.set("lsid",linkRoot.getIdString());
            mvcContext.setView("link,PoolList");
            return (Screen)data.getScreenAssembler();
        }
        catch(Exception e)
        {
            throw new ProcessingException("Link exception", e);
        }
    }
}

