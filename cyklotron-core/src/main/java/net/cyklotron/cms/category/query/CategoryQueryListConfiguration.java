package net.cyklotron.cms.category.query;

import net.labeo.services.table.TableConstants;
import net.labeo.util.configuration.Configuration;

/**
 * Provides default parameter values for category query list component's configuration.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryListConfiguration.java,v 1.1 2005-01-12 20:44:47 pablo Exp $ 
 */
public class CategoryQueryListConfiguration
{
	private String header;
	private String queryPoolName;
	private String sortColumn;
	private int sortDir;

	public CategoryQueryListConfiguration(Configuration componentConfig)
	{
		header = componentConfig.get("header").asString(null);
		queryPoolName = componentConfig.get("queryPoolName").asString(null);
		sortColumn = componentConfig.get("querySortColumn").asString("name");
		sortDir = componentConfig.get("querySortDir").asInt(TableConstants.SORT_ASC);
	}

	public String getHeader()
	{
		return header;
	}
    
	public String getQueryPoolName()
	{
		return queryPoolName;
	}
    
	public String getSortColumn()
	{
		return sortColumn;
	}

	public int getSortDir()
	{
		return sortDir;
	}
}
