package net.cyklotron.cms.category.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.cyklotron.cms.category.CategoryException;
import net.cyklotron.cms.category.CategoryMapResource;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryResourceImpl;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.site.SiteResource;
import net.labeo.services.BaseService;
import net.labeo.services.InitializationError;
import net.labeo.services.logging.LoggingFacility;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.CircularDependencyException;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.EntityInUseException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceClass;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.resource.event.ResourceDeletionListener;
import net.labeo.services.resource.generic.CrossReference;

/**
 * Implementation of Category Service.
 *
 * @author <a href="mailto:publo@ngo.pl">Pawel Potempski</a>.
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: CategoryServiceImpl.java,v 1.1 2005-01-12 20:45:16 pablo Exp $
 */
public class CategoryServiceImpl extends BaseService implements CategoryService, ResourceDeletionListener
{
    /** logging facility */
    private LoggingFacility log;

    /** integration service */
    private IntegrationService integrationService;

    /** resource service */
    private ResourceService resourceService;

    /** System category root and category map resource containing resource and resource class cross
     * references.
     */
    private CategoryMapResource categoryMap;

    /** system root subject */
    private Subject rootSubject;

    /**
     * Initializes the service.
     */
    public void start()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(LOGGING_FACILITY);
        resourceService = (ResourceService)broker.getService(ResourceService.SERVICE_NAME);
        integrationService = (IntegrationService)broker.getService(IntegrationService.SERVICE_NAME);

