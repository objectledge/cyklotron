package net.cyklotron.cms.category.query;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.site.SiteResource;

import org.objectledge.coral.util.ResourceSelectionState;
import org.objectledge.pipeline.ProcessingException;

/**
 * Provides default values and state keeping for pool resource editing.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryResourceData.java,v 1.2 2005-01-13 11:46:26 pablo Exp $
 */
public class CategoryQueryResourceData
{
    public static CategoryQueryResourceData getData(RunData data, CategoryQueryResource query)
    {
        String key = getDataKey(query);
        CategoryQueryResourceData currentData = (CategoryQueryResourceData)
            data.getGlobalContext().getAttribute(key);
        if(currentData == null)
        {
            currentData = new CategoryQueryResourceData();
            data.getGlobalContext().setAttribute(key, currentData);
        }
        return currentData;
    }

    public static void removeData(RunData data, CategoryQueryResource query)
    {
        data.getGlobalContext().removeAttribute(getDataKey(query));
    }

    private static String getDataKey(CategoryQueryResource query)
    {
        if(query != null)
        {
            return "cms.category.query.data."+query.getIdString();
        }
        else
        {
            return "cms.category.query.data.NEW";
        }
    }
    
    private String query;
    private String name;
    private String description;
    private ResourceSelectionState categories;
    private ResourceSelectionState resourceClasses;
    private boolean useSimpleQuery;
    private boolean useIdsAsIdentifiers;
	/** Sites selected fo query results filtering.*/
	private Set siteNames = new HashSet();
	
    /** <code>true</code> if the config object was created during current request. */
    private boolean newData;

    public CategoryQueryResourceData()
    {
        newData = true;
        categories = new ResourceSelectionState(null);
		categories.setPrefix("category");
        resourceClasses = new ResourceSelectionState(null);
        resourceClasses.setPrefix("res-class");
        useSimpleQuery = true;
    } 

    public boolean isNew()
    {
        return newData;
    }

    public void init(ResourceService resourceService, CategoryQueryResource queryRes)
        throws ProcessingException
    {
        if(queryRes != null)
        {
            name = queryRes.getName();
            description = queryRes.getDescription();
            useSimpleQuery = queryRes.getSimpleQuery(true);
            useIdsAsIdentifiers = queryRes.getUseIdsAsIdentifiers(true);
            query = queryRes.getQuery();

			siteNames.addAll(Arrays.asList(queryRes.getAcceptedSiteNames()));

            CategoryQueryService categoryQueryService = (CategoryQueryService)resourceService.
                getBroker().getService(CategoryQueryService.SERVICE_NAME);
            IntegrationService integrationService = (IntegrationService)resourceService.
                getBroker().getService(IntegrationService.SERVICE_NAME);
        
            Map map;
            map = categoryQueryService.initCategorySelection( 
                queryRes.getRequiredCategoryPaths(), "required");
            map.putAll(categoryQueryService.initCategorySelection( 
                queryRes.getOptionalCategoryPaths(), "optional"));
            categories.init(map);    
            map = integrationService.initResourceClassSelection( 
                queryRes.getAcceptedResourceClasses(), "accepted");
            resourceClasses.init(map);
        }
        // data was modified
        newData = false;
    }

    public void update(RunData data)
    {
        ParameterContainer params = data.getParameters();

        name = params.get("name").asString("");
        description = params.get("description").asString("");
        query = params.get("categoryQuery").asString("");
        useSimpleQuery = params.get("useSimpleQuery").asBoolean(false);
        useIdsAsIdentifiers = params.get("useIdsAsIdentifiers").asBoolean(true);
        categories.update(params);
        resourceClasses.update(params);
        siteNames.clear();
        siteNames.addAll(Arrays.asList(params.getStrings("acceptedSites")));
        
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
    
    public String getQuery()
    {
        return query;
    }
    
    public boolean useSimpleQuery()
    {
        return useSimpleQuery;
    }
    
    public boolean useIdsAsIdentifiers()
    {
        return useIdsAsIdentifiers;
    }

    public ResourceSelectionState getCategoriesSelection()
    {
        return categories;
    }
    
    public ResourceSelectionState getResourceClassSelection()
    {
        return resourceClasses;
    }
    
    public boolean hasSite(SiteResource site)
    {
    	return siteNames.contains(site.getName());
    }

    public String[] getSiteNames()
    {
    	return (String[]) siteNames.toArray(new String[siteNames.size()]);
    }
}
