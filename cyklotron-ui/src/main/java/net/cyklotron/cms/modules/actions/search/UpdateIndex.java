package net.cyklotron.cms.modules.actions.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.IndexResourceData;
import net.cyklotron.cms.search.SearchException;

/**
 * An action for index modifications.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdateIndex.java,v 1.2 2005-01-24 10:27:13 pablo Exp $
 */
public class UpdateIndex extends BaseSearchAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();

        IndexResource index = getIndex(data);

        IndexResourceData indexData = IndexResourceData.getData(data, index);
        indexData.update(data);
        
        index.setDescription(indexData.getDescription());

		// check and change public state, if index was private - delete index files
		boolean deleteIndexFiles = false;
        if(indexData.getPublic() && !index.getPublic())
        {
			deleteIndexFiles = true;
        }
		index.setPublic(indexData.getPublic());

		// update the resource
		index.update(subject);

		// set index branches and nodes
		boolean branchesChanged = false;
				
        Set resources = new HashSet(searchService.getIndexedBranches(index));
        List newResources = new ArrayList(indexData.getBranchesSelectionState()
            .getResources(coralSession, "recursive").keySet());
        if(! (resources.containsAll(newResources) && resources.size() == newResources.size()) )
        {
            searchService.setIndexedBranches(index, newResources);
			branchesChanged = true;
        }

        resources = new HashSet(searchService.getIndexedNodes(index));
        newResources = new ArrayList(indexData.getBranchesSelectionState()
            .getResources(coralSession, "local").keySet());
        if(! (resources.containsAll(newResources) && resources.size() == newResources.size()) )
        {
            searchService.setIndexedNodes(index, newResources);
			branchesChanged = true;
        }

        // if branches changed - update branches and delete index files
		if(branchesChanged)
		{
			deleteIndexFiles = true;
			// WARN: VERY IMPORTANT!!
			try
            {
                searchService.updateBranchesAndNodesXRef(subject);
            }
            catch (ValueRequiredException e1)
            {
                templatingContext.put("result","could_not_update_branches_and_nodes_xrefs");
                return;
            }
		}

        if(deleteIndexFiles)
        {
            try
            {
                searchService.getIndexingFacility().createIndexFiles(index);
            }
            catch(SearchException e)
            {
                templatingContext.put("result","could_not_delete_index_files");
                return;
            }
        }

		IndexResourceData.removeData(data, index);
        try
        {
            mvcContext.setView("search,IndexList");
        }
        catch(NotFoundException e)
        {
            throw new ProcessingException("cannot redirect to index list", e);
        }
        templatingContext.put("result","updated_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.search.index.modify");
    }
}
