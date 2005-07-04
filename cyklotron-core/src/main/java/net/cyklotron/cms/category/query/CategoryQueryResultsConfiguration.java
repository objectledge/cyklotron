package net.cyklotron.cms.category.query;

import org.objectledge.parameters.Parameters;

import net.cyklotron.cms.category.components.DocumentResourceListConfiguration;

/**
 * Provides default parameter values for category query results screen configuration.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryResultsConfiguration.java,v 1.4 2005-07-04 09:52:39 rafal Exp $ 
 */
public class CategoryQueryResultsConfiguration
extends DocumentResourceListConfiguration
{
    private CategoryQueryResource categoryQuery;
    
	public CategoryQueryResultsConfiguration(
        Parameters screenConfig,
        CategoryQueryResource categoryQuery)
	{
        super();
        if(categoryQuery != null)
        {
            this.categoryQueryName = categoryQuery.getName();
        }
        shortInit(screenConfig);
        this.categoryQuery = categoryQuery;
	}
    
    public CategoryQueryResource getCategoryQuery()
    {
        return categoryQuery;
    }
}
