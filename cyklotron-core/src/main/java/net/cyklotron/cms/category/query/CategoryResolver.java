package net.cyklotron.cms.category.query;

import java.util.HashSet;
import java.util.Set;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.generic.CrossReference;

import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryResourceImpl;
import net.cyklotron.cms.category.CategoryService;


public class CategoryResolver
    implements CrossReference.ResourceIdentifierResolver
{
    private final CategoryQueryService categoryQueryService;
    
    private final ResourceService resourceService;
    
    private final CategoryService categoryService;
    
    /**
     * @param CategoryQueryServiceImpl
     */
    public CategoryResolver(CategoryQueryService impl)
    {
        this.categoryQueryService = impl;
        this.resourceService = (ResourceService)impl.getBroker().
            getService(ResourceService.SERVICE_NAME);
        this.categoryService = (CategoryService)impl.getBroker().
            getService(CategoryService.SERVICE_NAME);
    }
    
    /** Resolves a given resource identifier to resource ids.
     *
     * @param identifier string representing resource indentifier
     * @return ids of resources
     * @throws Exception if there was something wrong while resolving an indentifier
     *
     */
    public Set resolveIdentifier(String identifier)
    {
		CategoryResource category = resolveCategoryIdentifier(identifier);
        CategoryResource[] categories = categoryService.getSubCategories(category, true);
        Set ids = new HashSet(categories.length);
        for (int i = 0; i < categories.length; i++)
        {
            ids.add(categories[i].getIdObject());
        }
        return ids;
    }

	/** Resolves a given resource identifier to a single resource id.
	 *
	 * @param identifier string representing resource indentifier
	 * @return id of a resource
	 * @throws Exception if there was something wrong while resolving an indentifier
	 */
	public long resolveSingleIdentifier(String identifier)
	{
		CategoryResource category = resolveCategoryIdentifier(identifier);
		return category.getId();
	}

	/** Resolves a given category identifier to a category resource.
	 *
	 * @param identifier string representing resource indentifier
	 * @return category resource
	 * @throws Exception if there was something wrong while resolving an indentifier
	 */
	public CategoryResource resolveCategoryIdentifier(String identifier)
	{
		if(identifier == null || identifier.length() == 0)
		{
			return null;
		}
    	
		CategoryResource category;
		try
		{
			if(identifier.charAt(0) == '/')
			{
				Resource res[] = resourceService.getStore().getResourceByPath(identifier);
				if(res.length == 0)
				{
					return null;
				}
				category = (CategoryResource) (res[0]);
			}
			else
			{
				category = CategoryResourceImpl.getCategoryResource(resourceService,
					Long.parseLong(identifier));
			}
		}
		catch (NumberFormatException e)
		{
			return null;
		}
		catch(EntityDoesNotExistException e)
		{
			return null;
		}
		return category;
	}
}