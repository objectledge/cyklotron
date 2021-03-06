package net.cyklotron.cms.modules.actions.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.query.CategoryQueryBuilder;
import net.cyklotron.cms.category.query.CategoryQueryUtil;
import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.IndexResourceData;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.structure.StructureService;

/**
 * An action for index modifications.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdateIndex.java,v 1.6 2007-02-25 14:15:27 pablo Exp $
 */
public class UpdateIndex extends BaseSearchAction
{    
    public UpdateIndex(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SearchService searchService)
    {
        super(logger, structureService, cmsDataFactory, searchService);
        
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        
        Subject subject = coralSession.getUserSubject();

        IndexResource index = getIndex(coralSession, parameters);

        IndexResourceData indexData = IndexResourceData.getData(httpContext, index);
        indexData.update(parameters);
        
        index.setDescription(indexData.getDescription());

		// check and change public state, if index was private - delete index files
		boolean deleteIndexFiles = false;
        if(indexData.getPublic() && !index.getPublic())
        {
			deleteIndexFiles = true;
        }

        index.setPublic(indexData.getPublic());
        
        // set categories
        boolean categoriesChagned = false;
        CategoryQueryBuilder parsedQuery = new CategoryQueryBuilder(coralSession, indexData
            .getCategoriesSelection(), true);
        Set oldOpt = new HashSet(Arrays.asList(CategoryQueryUtil.splitCategoryIdentifiers(index.getOptionalCategoryIdentifiers())));
        Set oldReq = new HashSet(Arrays.asList(CategoryQueryUtil.splitCategoryIdentifiers(index.getRequiredCategoryIdentifiers())));
        Set newOpt = new HashSet(Arrays.asList(parsedQuery.getOptionalIdentifiers()));
        Set newReq = new HashSet(Arrays.asList(parsedQuery.getRequiredIdentifiers()));
        
        if(!(oldOpt.containsAll(newOpt) && oldOpt.size() == newOpt.size()))
        {
            index.setOptionalCategoryIdentifiers(CategoryQueryUtil.joinCategoryIdentifiers(parsedQuery.getOptionalIdentifiers()));
            categoriesChagned = true;
        }
        if(!(oldReq.containsAll(newReq) && oldReq.size() == newReq.size()))
        {
            index.setRequiredCategoryIdentifiers(CategoryQueryUtil.joinCategoryIdentifiers(parsedQuery.getRequiredIdentifiers()));
            categoriesChagned = true;
        }
        
		// update the resource
		index.update();

		// set index branches and nodes
		boolean branchesChanged = false;
				
        Set resources = new HashSet(searchService.getIndexedBranches(coralSession, index));
        List newResources = new ArrayList(indexData.getBranchesSelectionState()
            .getEntities(coralSession, "recursive").keySet());
        if(! (resources.containsAll(newResources) && resources.size() == newResources.size()) )
        {
            searchService.setIndexedBranches(coralSession, index, newResources);
			branchesChanged = true;
        }

        resources = new HashSet(searchService.getIndexedNodes(coralSession, index));
        newResources = new ArrayList(indexData.getBranchesSelectionState()
            .getEntities(coralSession, "local").keySet());
        if(! (resources.containsAll(newResources) && resources.size() == newResources.size()) )
        {
            searchService.setIndexedNodes(coralSession, index, newResources);
			branchesChanged = true;
        }

        // if branches changed - update branches and delete index files
		if(branchesChanged || categoriesChagned)
		{
			deleteIndexFiles = true;
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

		IndexResourceData.removeData(httpContext, index);
        mvcContext.setView("search.IndexList");
        templatingContext.put("result","updated_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("search"))
        {
            logger.debug("Application 'search' not enabled in site");
            return false;
        }
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.search.index.modify");
    }
}
