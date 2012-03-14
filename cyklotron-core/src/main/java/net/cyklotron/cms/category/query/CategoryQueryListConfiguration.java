package net.cyklotron.cms.category.query;

import org.objectledge.parameters.Parameters;

/**
 * Provides default parameter values for category query list component's configuration.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryListConfiguration.java,v 1.5 2005-05-23 08:06:52 zwierzem Exp $ 
 */
public class CategoryQueryListConfiguration
{
	private String header;
	private String queryPoolName;
	private String sortColumn;
	private boolean sortDir;

	public CategoryQueryListConfiguration(Parameters componentConfig)
	{
		header = componentConfig.get("header",null);
		queryPoolName = componentConfig.get("queryPoolName",null);
		sortColumn = componentConfig.get("querySortColumn","name");
        // NOTE: 0 in parameters means ascending (true), 1 means descending (false)
        if(componentConfig.isDefined("querySortDir")
            && componentConfig.get("querySortDir").length() == 1)
        {
            sortDir = componentConfig.getInt("querySortDir", 0) == 0;
        }
        else
        {
            sortDir = componentConfig.getBoolean("querySortDir", true);
        }
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

	public boolean getSortDir()
	{
		return sortDir;
	}
}
