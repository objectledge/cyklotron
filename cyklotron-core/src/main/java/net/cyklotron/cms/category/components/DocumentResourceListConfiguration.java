package net.cyklotron.cms.category.components;

import org.objectledge.pipeline.ProcessingException;

/**
 * Provides default parameter values for resource list configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DocumentResourceListConfiguration.java,v 1.2 2005-01-13 11:46:31 pablo Exp $
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
