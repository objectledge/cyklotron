package net.cyklotron.cms.category.query;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.labeo.services.resource.util.ResourceSelectionState;
import net.labeo.util.configuration.ParameterContainer;
import net.labeo.webcore.RunData;


/**
 * Provides default values and state keeping for pool resource editing.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryPoolResourceData.java,v 1.1 2005-01-12 20:44:47 pablo Exp $
 */
public class CategoryQueryPoolResourceData
{
    public static CategoryQueryPoolResourceData getData(RunData data, CategoryQueryPoolResource pool)
    {
        String key = getDataKey(pool);
        CategoryQueryPoolResourceData currentData = (CategoryQueryPoolResourceData)
            data.getGlobalContext().getAttribute(key);
        if(currentData == null)
        {
            currentData = new CategoryQueryPoolResourceData();
            data.getGlobalContext().setAttribute(key, currentData);
        }
        return currentData;
    }

    public static void removeData(RunData data, CategoryQueryPoolResource pool)
    {
        data.getGlobalContext().removeAttribute(getDataKey(pool));
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

    public void update(RunData data)
    {
        ParameterContainer params = data.getParameters();

        name = params.get("name").asString("");
        description = params.get("description").asString("");
        
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
