package net.cyklotron.cms.modules.hooks;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.web.HttpContext;

import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;

public class CmsDomainHook implements Valve 
{
    /** site service  */
    private SiteService siteService;

    public CmsDomainHook(SiteService siteService)
    {
        this.siteService = siteService;
    }
    
    /**
     * @inheritDoc{}  
     */
    public void process(Context context) throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        if((parameters.get("view","").length() == 0 ||
            parameters.get("view").equals("Default")) &&
           !parameters.isDefined("x") &&
           !parameters.isDefined("node_id") &&
           !parameters.isDefined("site_id"))
        {
            try
            {
                NavigationNodeResource node = siteService.
                    getDefaultNode(coralSession, httpContext.getRequest().getServerName());
                if(node != null)
                {
                    parameters.set("x", node.getIdString());
                    parameters.set("app", "cms");
                    //data.setApplication("cms");
                }
            }
            catch(Exception e)
            {
                throw new ProcessingException("failed to locate start page", e);
            }
        }
    }
}
