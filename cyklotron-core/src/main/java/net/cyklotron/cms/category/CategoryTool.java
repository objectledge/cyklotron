package net.cyklotron.cms.category;

import java.util.List;
import java.util.Set;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.integration.IntegrationService;

/**
 * A category tool.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: CategoryTool.java,v 1.4 2005-02-09 22:19:55 rafal Exp $
 */
public class CategoryTool
{
    /** Integration service for information on resource classes */
    private IntegrationService integrationService;
    /** Category service for category manipulation */
    private CategoryService categoryService;

    private Context context;

    /** category info tool */
    private CategoryInfoTool categoryInfoTool;
    
    // initialization ////////////////////////////////////////////////////////

    public CategoryTool(Context context, IntegrationService integrationService, CategoryService
        categoryService)
    {
        this.context = context;
        this.integrationService = integrationService;
        this.categoryService = categoryService;
        categoryInfoTool = new CategoryInfoTool(context, integrationService, categoryService);
    }
    // public interface ///////////////////////////////////////////////////////

    /**
     * Gets all categories that resource belongs to.
     * 
     * @param resource the resource.
     * @param useImplied if <code>true</code> use implied categoris too.
     * @return the set of categories. 
     */
    public Set getCategories(Resource resource, Boolean useImplied)
    {
    	return categoryInfoTool.getCategories(resource,useImplied.booleanValue());
    }
    
	/**
	 * Gets all categories that resource belongs to as sorted list.
	 * 
	 * @param resource the resource.
	 * @param useImplied if <code>true</code> use implied categoris too.
	 * @return the list of categories. 
	 */
    public List getCategoriesAsList(Resource resource, Boolean useImplied)
	{
		return categoryInfoTool.getCategoriesAsList(resource,useImplied.booleanValue());
	}
    
    CoralSession getCoralSession(Context context)
    {   
        return (CoralSession)context.getAttribute(CoralSession.class);
    }

}

