package net.cyklotron.cms.modules.actions.search;

import java.util.ArrayList;
import java.util.List;

import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.IndexResourceData;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.site.SiteResource;

/**
 * Action for adding indexes.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AddIndex.java,v 1.1 2005-01-24 04:34:07 pablo Exp $
 */
public class AddIndex
    extends BaseSearchAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();

        IndexResourceData indexData = IndexResourceData.getData(data, null);
        indexData.update(data);
        
        if(indexData.getName().equals(""))
        {
            templatingContext.put("result", "name_empty");
            return;
        }

        SiteResource site = getSite(context);
        try
        {
            if(coralSession.getStore()
                .getResource(searchService.getIndexesRoot(site), indexData.getName()).length > 0)
            {
                templatingContext.put("result","cannot_add_indexes_with_the_same_name");
                return;
            }

            IndexResource index = searchService.createIndex(site, indexData.getName(), subject);
            index.setDescription(indexData.getDescription());
            index.setPublic(indexData.getPublic());

			index.update(subject);

			// setup branches
            List resources = new ArrayList(indexData.getBranchesSelectionState()
                .getResources(coralSession, "recursive").keySet());
            searchService.setIndexedBranches(index, resources);
            
            resources = new ArrayList(indexData.getBranchesSelectionState()
                .getResources(coralSession, "local").keySet());
            searchService.setIndexedNodes(index, resources);
            
            // WARN: VERY IMPORTANT!!
            searchService.updateBranchesAndNodesXRef(subject);
        }
        catch(SearchException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",StringUtils.stackTrace(e));
            log.error("problem adding an index for site '"+site.getName()+"'", e);
            return;
        }
        catch (ValueRequiredException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",StringUtils.stackTrace(e));
            log.error("problem adding an index for site '"+site.getName()+"'", e);
            return;
        }

		IndexResourceData.removeData(data, null);
        try
        {
            data.setView("search,IndexList");
        }
        catch(NotFoundException e)
        {
            throw new ProcessingException("cannot redirect to index list", e);
        }
        templatingContext.put("result","added_successfully");
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.search.index.add");
    }
}
