package net.cyklotron.cms.category.components;

import org.objectledge.context.Context;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.HttpContext;

/**
 * Provides default parameter values for resource list configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ResourceListConfiguration.java,v 1.3 2005-01-19 12:33:01 pablo Exp $
 */
public class ResourceListConfiguration
extends BaseResourceListConfiguration
{
    public static String KEY = "cms.category.resource_list.configuration";

	public static ResourceListConfiguration getConfig(Context context)
	throws ProcessingException
    {
        HttpContext httpContext = HttpContext.getHttpContext(context);
        ResourceListConfiguration currentConfig = (ResourceListConfiguration)
            httpContext.getSessionAttribute(KEY);
        if(currentConfig == null)
        {
            currentConfig = new ResourceListConfiguration();
            httpContext.setSessionAttribute(KEY, currentConfig);
        }
        return currentConfig;
    }

    public static void removeConfig(Context context)
    {
        HttpContext httpContext = HttpContext.getHttpContext(context);
        httpContext.removeSessionAttribute(KEY);
    }

    public ResourceListConfiguration()
    {
        super();
    }
    
    protected String categoryQueryName;

    protected void setParams(Parameters params)
    {
        super.setParams(params);
		categoryQueryName = params.get("categoryQueryName","");
    }

    // getters /////////////////////////////////////////////////////////////////////////////////////

    public String getCategoryQueryName()
    {
        return categoryQueryName;
    }
}
