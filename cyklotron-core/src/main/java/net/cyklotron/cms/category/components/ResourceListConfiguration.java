package net.cyklotron.cms.category.components;

import org.objectledge.pipeline.ProcessingException;

/**
 * Provides default parameter values for resource list configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ResourceListConfiguration.java,v 1.2 2005-01-13 11:46:31 pablo Exp $
 */
public class ResourceListConfiguration
extends BaseResourceListConfiguration
{
    public static String KEY = "cms.category.resource_list.configuration";

	public static ResourceListConfiguration getConfig(RunData data)
	throws ProcessingException
    {
        ResourceListConfiguration currentConfig = (ResourceListConfiguration)
            data.getGlobalContext().getAttribute(KEY);
        if(currentConfig == null)
        {
            currentConfig = new ResourceListConfiguration();
            data.getGlobalContext().setAttribute(KEY, currentConfig);
        }
        return currentConfig;
    }

    public static void removeConfig(RunData data)
    {
        data.getGlobalContext().removeAttribute(KEY);
    }

    public ResourceListConfiguration()
    {
        super();
    }
    
    protected String categoryQueryName;

    protected void setParams(ParameterContainer params)
    {
        super.setParams(params);
		categoryQueryName = params.get("categoryQueryName").asString("");
    }

    // getters /////////////////////////////////////////////////////////////////////////////////////

    public String getCategoryQueryName()
    {
        return categoryQueryName;
    }
}
