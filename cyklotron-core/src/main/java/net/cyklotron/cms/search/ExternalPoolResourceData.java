package net.cyklotron.cms.search;


/**
 * Provides default values and state keeping for external search pool resource editing.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ExternalPoolResourceData.java,v 1.2 2005-01-18 17:38:14 pablo Exp $
 */
public class ExternalPoolResourceData
{
    public static ExternalPoolResourceData getData(RunData data, ExternalPoolResource pool)
    {
        String key = getDataKey(pool);
        ExternalPoolResourceData currentData = (ExternalPoolResourceData)
            data.getGlobalContext().getAttribute(key);
        if(currentData == null)
        {
            currentData = new ExternalPoolResourceData();
            data.getGlobalContext().setAttribute(key, currentData);
        }
        return currentData;
    }

    public static void removeData(RunData data, ExternalPoolResource pool)
    {
        data.getGlobalContext().removeAttribute(getDataKey(pool));
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

    public void update(RunData data)
    {
        ParameterContainer params = data.getParameters();

        name = params.get("name").asString("");
        description = params.get("description").asString("");
        searchHandler = params.get("searchHandler").asString("");
        // TODO: Add URL template checking
        urlTemplate = params.get("urlTemplate").asString("");
        
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
