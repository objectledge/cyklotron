package net.cyklotron.cms.category.query;

import net.cyklotron.cms.category.components.DocumentResourceListConfiguration;
import net.labeo.util.configuration.Configuration;

/**
 * Provides default parameter values for category query results screen configuration.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryResultsConfiguration.java,v 1.1 2005-01-12 20:44:47 pablo Exp $ 
 */
public class CategoryQueryResultsConfiguration
extends DocumentResourceListConfiguration
{
    private CategoryQueryResource categoryQuery;
    
	public CategoryQueryResultsConfiguration(
        Configuration screenConfig,
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
