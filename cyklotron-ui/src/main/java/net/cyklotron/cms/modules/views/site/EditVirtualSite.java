package net.cyklotron.cms.modules.views.site;

import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 *
 */
public class EditVirtualSite
    extends BaseSiteScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        String domain = parameters.get("domain");
        templatingContext.put("domain", domain);

        try
        {
            NavigationNodeResource homePage = getHomePage();
            Resource structure = homePage.getParent();
            templatingContext.put("root", structure);
            
            if(!context.containsKey("default_node_path"))
            {
                templatingContext.put("default_node_path", homePage.getSitePath());
                templatingContext.put("default_node", homePage);
            }
            else
            {
                String defaultNodePath = (String)templatingContext.get("default_node_path");
                Resource[] res = coralSession.getStore().getResourceByPath(structure.getPath()+
                                                                   defaultNodePath);
                if(res.length == 1)
                {
                    templatingContext.put("default_node", res[0]);
                }
            }
        }
        catch(Exception e)
        {
            if(e instanceof ProcessingException)
            {
                throw (ProcessingException)e;
            }
            throw new ProcessingException("failed to lookup site information",e);
        }
    }
}
