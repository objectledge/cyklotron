package net.cyklotron.cms.search;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.objectledge.coral.util.ResourceSelectionState;


/**
 * Provides default values and state keeping for index resource editing.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: IndexResourceData.java,v 1.2 2005-01-13 11:46:24 pablo Exp $
 */
public class IndexResourceData
{
    public static IndexResourceData getData(RunData data, IndexResource index)
    {
        String key = getDataKey(index);
        IndexResourceData currentData = (IndexResourceData)
            data.getGlobalContext().getAttribute(key);
        if(currentData == null)
        {
            currentData = new IndexResourceData();
            data.getGlobalContext().setAttribute(key, currentData);
        }
        return currentData;
    }

    public static void removeData(RunData data, IndexResource index)
    {
        data.getGlobalContext().removeAttribute(getDataKey(index));
    }

    private static String getDataKey(IndexResource index)
    {
        if(index != null)
        {
            return "cms.search.index.data."+index.getIdString();
        }
        else
        {
            return "cms.search.index.data.NEW";
        }
    }
    
    private String name;
    private String description;
    private ResourceSelectionState branchesSelection;
	private boolean _public = true;

    /** <code>true</code> if the config object was created during current request. */
    private boolean newData;

    public IndexResourceData()
    {
        newData = true;
        branchesSelection = new ResourceSelectionState("empty_key");
    }

    public boolean isNew()
    {
        return newData;
    }

    public void init(IndexResource index, SearchService searchService)
    {
        if(index != null)
        {
            name = index.getName();
            description = index.getDescription();
			_public = index.getPublic();

            Map initialState = new HashMap();

            List resources = searchService.getIndexedBranches(index);
            if(resources != null)
            {
                for(Iterator i=resources.iterator(); i.hasNext();)
                {
                    initialState.put(i.next(), "recursive");
                }
            }

            resources = searchService.getIndexedNodes(index);
            if(resources != null)
            {
                for(Iterator i=resources.iterator(); i.hasNext();)
                {
                    initialState.put(i.next(), "local");
                }
            }

            branchesSelection.init(initialState);
        }
        // data was modified
        newData = false;
    }

    public void update(RunData data)
    {
        ParameterContainer params = data.getParameters();

        name = params.get("name").asString("");
        description = params.get("description").asString("");
		_public = params.get("public").isDefined();
        
        branchesSelection.update(params);
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

    public ResourceSelectionState getBranchesSelectionState()
    {
        return branchesSelection;
    }

    public boolean getPublic()
    {
        return _public;
    }
}
