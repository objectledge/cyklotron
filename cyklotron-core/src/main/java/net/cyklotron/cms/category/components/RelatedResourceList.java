package net.cyklotron.cms.category.components;

import java.util.HashSet;
import java.util.Set;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.category.query.CategoryResolver;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.util.SiteFilter;
import net.labeo.services.resource.Resource;
import net.labeo.services.table.TableFilter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * This component displays lists of resources assigned to categories assigned to current document
 * node. Category list is filtered upon this component's configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: RelatedResourceList.java,v 1.1 2005-01-12 20:45:00 pablo Exp $
 */
public class RelatedResourceList
extends BaseResourceList
{
	protected CategoryQueryService categoryQueryService;
	protected CategoryService categoryService;
	
	public RelatedResourceList(
		CategoryQueryService categoryQueryService,
		CategoryService categoryService)
	{
		this.categoryQueryService = categoryQueryService;
		this.categoryService = categoryService;
	}
	
    public BaseResourceListConfiguration createConfig(RunData data)
    {
        return new RelatedResourceListConfiguration();
    }
    
    public String getTableStateName(RunData data)
    {
        return "net.cyklotron.cms.category.related_resource_list";
    }

	/* (non-Javadoc)
	 * @see net.cyklotron.cms.modules.components.category.BaseResourceList#getResourceClasses(net.labeo.webcore.RunData, net.cyklotron.cms.category.BaseResourceListConfiguration)
	 */
	protected String[] getResourceClasses(RunData data, BaseResourceListConfiguration config)
	throws ProcessingException
	{
		RelatedResourceListConfiguration config2 = (RelatedResourceListConfiguration)config;
		return config2.getResourceClasses();
	}

    protected TableFilter[] getTableFilters(RunData data, BaseResourceListConfiguration config)
    throws ProcessingException
    {
        CmsData cmsData = CmsData.getCmsData(data);
        NavigationNodeResource node = cmsData.getNode();
		SiteFilter siteFilter = new SiteFilter(new SiteResource[] { cmsData.getSite() });
        if(node != null)
        {
            TableFilter[] filters = new TableFilter[2];
            filters[0] = new RejectResourceFilter(node);
            filters[1] = siteFilter;
            return filters;
        }
        else
        {
            return new TableFilter[] { siteFilter };
        }
    }

    public String getQuery(RunData data, BaseResourceListConfiguration config)
    throws ProcessingException
    {
        // get categories accepted in query
        Set acceptedCategories = new HashSet();

		CategoryResolver resolver = categoryQueryService.getCategoryResolver();
			
        RelatedResourceListConfiguration config2 = (RelatedResourceListConfiguration)config;
        String[] activeCategoriesPaths = config2.getActiveCategoriesPaths();
        for(int i = 0; i < activeCategoriesPaths.length; i++)
        {
            // get accepted category
			CategoryResource category =
				resolver.resolveCategoryIdentifier(activeCategoriesPaths[i]);
			if(category != null)
			{
				acceptedCategories.add(category);

	            // get subcategories of this category - they are also accepted
	            //      (they imply the parent category)
	            CategoryResource[] subCategories =
	                categoryService.getSubCategories(category, false);
	            for(int j = 0; j < subCategories.length; j++)
	            {
	                acceptedCategories.add(subCategories[j]);
	            }
			}
        }

        CmsData cmsData = CmsData.getCmsData(data);
		NavigationNodeResource node = cmsData.getNode();
        if(node != null)
        {
            CategoryResource[] categories = categoryService.getCategories(node, false);
        
            StringBuffer buf = new StringBuffer(100);
            for(int i = 0; i < categories.length; i++)
            {
                CategoryResource category = categories[i];
                if(acceptedCategories.contains(category))
                {
                    if(i > 0 && buf.length() > 0)
                    {
                        buf.append(" OR ");
                    }
                    buf.append(category.getIdString());
                }
            }
        
            if(buf.length() > 0)
            {
                return buf.append(';').toString();
            }
            else
            {
                return null;
            }
        }
        else
        {
        	if(cmsData.getComponent() != null)
        	{
				cmsData.getComponent().error("No navigation node selected", null);
        	}
            return null;
        }
    }
    
    // implementation /////////////////////////////////////////////////////////////////////////////
    
    private class RejectResourceFilter
    implements TableFilter
    {
        Resource rejectedResource;
        
        RejectResourceFilter(Resource rejectedResource)
        {
            this.rejectedResource = rejectedResource;
        }
        
        public boolean accept(Object o)
        {
            if(!(o instanceof Resource))
            {
                return false;
            }
            
            Resource r = (Resource)o;
            return rejectedResource.getId() != r.getId();
        }
    }
}
