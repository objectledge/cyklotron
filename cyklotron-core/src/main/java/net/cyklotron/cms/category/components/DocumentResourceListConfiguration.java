package net.cyklotron.cms.category.components;

import org.objectledge.context.Context;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.HttpContext;

/**
 * Provides default parameter values for resource list configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DocumentResourceListConfiguration.java,v 1.3 2005-01-19 12:33:01 pablo Exp $
 */
public class DocumentResourceListConfiguration
extends ResourceListConfiguration
{
    public static String KEY = "cms.category.document_resource_list.configuration";

    public static ResourceListConfiguration getConfig(Context context)
    throws ProcessingException
    {
        HttpContext httpContext = HttpContext.getHttpContext(context);
        DocumentResourceListConfiguration currentConfig = (DocumentResourceListConfiguration)
            httpContext.getSessionAttribute(KEY);
        if(currentConfig == null)
        {
            currentConfig = new DocumentResourceListConfiguration();
            httpContext.setSessionAttribute(KEY, currentConfig);
        }
        return currentConfig;
    }

    public static void removeConfig(Context context)
    {
        HttpContext httpContext = HttpContext.getHttpContext(context);
        httpContext.removeSessionAttribute(KEY);
    }

    public DocumentResourceListConfiguration()
    {
        super();
    }
    
    /** Validity start duration offset */
    private int publicationTimeOffset;

    protected void setParams(Parameters params)
    {
        super.setParams(params);
        publicationTimeOffset = params.getInt("publicationTimeOffset",-1);
    }

    // getters /////////////////////////////////////////////////////////////////////////////////////

    public int getPublicationTimeOffset()
    {
        return publicationTimeOffset;
    }
}
