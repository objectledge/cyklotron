package net.cyklotron.cms.category.components;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.cyklotron.cms.category.CategoryConstants;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.category.query.CategoryResolver;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.labeo.services.resource.CoralSession;
import net.labeo.services.resource.util.ResourceSelectionState;
import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.ParameterContainer;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Provides default parameter values for resource list configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: RelatedResourceListConfiguration.java,v 1.2 2005-01-18 17:38:23 pablo Exp $
 */
public class RelatedResourceListConfiguration
extends BaseResourceListConfiguration
{
	public static String KEY = "cms.category.related_resource_list.configuration";
	
    public static RelatedResourceListConfiguration getConfig(RunData data)
    {
        RelatedResourceListConfiguration currentConfig = (RelatedResourceListConfiguration)
            data.getGlobalContext().getAttribute(KEY);
        if(currentConfig == null)
        {
            currentConfig = new RelatedResourceListConfiguration();
            data.getGlobalContext().setAttribute(KEY, currentConfig);
        }
        return currentConfig;
    }

	public static void removeConfig(RunData data)
	{
		data.getGlobalContext().removeAttribute(KEY);
	}

	public RelatedResourceListConfiguration()
	{
		super();
	}
    
	/** Set of resource classes names accepted in resource list. */
	private Set resourceClassesNames = new HashSet();
	/** Category selection state used during component configuration. */
	private ResourceSelectionState categorySelectionState;
    private String[] activeCategoriesPaths;

	public static String ACTIVE_CATEGORIES_PARAM_KEY = "activeCategories";

	/** Short initialisation used during component preparation. Does not initialise category
	 selection state. */
	public void shortInit(Configuration componentConfig)
	{
		super.shortInit(componentConfig);	
	}

	/** Initialisation used during component configuration. Initialises category selection state. */
	public void init(Configuration componentConfig, CoralSession resourceService)
	{
		super.init(componentConfig, resourceService);

		categorySelectionState =
			new ResourceSelectionState(CategoryConstants.CATEGORY_SELECTION_STATE);
		categorySelectionState.setPrefix("category");
		categorySelectionState.init(buildInitialState(componentConfig, resourceService));
	}

	/** Updates the config after a form post during configuration. */
	public void update(RunData data)
	throws ProcessingException
	{
		ParameterContainer params = data.getParameters();
		categorySelectionState.update(params);

		super.update(data);
	}

    protected void setParams(ParameterContainer params)
    {
        super.setParams(params);
        
		resourceClassesNames.clear();
		resourceClassesNames.addAll(Arrays.asList(params.getStrings("resourceClasses")));

		String[] quotedPaths = params.getStrings(ACTIVE_CATEGORIES_PARAM_KEY);
		activeCategoriesPaths = new String[quotedPaths.length];
		for (int i = 0; i < quotedPaths.length; i++)
		{
			String quotedPath = quotedPaths[i];
			activeCategoriesPaths[i] = quotedPath.substring(1, quotedPath.length()-1);
		}
    }

	// special /////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Checks if a given resource class resource is chosen for this configuration.
	 */
	public boolean hasResourceClass(ResourceClassResource rc)
	{
		return resourceClassesNames.contains(rc.getName());
	}

    // getters /////////////////////////////////////////////////////////////////////////////////////

	public String[] getResourceClasses()
	{
		String[] resClasses = new String[resourceClassesNames.size()];
		return (String[])(resourceClassesNames.toArray(resClasses));
	}

	public ResourceSelectionState getCategorySelectionState()
	{
		return categorySelectionState;
	}
	
    public String[] getActiveCategoriesPaths()
    {
        return activeCategoriesPaths;
    }

    // category selection state initialisation /////////////////////////////////////////////////////

	protected void buildCategorySelectionState(
		Map initialState, String[] paths, String stateName, CoralSession resourceService)
	{
		if(paths != null)
		{
			CategoryQueryService categoryQueryService = (CategoryQueryService)
				resourceService.getBroker().getService(CategoryQueryService.SERVICE_NAME);
			CategoryResolver resolver = categoryQueryService.getCategoryResolver();
			
			for(int i=0; i<paths.length; i++)
			{
				CategoryResource category = resolver.resolveCategoryIdentifier(paths[i]);
				if(category != null)
				{
					initialState.put(category, stateName);
				}
			}
		}
	}

    protected Map buildInitialState(Configuration componentConfig, CoralSession resourceService)
    {
        // activeCategoriesIds is already prepared in setParams called from init()
        Map initialState = new HashMap();
        buildCategorySelectionState(initialState, activeCategoriesPaths, "active", resourceService);
        return initialState;
    }
}
