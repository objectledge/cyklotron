package net.cyklotron.cms.category.query;

import net.cyklotron.cms.category.components.DocumentResourceListConfiguration;

import org.objectledge.parameters.Parameters;

/**
 * Provides default parameter values for category query results screen configuration.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryResultsConfiguration.java,v 1.2 2005-01-20 05:45:22 pablo Exp $ 
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
	}
}
