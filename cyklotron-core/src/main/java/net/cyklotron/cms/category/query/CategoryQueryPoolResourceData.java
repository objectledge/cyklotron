package net.cyklotron.cms.category.query;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.objectledge.coral.util.ResourceSelectionState;
import org.objectledge.parameters.Parameters;
import org.objectledge.web.HttpContext;


/**
 * Provides default values and state keeping for pool resource editing.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryPoolResourceData.java,v 1.3 2005-01-20 05:45:22 pablo Exp $
 */
public class CategoryQueryPoolResourceData
{
    public static CategoryQueryPoolResourceData getData(HttpContext httpContext, CategoryQueryPoolResource pool)
    {
        String key = getDataKey(pool);
        CategoryQueryPoolResourceData currentData = (CategoryQueryPoolResourceData)
            httpContext.getSessionAttribute(key);
        if(currentData == null)
        {
            currentData = new CategoryQueryPoolResourceData();
            httpContext.setSessionAttribute(key, currentData);
        }
        return currentData;
    }

    public static void removeData(HttpContext httpContext, CategoryQueryPoolResource pool)
    {
        httpContext.removeSessionAttribute(getDataKey(pool));
    }

    private static String getDataKey(CategoryQueryPoolResource pool)
    {
        if(pool != null)
        {
            return "cms.category.query.pool.data."+pool.getIdString();
        }
        else
        {
            return "cms.category.query.pool.data.NEW";
        }
    }
    
    private String name;
    private String description;
    ResourceSelectionState queriesSelection;

    /** <code>true</code> if the config object was created during current request. */
    private boolean newData;

    public CategoryQueryPoolResourceData()
    {
        newData = true;
        queriesSelection = new ResourceSelectionState("empty_key");
    } 

    public boolean isNew()
    {
        return newData;
    }

    public void init(CategoryQueryPoolResource pool)
    {
        if(pool != null)
        {
            name = pool.getName();
            description = pool.getDescription();

            Map initialState = new HashMap();

            List resources = pool.getQueries();
            if(resources != null)
            {
                for(Iterator i=resources.iterator(); i.hasNext();)
                {
                    initialState.put(i.next(), "selected");
                }
            }

            queriesSelection.init(initialState);
        }
        // data was modified
        newData = false;
    }

    public void update(Parameters params)
    {
        name = params.get("name","");
        description = params.get("description","");
        
        queriesSelection.update(params);
        // data was modified
        newData = false;
    }

    // getters /////////////////////////////////////////////////////////////////////////////////////

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public ResourceSelectionState getQueriesSelectionState()
    {
        return queriesSelection;
    }
}