        Resource[] res = resourceService.getStore().getResourceByPath(SYSTEM_CATEGORIES);
        if (res.length == 0)
        {
            throw new InitializationError("failed to lookup system-wide category root " + SYSTEM_CATEGORIES);
        }
        if (res.length > 1)
        {
            throw new InitializationError("ambigous pathname " + SYSTEM_CATEGORIES);
        }
        categoryMap = (CategoryMapResource)res[0];
        // setup deletion listener
        try
        {
            rootSubject = resourceService.getSecurity().getSubject(Subject.ROOT);
        }
        catch (EntityDoesNotExistException e)
        {
            throw new InitializationError("cannot get root subject", e);
        }
        resourceService.getEvent().addResourceDeletionListener(this, null);
    }

	/**
	 * Returns the resource which contains category cross references.
	 */
	public CategoryMapResource getCategoryMap()
	{
		return categoryMap;
	}

    /**
     * Returns the root of category tree.
     *
     * @param site the site to return category tree for, or <code>null</code>
     *        for system-wide categories.
     */
    public Resource getCategoryRoot(SiteResource site) throws CategoryException
    {
        if (site == null)
        {
            return categoryMap;
        }

        Resource[] res = resourceService.getStore().getResource(site, SITE_CATEGORIES);
        if (res.length == 0)
        {
            throw new CategoryException("failed to lookup category root for site " + site.getName());
        }
        if (res.length > 1)
        {
            throw new InitializationError("multiple category roots for site " + site.getName());
        }
        return res[0];
    }

    /**
     * Returns a list of super categories of a specified category.
     *
     * @param category the category resource.
     * @param includeSelf <code>true</code> to include <code>category</code>
     *        in the list.
     * @return the list of categories.
     */
    public CategoryResource[] getImpliedCategories(CategoryResource category, boolean includeSelf)
    {
        ArrayList target = new ArrayList();
        if (includeSelf)
        {
            target.add(category);
        }
        Resource parent = category.getParent();
        while (parent instanceof CategoryResource)
        {
            target.add(parent);
            parent = parent.getParent();
        }
        CategoryResource[] result = new CategoryResource[target.size()];
        target.toArray(result);
        return result;
    }

    /**
     * Returns a list of sub categories of a specified category.
     *
     * @ deprecated WARN: this is used only in groups
     * @param category the category resource.
     * @param includeSelf <code>true</code> to include <code>category</code>
     *        in the list.
     * @return the list of categories.
     */
    public CategoryResource[] getSubCategories(CategoryResource category, boolean includeSelf)
    {
    	if(category == null)
    	{
    		return new CategoryResource[0];
    	}

        List target = new ArrayList();
        if (includeSelf)
        {
            target.add(category);
        }
        collectSubCategories(category, target);
        CategoryResource[] result = new CategoryResource[target.size()];
        target.toArray(result);
        return result;
    }

    private void collectSubCategories(Resource resource, List target)
    {
        Resource[] categories = resourceService.getStore().getResource(resource);
        for (int i = 0; i < categories.length; i++)
        {
            target.add(categories[i]);
            collectSubCategories(categories[i], target);
        }
    }

    /**
     * Returns all categories the resource belongs to.
     *
     * @param resource the resource.
     * @param includeImplied <code>false</code> to list categories assigned
     *        directly, <code>true</code> to include super-categories also.
     */
    public CategoryResource[] getCategories(Resource resource, boolean includeImplied)
    {
        CrossReference reference = categoryMap.getReferences();

        Resource[] refCategories = reference.getInv(resource);
        Set target = new HashSet();
        target.addAll(Arrays.asList(refCategories));
        if (includeImplied)
        {
            for (int i = 0; i < refCategories.length; i++)
            {
                CategoryResource category = (CategoryResource)refCategories[i];
                CategoryResource[] categories = getImpliedCategories(category, false);
                for (int j = 0; j < categories.length; j++)
                {
                    target.add(categories[j]);
                }
            }
        }
        CategoryResource[] result = new CategoryResource[target.size()];
        target.toArray(result);
        return result;
    }

    /**
     * Returns all resources belonging to a category.
     *
     * <p>Note that the meaning of <code>includeImplied</code> parameter of
     * this method is quite different than in the {@link
     * #getImpliedCategories(CategoryResource,boolean)}, and in general case
     * setting this one to <code>true</code> makes less senese.</p>
     *
     * @param category the category.
     * @param includeImplied <code>false</code> to return only resources
     *        assigned directly to the given category, <code>true</code> to
     *        inclue resources assigned directly to sub-categories of the
     *        given category.
     */
    public Resource[] getResources(CategoryResource category, boolean includeImplied) throws CategoryException
    {
        CrossReference refs = categoryMap.getReferences();
        if (!includeImplied)
        {
            return refs.get(category);
        }
        else
        {
            Set target = new HashSet();
            List categories = new ArrayList();
            getSubCategories(category, categories);
            for (int i = 0; i < categories.size(); i++)
            {
                CategoryResource cat = (CategoryResource)categories.get(i);
                Resource[] resources = refs.get(cat);
                for (int j = 0; j < resources.length; j++)
                {
                    target.add(resources[j]);
                }
            }
            Resource[] result = new Resource[target.size()];
            target.toArray(result);
            return result;
        }
    }

    /**
     * Collects subcategories of a category in a list.
     *
     * @param resource the category.
     * @param list the list to store results to.
     */
    private void getSubCategories(Resource resource, List list)
    {
        list.add(resource);
        Resource[] children = resourceService.getStore().getResource(resource);
        for (int i = 0; i < children.length; i++)
        {
            getSubCategories(children[i], list);
        }
    }

    /**
     * Adds a new category to the system.
     *
     * @param name the category name.
     * @param description the category description.
     * @param parent the parent category or category tree root.
     * @param subject the creator.
     * @return category resource.
     */
    public CategoryResource addCategory(String name, String description, Resource parent, Subject subject, ResourceClassResource[] resourceClasses)
        throws CategoryException
    {
        CategoryResource category = null;
        try
        {
            category = CategoryResourceImpl.createCategoryResource(resourceService, name, parent, subject);
            category.setDescription(description);
            category.update(subject);
        }
        catch (ValueRequiredException e)
        {
            throw new CategoryException("Value required exception", e);
        }

        setCategoryResourceClasses(category, subject, resourceClasses);

        return category;
    }

    /**
     * Deletes a category from the system.
     *
     * @param category the category to delete.
     * @param subject the subject performing delete action.
     */
    public void deleteCategory(CategoryResource category, Subject subject) throws CategoryException
    {
        CategoryResource[] cats = getSubCategories(category, false);

        if (cats.length > 0)
        {
            throw new CategoryException("Cannot remove categories with children");
        }

        // remove resource categorization references
        try
        {
            CrossReference refs = categoryMap.getReferences();
            refs.remove(category);
            categoryMap.setReferences(refs);
            categoryMap.update(subject);
        }
        catch (ValueRequiredException e)
        {
            throw new CategoryException("Cannot set references", e);
        }

        // remove resource class references
        setCategoryResourceClasses(category, subject, new ResourceClassResource[0]);

        // remove resource
        try
        {
            resourceService.getStore().deleteResource(category);
        }
        catch (EntityInUseException e)
        {
            throw new CategoryException("Entity in use exception", e);
        }
    }

    /**
     * Update a category.
     *
     * @param category the category being updated.
     * @param name new category name.
     * @param description new category description.
     * @param parent new category parent (can be another category, or category
     *        root)
     * @param subject the subject performing update action.
     */
    public void updateCategory(
        CategoryResource category,
        String name,
        String description,
        Resource parent,
        Subject subject,
        ResourceClassResource[] resourceClasses)
        throws CategoryException
    {
        if (!category.getName().equals(name))
        {
            resourceService.getStore().setName(category, name);
        }
        if (!description.equals(category.getDescription()))
        {
            category.setDescription(description);
            category.update(subject);
        }

        setCategoryResourceClasses(category, subject, resourceClasses);

        if (!parent.equals(category.getParent()))
        {
            try
            {
                resourceService.getStore().setParent(category, parent);
            }
            catch (CircularDependencyException e)
            {
                throw new CategoryException("circular dependedncy", e);
            }
        }
    }

    private void setCategoryResourceClasses(CategoryResource category, Subject subject, ResourceClassResource[] resourceClasses) throws CategoryException
    {
        try
        {
            CrossReference refs = categoryMap.getResourceTypeReferences();
            refs.remove(category);
            for (int i = 0; i < resourceClasses.length; i++)
            {
                if (resourceClasses[i].getCategorizable())
                {
                    refs.put(category, resourceClasses[i]);
                }
            }
            categoryMap.setResourceTypeReferences(refs);
            categoryMap.update(subject);
        }
        catch (ValueRequiredException e)
        {
            throw new CategoryException("Problem updating resource type references", e);
        }
    }

    private void unsetCategoryResourceClass(ResourceClassResource resourceClass)
        throws CategoryException
    {
        try
        {
            CrossReference refs = categoryMap.getResourceTypeReferences();
            refs.removeInv(resourceClass);
            categoryMap.setResourceTypeReferences(refs);
            categoryMap.update(rootSubject);
        }
        catch (ValueRequiredException e)
        {
            throw new CategoryException("Problem updating resource type references", e);
        }
    }

    /**
     * Returns all resource classes bound to a category.
     *
     * @param category the category.
     * @return an array of resource class resources bound to a category
     */
    public ResourceClassResource[] getResourceClasses(CategoryResource category, boolean includeImplied)
    {
        CrossReference resTypeReferences = categoryMap.getResourceTypeReferences();

        CategoryResource[] categories;
        if (includeImplied)
        {
            categories = getImpliedCategories(category, true);
        }
        else
        {
            categories = new CategoryResource[1];
            categories[0] = category;
        }

        HashSet resClasses = new HashSet();
        for (int i = 0; i < categories.length; i++)
        {
            resClasses.addAll(Arrays.asList(resTypeReferences.get(categories[i])));
        }

        ResourceClassResource[] resClasses2 = new ResourceClassResource[resClasses.size()];
        return (ResourceClassResource[]) (resClasses.toArray(resClasses2));
    }

    /**
     * Returns <code>true</code> if given category is bound to a given resource class.
     *
     * @param category the category.
     * @param resClass the resource class.
     * @return <code>true</code> if given category is bound to given resource class.
     */
    public boolean hasResourceClass(CategoryResource category, ResourceClassResource resClass)
    {
        CrossReference resTypeReferences = categoryMap.getResourceTypeReferences();
        return resTypeReferences.hasRef(category, resClass);
    }

    /**
     * Returns <code>true</code> if given category supports given resource class,
     * ie. if the category or one of the parent categories is bound to a given resource class.
     *
     * @param category the category.
     * @param resClass the resource class.
     * @return <code>true</code> if given category is bound to given resource class.
     */
    public boolean supportsResourceClass(CategoryResource category, ResourceClassResource resClass)
    {
        CrossReference resTypeReferences = categoryMap.getResourceTypeReferences();
        CategoryResource[] parentCats = getImpliedCategories(category, true);
        for (int i = 0; i < parentCats.length; i++)
        {
            if (resTypeReferences.hasRef(parentCats[i], resClass))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove a resource collection from a category.
     *
     * @param resources the resource collection.
     * @param category the category.
     * @param subject the subject that performs the operation.
     */
    public void removeFromCategory(Resource[] resources, CategoryResource category, Subject subject) throws CategoryException
    {
        try
        {
            CrossReference refs = categoryMap.getReferences();

            for (int i = 0; i < resources.length; i++)
            {
                refs.remove(category, resources[i]);
            }

            categoryMap.setReferences(refs);
            categoryMap.update(subject);
        }
        catch (ValueRequiredException e)
        {
            throw new CategoryException("Problem updating category references", e);
        }
    }

    /**
     * Remove a resource from all categories.
     *
     * @param resource the resource.
     */
    public void removeFromAllCategories(Resource resource, Subject subject) throws CategoryException
    {
        try
        {
            CrossReference refs = categoryMap.getReferences();
            refs.removeInv(resource);
            categoryMap.setReferences(refs);
            categoryMap.update(subject);
        }
        catch (ValueRequiredException e)
        {
            throw new CategoryException("Problem updating category references", e);
        }
    }

    // optimisation /////////////////////////////////////////////////////////////////////////

    /**
     *  Optimises Resource Class assignments in category tree.
     */
    public Set optimiseResourceClassesAssignments(CategoryResource category, Subject subject, boolean recursive) throws CategoryException
    {
        // get category - resource class references
        CrossReference refs = categoryMap.getResourceTypeReferences();

        // prepare parent categories resource classes
        HashSet impliedResClasses = new HashSet();
        Resource parent = category.getParent();
        if (parent instanceof CategoryResource)
        {
            impliedResClasses.addAll(Arrays.asList(getResourceClasses((CategoryResource)parent, true)));
        }

        HashSet removedResClasses = new HashSet();
        // traverse down the tree and remove repeated resource class assignments
        optimiseRCA(refs, impliedResClasses, category, removedResClasses, recursive);

        // update references
        try
        {
            categoryMap.setResourceTypeReferences(refs);
            categoryMap.update(subject);
        }
        catch (ValueRequiredException e)
        {
            throw new CategoryException("Problem updating resource type references", e);
        }

        // returns removed resource classes
        return removedResClasses;
    }

    private void optimiseRCA(CrossReference refs, HashSet impliedResClasses, CategoryResource category, HashSet removedResClasses, boolean recursive)
    {
        // get resource classes directly assigned to category
        ResourceClassResource[] resClasses = getResourceClasses(category, false);

        // remove ones which are also bound to parent categories
        for (int i = 0; i < resClasses.length; i++)
        {
            ResourceClassResource resClass = resClasses[i];
            if (impliedResClasses.contains(resClass))
            {
                // remove from references
                refs.remove(category, resClass);
                removedResClasses.add(resClass);
            }
            else
            {
                // add for child categories
                impliedResClasses.add(resClass);
            }
        }

        if (recursive)
        {
            // loop through children and descend
            Resource[] children = resourceService.getStore().getResource(category);
            for (int i = 0; i < children.length; i++)
            {
                optimiseRCA(refs, impliedResClasses, (CategoryResource)children[i], removedResClasses, true);
            }
        }
    }

    /**
     *  Optimises category set (assigned to a resource), leaves only subcategories removing
     *  categories which are implied.
     */
    public Set optimiseCategorizationSet(Set categories)
    {
        HashSet removedCategories = new HashSet();

        // get implied categories for all categories excluding themselfs
        HashSet impliedCategories = new HashSet();
        for (Iterator i = categories.iterator(); i.hasNext();)
        {
            CategoryResource category = (CategoryResource)i.next();
            CategoryResource[] implied = getImpliedCategories(category, false);
            impliedCategories.addAll(Arrays.asList(implied));
        }

        // remove categories which have subcategories
        HashSet categs = new HashSet(categories);
        for (Iterator i = categs.iterator(); i.hasNext();)
        {
            CategoryResource category = (CategoryResource)i.next();
            if (impliedCategories.contains(category))
            {
                // remove
                categories.remove(category);
                removedCategories.add(category);
            }
        }

        // return removed categories
        return removedCategories;
    }

    /**
     *  Gathers wrong resource assignments for a category taking into account supported resource
     *  classes. Returns resources which do not fit supported resource classes.
     */
    public Set fixCategoryAssignments(CategoryResource category) throws CategoryException
    {
        HashSet removedResources = new HashSet();

        // get resources directly assigned to category
        Resource[] assignedResources = getResources(category, false);

        // get category's resource classes (including implied)
        HashSet categoryResClasses = new HashSet();
        categoryResClasses.addAll(Arrays.asList(getResourceClasses(category, true)));

        HashMap resourceClassCache = new HashMap();

        // remove resources which do not fit category's resource classes
        for (int i = 0; i < assignedResources.length; i++)
        {
            Resource resource = assignedResources[i];

            ResourceClass resClass1 = resource.getResourceClass();
            if (!resourceClassCache.containsKey(resClass1))
            {
                resourceClassCache.put(resClass1, integrationService.getResourceClass(resClass1));
            }
            ResourceClassResource resClass = (ResourceClassResource) (resourceClassCache.get(resClass1));

            if (!categoryResClasses.contains(resClass))
            {
                removedResources.add(resource);
            }
        }

        // return removed resources
        return removedResources;
    }

	// resource deletion listener //////////////////////////////////////////////////////////////////
	
    public void resourceDeleted(Resource resource)
    {
        if (resource instanceof ResourceClassResource)
        {
            try
            {
				unsetCategoryResourceClass((ResourceClassResource)resource);
            }
            catch (CategoryException e)
            {
                throw new RuntimeException(e);
            }
        }
        if (!(resource instanceof CategoryResource))
        {
            try
            {
                removeFromAllCategories(resource, rootSubject);
            }
            catch (CategoryException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

}
