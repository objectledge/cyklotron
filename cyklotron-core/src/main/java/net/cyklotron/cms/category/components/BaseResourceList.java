package net.cyklotron.cms.category.components;

import java.util.ArrayList;
import java.util.Set;

import net.labeo.services.resource.Resource;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableFilter;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.util.CmsResourceClassFilter;
import net.cyklotron.cms.util.CmsResourceListTableModel;
import net.cyklotron.cms.util.ProtectedValidityViewFilter;

/**
 * Class which hold basic logic for component for displaying lists of resources assigned to
 * queried categories.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseResourceList.java,v 1.1 2005-01-12 20:45:00 pablo Exp $
 */
public abstract class BaseResourceList
{
	public abstract BaseResourceListConfiguration createConfig(RunData data)
	throws ProcessingException;

    public TableTool getTableTool(
    	RunData data,
    	BaseResourceListConfiguration initedConfig,
    	TableState state, 
    	Resource[] resources)
        throws ProcessingException
    {
        CmsData cmsData = CmsData.getCmsData(data);

        // setup table state
        state.setViewType(TableConstants.VIEW_AS_LIST);
        state.setMultiSelect(false);
        state.setShowRoot(false);


        // number of shown resources
        setupPaging(data, initedConfig, state);

        // sorting
		state.setSortColumnName(initedConfig.getSortColumn());
		state.setSortDir(initedConfig.getSortDir());

        //prepare a tableHelper to display in component
        try
        {
            TableModel model = getTableModel(resources, initedConfig, data);
            ArrayList filters = new ArrayList();
            // setup filters for resources

            //  - security and time filter
            filters.add(new ProtectedValidityViewFilter(cmsData, cmsData.getUserData().getSubject()));

            //  - filter out via res classes - if none selected, pass all
            String[] resClassesNames = getResourceClasses(data, initedConfig);
            if(resClassesNames != null && resClassesNames.length > 0)
            {
                filters.add(new CmsResourceClassFilter(resClassesNames));
            }
        
            //  - add custom filters from subclass
            TableFilter[] filters2 = getTableFilters(data, initedConfig);
            for(int i=0; i < filters2.length; i++)
            {
                filters.add(filters2[i]);
            }
            TableTool helper = new TableTool(state, model, filters);
            return helper;
        }
        catch(TableException e)
        {
            throw new ProcessingException("Cannot create TableTool", e);
        }
    }
    
	/** Returns a table state name unique for this resource list component. */
	public abstract String getTableStateName(RunData data);
    
	/**
	 * Returns accepted resource classes for this component.
	 * 
	 * @param data
	 * @param config
	 */
	protected abstract String[] getResourceClasses(RunData data, BaseResourceListConfiguration config)
	throws ProcessingException;

	/** Returns a category query string for this resource list component. */
	public abstract String getQuery(RunData data, BaseResourceListConfiguration config)
	throws ProcessingException;

    /** Return a filterig id set for this resource list component. */
    public Set getIdSet(RunData data, BaseResourceListConfiguration config)
        throws ProcessingException
    {
        return null;
    }

    private static final TableFilter[] emptyFilters = new TableFilter[0];
    /** Returns filters specific for resource list subclass. */
    protected TableFilter[] getTableFilters(RunData data, BaseResourceListConfiguration config)
    throws ProcessingException
    {
        return emptyFilters;
    }

    /** Sets up paging for this resource class. */
    protected void setupPaging(RunData data, BaseResourceListConfiguration config, TableState state)
    {
        // number of shown resources
        state.setCurrentPage(1);
        state.setPageSize(config.getMaxResNumber());
    }
    
	/** Returns a table mode specific for this resource list subclass. */
	protected TableModel getTableModel(Resource[] resources,
		BaseResourceListConfiguration config, RunData data) throws TableException
	{
		return new CmsResourceListTableModel(resources, data.getLocale());
	}   
}
