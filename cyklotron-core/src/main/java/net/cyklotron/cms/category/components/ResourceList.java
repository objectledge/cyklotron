package net.cyklotron.cms.category.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.cyklotron.cms.CmsComponentData;
import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.category.query.CategoryQueryException;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.util.SiteFilter;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.CoralSession;
import net.labeo.services.table.TableFilter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * This class contains logic of component which displays lists of resources assigned
 * to queried categories.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ResourceList.java,v 1.2 2005-01-18 17:38:23 pablo Exp $
 */
public class ResourceList
extends BaseResourceList
{
	protected CoralSession resourceService;
	protected CategoryQueryService categoryQueryService;
	
	public ResourceList(CoralSession resourceService, CategoryQueryService categoryQueryService)
	{
		this.resourceService = resourceService;
		this.categoryQueryService = categoryQueryService;
	}

    public BaseResourceListConfiguration createConfig(RunData data)
    throws ProcessingException
    {
        return new ResourceListConfiguration();
    }
    
    private CategoryQueryResource categoryQuery;
    private boolean categoryQuerySought = false;

    public String getQuery(RunData data, BaseResourceListConfiguration config)
    throws ProcessingException
    {
		String query = null;
        categoryQuery = getCategoryQueryRes(data, config);
		if (categoryQuery != null)
		{
			query = categoryQuery.getQuery();
		} 
		return query;
    }
    
    public String getTableStateName(RunData data)
    {
        return "net.cyklotron.cms.category.resource_list";
    }

    /* (non-Javadoc)
     * @see net.cyklotron.cms.modules.components.category.BaseResourceList#getResourceClasses(net.labeo.webcore.RunData, net.cyklotron.cms.category.BaseResourceListConfiguration)
     */
    protected String[] getResourceClasses(RunData data, BaseResourceListConfiguration config)
	throws ProcessingException
    {
		String[] resClassNames = null;
		categoryQuery = getCategoryQueryRes(data, config);
    	if (categoryQuery != null)
        {
			resClassNames = categoryQuery.getAcceptedResourceClassNames();
        } 
		if(resClassNames == null)
		{
			resClassNames = new String[0];
		}
		return resClassNames;
    }
    
	/* (non-Javadoc)
	 * @see net.cyklotron.cms.modules.components.category.BaseResourceList#getTableFilters(net.labeo.webcore.RunData)
	 */
	protected TableFilter[] getTableFilters(RunData data, BaseResourceListConfiguration config)
	throws ProcessingException
	{
        CmsData cmsData = CmsData.getCmsData(data);

        List tableFilters = new ArrayList(Arrays.asList(super.getTableFilters(data, config)));
        
		CategoryQueryResource categoryQuery = getCategoryQueryRes(data, config);
		//  - filter out via site list
		if(categoryQuery != null)
		{
			try
			{
				String[] siteNames = categoryQuery.getAcceptedSiteNames();
				if(siteNames.length > 0)
				{
                    tableFilters.add(new SiteFilter(siteNames));
				}
			}
			catch (SiteException e)
			{
				// TODO: ????
			}
		}
		return (TableFilter[]) tableFilters.toArray(new TableFilter[tableFilters.size()]);
	}

    // implementation /////////////////////////////////////////////////////////////////////////////
    
    protected CategoryQueryResource getCategoryQueryRes(
    	RunData data, BaseResourceListConfiguration config)
    	throws ProcessingException
	{
		CmsData cmsData = CmsData.getCmsData(data);
		
		ResourceListConfiguration config2 = (ResourceListConfiguration)config;
		String name = config2.getCategoryQueryName();

        if (categoryQuerySought)
        {
            return categoryQuery;
        }

        // guard from multiple error reporting
        categoryQuerySought = true;

		CmsComponentData compData = cmsData.getComponent();
		if(name.length() == 0 && compData != null)
		{
            compData.error("category query not configured", null);
            return null;
		}

		try
		{
			Resource[] res = resourceService.getStore().getResource(
							   categoryQueryService.getCategoryQueryRoot(cmsData.getSite()),
							   name);
			
			if(res.length == 1)
			{
				categoryQuery = (CategoryQueryResource) res[0];
			}
			else if(res.length == 0 && compData != null)
			{
                compData.error("no category query with name '"+name+"'", null);
			}
			else if(compData != null)
			{
                compData.error("too many category query objects", null);
			}
		}
		catch (CategoryQueryException e)
		{
			if(compData != null)
			{
                compData.error("cannot get category query object", e);
			}
			return null;
		}    

		return categoryQuery;
	}
}
