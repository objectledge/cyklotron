package net.cyklotron.cms.modules.actions.category.query;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.cyklotron.cms.category.query.CategoryQueryBuilder;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryResourceData;
import net.cyklotron.cms.category.query.CategoryQueryUtil;
import net.cyklotron.cms.site.SiteException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.util.ResourceSelectionState;

/**
 * Category query pool base update and add action.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseCategoryQueryAddUpdate.java,v 1.1 2005-01-24 04:34:35 pablo Exp $
 */
public abstract class BaseCategoryQueryAddUpdate
    extends BaseCategoryQueryAction
{
    public void updateQuery(CategoryQueryResource query, CategoryQueryResourceData queryData,
    	Subject subject)
        throws Exception
    {
		String[] siteNames = queryData.getSiteNames();
		Set acceptedCategoryRoots = new HashSet();
		// add global categories root
		acceptedCategoryRoots.add(categoryService.getCategoryRoot(null));
		if(siteNames.length == 1)
		{
			try
			{
				// add site categories root
				acceptedCategoryRoots.add(
					categoryService.getCategoryRoot(siteService.getSite(siteNames[0])));
			}
			catch (SiteException e)
			{
				throw new Exception("Cannot find selected site", e);
			}
		}
		ResourceSelectionState catsSel = queryData.getCategoriesSelection();
		for (Iterator iter = catsSel.getResources(coralSession).keySet().iterator(); iter.hasNext();)
		{
			Resource category = (Resource) iter.next();
			Resource parent = category;
			while(parent != null)
			{
				if(acceptedCategoryRoots.contains(parent))
				{
					break;
				}
				parent = parent.getParent();
			}
			if(parent == null)
			{
				catsSel.remove(category);
			}                
		}

		query.setAcceptedSiteNames(siteNames);
        query.setDescription(queryData.getDescription());
        query.setSimpleQuery(queryData.useSimpleQuery());
        query.setUseIdsAsIdentifiers(queryData.useIdsAsIdentifiers());
		CategoryQueryBuilder parsedQuery = new CategoryQueryBuilder(coralSession, 
			queryData.getCategoriesSelection(), queryData.useIdsAsIdentifiers());
		if(queryData.useSimpleQuery())
		{
			query.setQuery(parsedQuery.getQuery());
		}
		else
		{
			query.setQuery(queryData.getQuery());
		}
		query.setRequiredCategoryPaths(CategoryQueryUtil.joinCategoryIdentifiers(
			parsedQuery.getRequiredIdentifiers()));
		query.setOptionalCategoryPaths(CategoryQueryUtil.joinCategoryIdentifiers(
			parsedQuery.getOptionalIdentifiers()));
		query.setAcceptedResourceClasses(CategoryQueryUtil.getNames(coralSession,
			queryData.getResourceClassSelection(),"accepted"));
        
        query.update(subject);
    }
}
