package net.cyklotron.cms.search;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.util.ResourceSelectionState;
import org.objectledge.parameters.Parameters;
import org.objectledge.web.HttpContext;


/**
 * Provides default values and state keeping for index resource editing.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: IndexResourceData.java,v 1.3 2005-01-19 08:22:54 pablo Exp $
 */
public class IndexResourceData
{
    public static IndexResourceData getData(HttpContext httpContext, IndexResource index)
    {
        String key = getDataKey(index);
        IndexResourceData currentData = (IndexResourceData)
            httpContext.getSessionAttribute(key);
        if(currentData == null)
        {
            currentData = new IndexResourceData();
            httpContext.setSessionAttribute(key, currentData);
        }
        return currentData;
    }

    public static void removeData(HttpContext httpContext, IndexResource index)
    {
        httpContext.removeSessionAttribute(getDataKey(index));
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

    public void init(CoralSession coralSession, IndexResource index, SearchService searchService)
    {
        if(index != null)
        {
            name = index.getName();
            description = index.getDescription();
			_public = index.getPublic();

            Map initialState = new HashMap();

            List resources = searchService.getIndexedBranches(coralSession, index);
            if(resources != null)
            {
                for(Iterator i=resources.iterator(); i.hasNext();)
                {
                    initialState.put(i.next(), "recursive");
                }
            }

            resources = searchService.getIndexedNodes(coralSession, index);
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

    public void update(Parameters params)
    {
        name = params.get("name","");
        description = params.get("description","");
		_public = params.isDefined("public");
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
