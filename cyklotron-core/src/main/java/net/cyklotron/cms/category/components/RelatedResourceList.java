package net.cyklotron.cms.category.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.category.query.CategoryResolver;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.util.SiteFilter;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableFilter;

/**
 * This component displays lists of resources assigned to categories assigned to current document
 * node. Category list is filtered upon this component's configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: RelatedResourceList.java,v 1.8 2005-05-17 06:19:58 zwierzem Exp $
 */
public class RelatedResourceList
extends BaseResourceList
{
	protected CategoryQueryService categoryQueryService;
	
    protected CategoryService categoryService;
    
    protected NavigationNodeResource contextNode;
	
	public RelatedResourceList(Context context, IntegrationService integrationService,
        CmsDataFactory cmsDataFactory,  CategoryQueryService categoryQueryService,
        CategoryService categoryService, NavigationNodeResource contextNode)
    {
        super(context,integrationService, cmsDataFactory);
        this.categoryService = categoryService;
        this.categoryQueryService = categoryQueryService;
        this.contextNode = contextNode;
    }
    
	
    public BaseResourceListConfiguration createConfig()
    {
        return new RelatedResourceListConfiguration();
    }

	protected String[] getResourceClasses(CoralSession coralSession, BaseResourceListConfiguration config)
	throws ProcessingException
	{
		RelatedResourceListConfiguration config2 = (RelatedResourceListConfiguration)config;
		return config2.getResourceClasses();
	}

    protected TableFilter<Resource>[] getTableFilters(CoralSession coralSession, BaseResourceListConfiguration config)
                    throws ProcessingException
    {
        RelatedResourceListConfiguration config2 = (RelatedResourceListConfiguration)config;
		List<TableFilter<Resource>> filters = new ArrayList<TableFilter<Resource>>();
        if(contextNode != null)
        {
            filters.add(new RejectResourceFilter(contextNode));
            if(config2.isSiteFilterEnabled())
            {
                filters.add(new SiteFilter(new SiteResource[] { contextNode.getSite() }));
            }
        }
        return filters.toArray(new TableFilter[filters.size()]);
    }

    public String getQuery(CoralSession coralSession, BaseResourceListConfiguration config)
    throws ProcessingException
    {
        // get categories accepted in query
        Set<CategoryResource> acceptedCategories = new HashSet<CategoryResource>();

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
	                categoryService.getSubCategories(coralSession, category, false);
	            for(int j = 0; j < subCategories.length; j++)
	            {
	                acceptedCategories.add(subCategories[j]);
	            }
			}
        }

        CmsData cmsData = cmsDataFactory.getCmsData(context);
		
        if(contextNode != null)
        {
            CategoryResource[] categories = categoryService.getCategories(coralSession, contextNode, false);
        
            StringBuilder buf = new StringBuilder(100);
            for(int i = 0; i < categories.length; i++)
            {
                CategoryResource category = categories[i];
                if(acceptedCategories.contains(category))
                {
                    if(i > 0 && buf.length() > 0)
                    {
                        buf.append(" + ");
                    }
                    buf.append("MAP('category.References'){ RES(");
                    buf.append(category.getIdString());
                    buf.append(") }");
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
        implements TableFilter<Resource>
    {
        Resource rejectedResource;
        
        RejectResourceFilter(Resource rejectedResource)
        {
            this.rejectedResource = rejectedResource;
        }
        
        public boolean accept(Resource o)
        {
            return rejectedResource.getId() != o.getId();
        }
    }
}
