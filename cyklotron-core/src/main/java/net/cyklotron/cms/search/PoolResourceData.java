package net.cyklotron.cms.search;

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
 * @version $Id: PoolResourceData.java,v 1.1 2005-01-12 20:44:36 pablo Exp $
 */
public class PoolResourceData
{
    public static PoolResourceData getData(RunData data, PoolResource pool)
    {
        String key = getDataKey(pool);
        PoolResourceData currentData = (PoolResourceData)
            data.getGlobalContext().getAttribute(key);
        if(currentData == null)
        {
            currentData = new PoolResourceData();
            data.getGlobalContext().setAttribute(key, currentData);
        }
        return currentData;
    }

    public static void removeData(RunData data, PoolResource pool)
    {
        data.getGlobalContext().removeAttribute(getDataKey(pool));
    }

    private static String getDataKey(PoolResource pool)
    {
        if(pool != null)
        {
            return "cms.search.pool.data."+pool.getIdString();
        }
        else
        {
            return "cms.search.pool.data.NEW";
        }
    }
    
    private String name;
    private String description;
    ResourceSelectionState indexesSelection;

    /** <code>true</code> if the config object was created during current request. */
    private boolean newData;

    public PoolResourceData()
    {
        newData = true;
        indexesSelection = new ResourceSelectionState("empty_key");
    } 

    public boolean isNew()
    {
        return newData;
    }

    public void init(PoolResource pool)
    {
        if(pool != null)
        {
            name = pool.getName();
            description = pool.getDescription();

            Map initialState = new HashMap();

            List resources = pool.getIndexes();
            if(resources != null)
            {
                for(Iterator i=resources.iterator(); i.hasNext();)
                {
                    initialState.put(i.next(), "selected");
                }
            }

            indexesSelection.init(initialState);
        }
        // data was modified
        newData = false;
    }

    public void update(RunData data)
    {
        ParameterContainer params = data.getParameters();

        name = params.get("name").asString("");
        description = params.get("description").asString("");
        
        indexesSelection.update(params);
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

    /** Getter for property indexesSelection.
     * @return Value of property indexesSelection.
     *
     */
    public ResourceSelectionState getIndexesSelectionState()
    {
        return indexesSelection;
    }
}
