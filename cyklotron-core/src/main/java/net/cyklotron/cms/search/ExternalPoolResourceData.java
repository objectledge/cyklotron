package net.cyklotron.cms.search;

import org.objectledge.parameters.Parameters;
import org.objectledge.web.HttpContext;


/**
 * Provides default values and state keeping for external search pool resource editing.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ExternalPoolResourceData.java,v 1.3 2005-01-19 08:22:54 pablo Exp $
 */
public class ExternalPoolResourceData
{
    public static ExternalPoolResourceData getData(HttpContext httpContext, ExternalPoolResource pool)
    {
        String key = getDataKey(pool);
        ExternalPoolResourceData currentData = (ExternalPoolResourceData)
            httpContext.getSessionAttribute(key);
        if(currentData == null)
        {
            currentData = new ExternalPoolResourceData();
            httpContext.setSessionAttribute(key, currentData);
        }
        return currentData;
    }

    public static void removeData(HttpContext httpContext, ExternalPoolResource pool)
    {
        httpContext.removeSessionAttribute(getDataKey(pool));
    }

    private static String getDataKey(ExternalPoolResource pool)
    {
        if(pool != null)
        {
            return "cms.search.external.pool.data."+pool.getIdString();
        }
        else
        {
            return "cms.search.external.pool.data.NEW";
        }
    }
    
    private String name;
    private String description;
    private String searchHandler;
    private String urlTemplate;

    /** <code>true</code> if the config object was created during current request. */
    private boolean newData;

    public ExternalPoolResourceData()
    {
        newData = true;
    } 

    public boolean isNew()
    {
        return newData;
    }

    public void init(ExternalPoolResource pool)
    {
        if(pool != null)
        {
            name = pool.getName();
            searchHandler = pool.getSearchHandler();
            description = pool.getDescription();
            urlTemplate = pool.getUrlTemplate();
        }
        // data was modified
        newData = false;
    }

    public void update(Parameters params)
    {
        name = params.get("name","");
        description = params.get("description","");
        searchHandler = params.get("searchHandler","");
        // TODO: Add URL template checking
        urlTemplate = params.get("urlTemplate","");
        
        // data was modified
        newData = false;
    }

    // getters /////////////////////////////////////////////////////////////////////////////////////

    /** Getter for property name.
     * @return Value of property name.
     *
     */
    public String getName()
    {
        return name;
    }

    /** Getter for property description.
     * @return Value of property description.
     *
     */
    public String getDescription()
    {
        return description;
    }

    /** Getter for property searchHandler.
     * @return Value of property searchHandler.
     *
     */
    public String getSearchHandler()
    {
        return searchHandler;
    }

    /** Getter for property urlTemplate.
     * @return Value of property urlTemplate.
     *
     */
    public String getUrlTemplate()
    {
        return urlTemplate;
    }
}
