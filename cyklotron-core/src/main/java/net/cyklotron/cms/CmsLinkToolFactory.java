/*
 * Created on 2005-01-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.cyklotron.cms;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.context.Context;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConfigurator;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.tools.LinkToolFactoryImpl;

/**
 * @author pablo
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CmsLinkToolFactory extends LinkToolFactoryImpl
{
    private CmsDataFactory cmsDataFactory;
    
    public CmsLinkToolFactory(Configuration config, Context context, 
        WebConfigurator webConfigurator, CmsDataFactory cmsDataFactory)
        throws ConfigurationException
    {
        super(config, context, webConfigurator);
        this.cmsDataFactory = cmsDataFactory;
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getTool()
    {
        HttpContext httpContext = HttpContext.getHttpContext(context);
        MVCContext mvcContext = MVCContext.getMVCContext(context);
        RequestParameters requestParameters = RequestParameters.getRequestParameters(context);
        return new CmsLinkTool(httpContext, mvcContext, requestParameters, linkToolConfiguration, cmsDataFactory,
            context);
    }

}
