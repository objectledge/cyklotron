package net.cyklotron.cms.category.components;

import net.labeo.util.configuration.ParameterContainer;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Provides default parameter values for resource list configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ResourceListConfiguration.java,v 1.1 2005-01-12 20:45:00 pablo Exp $
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
