package net.cyklotron.cms.category;

import java.util.List;
import java.util.Set;

import net.labeo.services.ServiceBroker;
import net.labeo.services.pool.RecyclableObject;
import net.labeo.services.resource.Resource;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ContextTool;
import net.labeo.webcore.RunData;

/**
 * A category tool.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: CategoryTool.java,v 1.1 2005-01-12 20:44:28 pablo Exp $
 */
public class CategoryTool
    extends RecyclableObject
    implements ContextTool
{
    /** the rundata for future use */
    private RunData data;

    /** initialization flag. */
    private boolean initialized = false;
    
    /** category info tool */
    private CategoryInfoTool categoryInfoTool;
    
    // initialization ////////////////////////////////////////////////////////

    public void init(ServiceBroker broker, Configuration config)
    {
        if(!initialized)
        {
            initialized = true;
        }
    }

    public void prepare(RunData data)
    {
        this.data = data;
        categoryInfoTool = new CategoryInfoTool(data);
    }
    
    public void reset()
    {
        data = null;
    }
    
    // public interface ///////////////////////////////////////////////////////

    /**
     * Gets all categories that resource belongs to.
     * 
     * @param resource the resource.
     * @param implied if <code>true</code> use implied categoris too.
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
	 * @param implied if <code>true</code> use implied categoris too.
	 * @return the list of categories. 
	 */
    public List getCategoriesAsList(Resource resource, Boolean useImplied)
	{
		return categoryInfoTool.getCategoriesAsList(resource,useImplied.booleanValue());
	}
}

