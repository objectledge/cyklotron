package net.cyklotron.cms.category.query;

import java.util.HashSet;
import java.util.Set;

import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryResourceImpl;
import net.cyklotron.cms.category.CategoryService;

import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.relation.ResourceIdentifierResolver;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;


public class CategoryResolver
    implements ResourceIdentifierResolver
{
    private final CategoryQueryService categoryQueryService;
    
    private final CategoryService categoryService;
    
    private final CoralSession coralSession;
    /**
     * @param CategoryQueryServiceImpl
     */
    public CategoryResolver(CategoryQueryService categoryQueryService,
        CategoryService categoryService, CoralSession coralSession)
    {
        this.categoryQueryService = categoryQueryService;
        this.categoryService = categoryService;
        this.coralSession = coralSession;
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
        CategoryResource[] categories = categoryService.getSubCategories(coralSession, category, true);
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
				Resource res[] = coralSession.getStore().getResourceByPath(identifier);
				if(res.length == 0)
				{
					return null;
				}
				category = (CategoryResource) (res[0]);
			}
			else
			{
				category = CategoryResourceImpl.getCategoryResource(coralSession,
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