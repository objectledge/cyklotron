package net.cyklotron.cms.category;

import java.util.Set;

import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.site.SiteResource;

import org.objectledge.coral.relation.Relation;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;

/**
 * This service manages the categories and their relations with various
 * resources.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: CategoryService.java,v 1.8.6.1 2005-07-28 14:34:51 pablo Exp $
 */
public interface CategoryService
    extends CategoryConstants
{
    /** The parent node of the system-wide categories (/cms/categories) */
    public static final String SYSTEM_CATEGORIES = "/cms/categories";

    /** The parent node of the site-wide categories (categories) */
    public static final String SITE_CATEGORIES = "categories";

    public Relation getResourcesRelation(CoralSession coralSession);
    
    public Relation getResourceClassRelation(CoralSession coralSession);
	
    /**
     * Returns the root of category tree.
     *
     * @param site the site to return category tree for, or <code>null</code>
     *        for system-wide categories.
     */
    public Resource getCategoryRoot(CoralSession coralSession, SiteResource site)
        throws CategoryException;

    /**
     * Returns a list of super categories of a specified category.
     *
     * @param category the category resource.
     * @param includeSelf <code>true</code> to include <code>category</code>
     *        in the list.
     * @return the list of categories.
     */
    public CategoryResource[] getImpliedCategories(CategoryResource category,
                                                   boolean includeSelf);

    /**
     * Returns a list of sub categories of a specified category.
     *
     * @param category the category resource.
     * @param includeSelf <code>true</code> to include <code>category</code>
     *        in the list.
     * @return the list of categories.
     */
    public CategoryResource[] getSubCategories(CoralSession coralSession, CategoryResource category,
                                                   boolean includeSelf);


    /**
     * Returns all categories the resource belongs to.
     *
     * @param resource the resource.
     * @param includeImplied <code>false</code> to list categories assigned
     *        directly, <code>true</code> to include super-categories also.
     */
    public CategoryResource[] getCategories(CoralSession coralSession, Resource resource, boolean includeImplied);

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
     *        inclue resources assigned directly to super-categories of the
     *        given category.
     */
    public Resource[] getResources(CoralSession coralSession, CategoryResource category, boolean includeImplied)
        throws CategoryException;

    /**
     * Adds a new category to the system.
     *
     * @param name the category name.
     * @param description the category description.
     * @param parent the parent category or category tree root.
     * @return category resource.
     * 
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public CategoryResource addCategory(CoralSession coralSession, String name, String description,
                                        Resource parent,
                                        ResourceClassResource[] resourceClasses)
        throws CategoryException, InvalidResourceNameException;

    /**
     * Deletes a category from the system.
     *
     * @param category the category to delete.
     */
    public void deleteCategory(CoralSession coralSession, CategoryResource category)
        throws CategoryException;

    /**
     * Update a category.
     *
     * @param category the category being updated.
     * @param name new category name.
     * @param description new category description.
     * @param parent new category parent (can be another category, or category
     *        root)
     *        
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public void updateCategory(CoralSession coralSession, CategoryResource category, String name,
                               String description, Resource parent, 
                               ResourceClassResource[] resourceClasses)
        throws CategoryException, InvalidResourceNameException;

    /**
     * Returns all resource classes bound to a category.
     *
     * @param category the category.
     * @param includeImplied if <code>true</code> returns also resource classes implied by parent
     *                       categories
     * @return an array of resource class resources bound to a category
     */
    public ResourceClassResource[] getResourceClasses(CoralSession coralSession, CategoryResource category,
        boolean includeImplied);

    /**
     * Returns <code>true</code> if given category is bound to a given resource class.
     *
     * @param category the category.
     * @param resClass the resource class.
     * @return <code>true</code> if given category is bound to given resource class.
     */
    public boolean hasResourceClass(CoralSession coralSession, CategoryResource category,
                                              ResourceClassResource resClass);

    /**
     * Returns <code>true</code> if given category supports given resource class,
     * ie. if the category or one of the parent categories is bound to a given resource class.
     *
     * @param category the category.
     * @param resClass the resource class.
     * @return <code>true</code> if given category is bound to given resource class.
     */
    public boolean supportsResourceClass(CoralSession coralSession, CategoryResource category,
                                              ResourceClassResource resClass);

    /**
     * Remove a resource collection from a category.
     *
     * @param resources the resource collection.
     * @param category the category.
     */
    public void removeFromCategory(CoralSession coralSession, Resource[] resources, CategoryResource category)
        throws CategoryException;

    /**
     * Remove a resource from all categories.
     *
     * @param resource the resource.
     */
    public void removeFromAllCategories(CoralSession coralSession, Resource resource)
        throws CategoryException;

    // optimisation /////////////////////////////////////////////////////////////////////////

    /**
     *  Optimises Resource Class assignments in category tree.
     */
    public Set optimiseResourceClassesAssignments(CoralSession coralSession, CategoryResource category, 
                                                      boolean recursive)
        throws CategoryException;

    /**
     *  Optimises category set (assigned to a resource), leaves only subcategories removing
     *  categories which are implied.
     */
    public Set optimiseCategorizationSet(Set categories);

    /**
     *  Gathers wrong resource assignments for a category taking into account supported resource
     *  classes. Returns resources which do not fit supported resource classes.
     */
    public Set fixCategoryAssignments(CoralSession coralSession, CategoryResource category)
        throws CategoryException;
    
    
    /**
     * 
     * @param coralSession
     * @param node
     * @param oldSite
     * @param newSite
     */
    public void reassignLocalCategories(CoralSession coralSession, Resource node,
        SiteResource oldSite, SiteResource newSite)
        throws CategoryException; 
}

