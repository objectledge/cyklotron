/*
 */
package net.cyklotron.cms.modules.hooks;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.web.HttpContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.CmsTool;

/**
 * A post login hook that updates user information in the {@link CmsTool}.
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
public class CmsUserDataHook 
    implements Valve
{
    private CmsDataFactory cmsDataFactory;
    
    public CmsUserDataHook(CmsDataFactory cmsDataFactory)
    {
        this.cmsDataFactory = cmsDataFactory;
    }
    
    /**
     * @inheritDoc{}  
     */
    public void process(Context context) throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        //TODO what for!!!
        //CmsTool cmsTool = (CmsTool)templatingContext.get("cms_tool");
        //cmsTool.setSubject(data.getUserPrincipal());
    }
}
