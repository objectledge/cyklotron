package net.cyklotron.cms.category.query;

import org.objectledge.context.Context;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.HttpContext;

import net.cyklotron.cms.category.components.DocumentResourceListConfiguration;
import net.cyklotron.cms.category.components.ResourceListConfiguration;

/**
 * Provides default parameter values for resource list configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DocumentResourceListConfiguration.java,v 1.3 2005-01-19 12:33:01 pablo Exp $
 */
public class CategoryQueryPersistentListConfiguration
extends DocumentResourceListConfiguration
{   
    public static String KEY = "cms.category.category_query_persistent_list.configuration";
    
    /** Validity start duration offset */
    private int publicationTimeOffset;
    
    /** domain added to cookie */
    private String domain;
    
    /** define if component site path is added to cookie */
    private boolean pathIncluded;
    
    /** query pool name */
    private String queryPoolName;
    
    public CategoryQueryPersistentListConfiguration()
    {
        super();
        queryPoolName = "";
        domain = "";
        publicationTimeOffset = -1;
        pathIncluded = false;
    }
    
    public CategoryQueryPersistentListConfiguration(Parameters componentConfig)
    {
        shortInit(componentConfig);
        queryPoolName = componentConfig.get("queryPoolName",null);
        domain = componentConfig.get("domain",null);
        publicationTimeOffset = componentConfig.getInt("publicationTimeOffset",-1);
        pathIncluded = componentConfig.getBoolean("pathIncluded",false);
    }
    
    public static ResourceListConfiguration getConfig(Context context)
    throws ProcessingException
    {
        HttpContext httpContext = HttpContext.getHttpContext(context);
        CategoryQueryPersistentListConfiguration currentConfig = (CategoryQueryPersistentListConfiguration)
            httpContext.getSessionAttribute(KEY);
        if(currentConfig == null)
        {
            currentConfig = new CategoryQueryPersistentListConfiguration();
            httpContext.setSessionAttribute(KEY, currentConfig);
        }
        return currentConfig;
    }

    public static void removeConfig(Context context)
    {
        HttpContext httpContext = HttpContext.getHttpContext(context);
        httpContext.removeSessionAttribute(KEY);
    }

    protected void setParams(Parameters params)
    {
        super.setParams(params);
        domain =  params.get("domain",null);
        queryPoolName =  params.get("queryPoolName",null);
        publicationTimeOffset = params.getInt("publicationTimeOffset",-1);
        pathIncluded = params.getBoolean("pathIncluded",false);
    }

    // getters /////////////////////////////////////////////////////////////////////////////////////

    public String getDomain()
    {
        return domain;
    }
    
    public boolean isPathIncluded()
    {
        return pathIncluded;
    }
    
    public String getQueryPoolName()
    {
        return queryPoolName;
    }
    
    public int getPublicationTimeOffset()
    {
        return publicationTimeOffset;
    }
}
