package net.cyklotron.cms.category.query;

import java.util.HashSet;
import java.util.Set;

import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.relation.ResourceIdentifierResolver;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryResourceImpl;
import net.cyklotron.cms.category.CategoryService;

import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;


public class CategoryResolver
    implements ResourceIdentifierResolver
{
    private final CategoryQueryService categoryQueryService;
    
    private final CategoryService categoryService;
    
    private final CoralSessionFactory coralSessionFactory;

    /**
     * Creates new CategoryResolver instance.
     * 
     * @param categoryQueryService
     * @param categoryService
     * @param coralSession
     */    
    public CategoryResolver(CategoryQueryService categoryQueryService,
        CategoryService categoryService, CoralSessionFactory coralSessionFactory)
    {
        this.categoryQueryService = categoryQueryService;
        this.categoryService = categoryService;
        this.coralSessionFactory = coralSessionFactory;
    }
    
    /** Resolves a given resource identifier to resource ids.
     *
     * @param identifier string representing resource indentifier
     * @return ids of resources
     */
    public LongSet resolveIdentifier(String identifier)
    {
		CategoryResource category = resolveCategoryIdentifier(identifier);
        CategoryResource[] categories = categoryService.getSubCategories(coralSessionFactory.getCurrentSession(), category, true);
        LongSet ids = new LongOpenHashSet(categories.length);
        for (int i = 0; i < categories.length; i++)
        {
            ids.add(categories[i].getId());
        }
        return ids;
    }

	/** Resolves a given category identifier to a category resource.
	 *
	 * @param identifier string representing resource indentifier
	 * @return category resource
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
				Resource res[] = coralSessionFactory.getCurrentSession().getStore().getResourceByPath(identifier);
				if(res.length == 0)
				{
					return null;
				}
				category = (CategoryResource) (res[0]);
			}
			else
			{
				category = CategoryResourceImpl.getCategoryResource(coralSessionFactory.getCurrentSession(),
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