package net.cyklotron.cms.category.query;

import java.util.Map;
import java.util.Set;

import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * This service manages and executes category queries.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryService.java,v 1.8 2007-11-18 21:23:11 rafal Exp $
 */
public interface CategoryQueryService
{
    /** The name of the service (<code>"category"</code>). */
    public final static String SERVICE_NAME = "cms_category_query";

    /** The logging facility where the service issues it's informational
     * messages. */
    public static final String LOGGING_FACILITY = "cms_category_query";

    /** The parent node of the category-query data (&lt;site&gt;/applications/category_query) */
    public static final String ROOT = "category_query";

	/** The parent node of the category queries (&lt;site&gt;/applications/category_query/query) */
    public static final String QUERY_ROOT = "query";

	/** The parent node of the category queries (&lt;site&gt;/applications/category_query/pool) */
	public static final String POOL_ROOT = "pool";

	// resource management ///////////////////////////////////////////////////////////////

    /**
     * Returns the root of category queries list
     *
     * @param site the site to return category query root for.
     */
    public Resource getCategoryQueryRoot(CoralSession coralSession, SiteResource site)
        throws CategoryQueryException;

	/**
	 * Returns the root of category query pools list
	 *
	 * @param site the site to return category query pools root for.
	 */
	public Resource getCategoryQueryPoolRoot(CoralSession coralSession, SiteResource site)
	    throws CategoryQueryException;

    // category resolution /////////////////////////////////////////////////////////////
    
    /**
     * Returns an instance of {@link org.objectledge.coral.relation.ResourceIdentifierResolver} 
     * that is able to translate category identifiers into numeric ids.
     * 
     * @return the resolver object.
     */
    public CategoryResolver getCategoryResolver();

    /**
     * Initializes an ResourceSelectionState object for CategoryResources.
     * 
     * @param items a <code>\n</code> separated list of quoted category paths or numeric ids.
     * @param state the state to be assigned to the CategoryResource objects.
     */
    public Map<CategoryResource, String>
        initCategorySelection(CoralSession coralSession, String items, String state);

	/**
	 * Initializes an ResourceSelectionState object for CategoryResources.
	 * 
	 * @param items a table of single quoted category paths or numeric ids.
	 * @param state the state to be assigned to the CategoryResource objects.
	 */
	public Map<CategoryResource, String>
        initCategorySelection(CoralSession coralSession, String[] items, String state);

    // results screen //////////////////////////////////////////////////////////////////
    
    /**
     * Returns the navigation node that should be used used for displaying category 
     * query results in the specified site.
     * 
     * @param site the site.
     * @return the node to be used for displaying results, or null if not selected.
     */
    public NavigationNodeResource getResultsNode(CoralSession coralSession, SiteResource site)
        throws CategoryQueryException;
    
    /**
     * Returns the default query (used when the user has not selected one) for the 
     * specifed site.
     * 
     * @param site the site.
     * @return the default category query.
     */
    public CategoryQueryResource getDefaultQuery(CoralSession coralSession, SiteResource site)
        throws CategoryQueryException;
        
    /**
     * Sets the navigation node that should be used used for displaying category 
     * query results in the specified site.
     * 
     * @param site the site.
     * @param node the node.
     */
    public void setResultsNode(CoralSession coralSession, SiteResource site, NavigationNodeResource node)
        throws CategoryQueryException;
        
    /**
     * Sets the default query (used when the user has not selected one) for the 
     * specifed site.
     * 
     * @param site the site.
     * @param query the query.
     */
    public void setDefaultQuery(CoralSession coralSession, SiteResource site, CategoryQueryResource query)
        throws CategoryQueryException;

    // queries /////////////////////////////////////////////////////////////////////////

    public Resource[] forwardQuery(CoralSession coralSession, String query)
        throws Exception;

    public Resource[] forwardQuery(CoralSession coralSession, String query, Set idSet)
        throws Exception;

    //public Resource[] reverseQuery(CoralSession coralSession, String query)
    //    throws Exception;

    //public Resource[] reverseQuery(CoralSession coralSession, String query, Set idSet)
     //   throws Exception;
}

