package net.cyklotron.cms.category.components;

import net.labeo.util.configuration.ParameterContainer;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Provides default parameter values for resource list configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DocumentResourceListConfiguration.java,v 1.1 2005-01-12 20:45:00 pablo Exp $
 */
public class DocumentResourceListConfiguration
extends ResourceListConfiguration
{
    public static String KEY = "cms.category.document_resource_list.configuration";

	public static ResourceListConfiguration getConfig(RunData data)
	throws ProcessingException
    {
        DocumentResourceListConfiguration currentConfig = (DocumentResourceListConfiguration)
            data.getGlobalContext().getAttribute(KEY);
        if(currentConfig == null)
        {
            currentConfig = new DocumentResourceListConfiguration();
            data.getGlobalContext().setAttribute(KEY, currentConfig);
        }
        return currentConfig;
    }

    public static void removeConfig(RunData data)
    {
        data.getGlobalContext().removeAttribute(KEY);
    }

    public DocumentResourceListConfiguration()
    {
        super();
    }
    
    /** Validity start duration offset */
    private int publicationTimeOffset;

    protected void setParams(ParameterContainer params)
    {
        super.setParams(params);
        publicationTimeOffset = params.get("publicationTimeOffset").asInt(-1);
    }

    // getters /////////////////////////////////////////////////////////////////////////////////////

    public int getPublicationTimeOffset()
    {
        return publicationTimeOffset;
    }
}
