package net.cyklotron.cms.category.components;

import java.util.ArrayList;
import java.util.Set;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.util.CmsResourceClassFilter;
import net.cyklotron.cms.util.CmsResourceListTableModel;
import net.cyklotron.cms.util.ProtectedValidityViewFilter;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableException;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableTool;

/**
 * Class which hold basic logic for component for displaying lists of resources assigned to
 * queried categories.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseResourceList.java,v 1.5 2005-02-09 19:23:06 rafal Exp $
 */
public abstract class BaseResourceList
{
    /** context */
    protected Context context;
    
    /** integration service */
    protected IntegrationService integrationService;
    
    /** cms date facotry */
    protected CmsDataFactory cmsDataFactory;
    
    public BaseResourceList(Context context, IntegrationService integrationService,
        CmsDataFactory cmsDataFactory)
    {
        this.context = context;
        this.integrationService = integrationService;
        this.cmsDataFactory = cmsDataFactory;
    }
    
	public abstract BaseResourceListConfiguration createConfig()
	throws ProcessingException;

    public TableTool getTableTool(
        CoralSession coralSession, Context context, 
    	BaseResourceListConfiguration initedConfig, TableState state, 
    	Resource[] resources)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        I18nContext i18nContext = I18nContext.getI18nContext(context);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        

        // setup table state
        state.setTreeView(false);
        //state.setMultiSelect(false);
        state.setShowRoot(false);

        // number of shown resources
        setupPaging(initedConfig, state, parameters);

        // sorting
		state.setSortColumnName(initedConfig.getSortColumn());
		state.setAscSort(initedConfig.getSortDir());

        //prepare a tableHelper to display in component
        try
        {
            TableModel model = getTableModel(resources, initedConfig, i18nContext);
            ArrayList filters = new ArrayList();
            // setup filters for resources

            //  - security and time filter
            filters.add(new ProtectedValidityViewFilter(context, cmsData, cmsData.getUserData().getSubject()));

            //  - filter out via res classes - if none selected, pass all
            String[] resClassesNames = getResourceClasses(coralSession, initedConfig);
            if(resClassesNames != null && resClassesNames.length > 0)
            {
                filters.add(new CmsResourceClassFilter(coralSession, integrationService, resClassesNames));
            }
        
            //  - add custom filters from subclass
            TableFilter[] filters2 = getTableFilters(coralSession, initedConfig);
            for(int i=0; i < filters2.length; i++)
            {
                filters.add(filters2[i]);
            }
            TableTool helper = new TableTool(state, filters, model);
            return helper;
        }
        catch(TableException e)
        {
            throw new ProcessingException("Cannot create TableTool", e);
        }
    }
    
	/** Returns a table state name unique for this resource list component. */
	public abstract String getTableStateName();
    
	/**
	 * Returns accepted resource classes for this component.
	 * @param coralSession TODO
	 * @param config
	 */
	protected abstract String[] getResourceClasses(CoralSession coralSession, BaseResourceListConfiguration config)
	throws ProcessingException;

	/** Returns a category query string for this resource list component. 
	 * @param coralSession TODO*/
	public abstract String getQuery(CoralSession coralSession, BaseResourceListConfiguration config)
	throws ProcessingException;

    /** Return a filterig id set for this resource list component. 
     * @param coralSession TODO*/
    public Set getIdSet(CoralSession coralSession, BaseResourceListConfiguration config)
        throws ProcessingException
    {
        return null;
    }

    private static final TableFilter[] emptyFilters = new TableFilter[0];
    /** Returns filters specific for resource list subclass. 
     * @param coralSession TODO*/
    protected TableFilter[] getTableFilters(CoralSession coralSession, BaseResourceListConfiguration config)
    throws ProcessingException
    {
        return emptyFilters;
    }

    /** Sets up paging for this resource class. 
     * @param parameters TODO*/
    protected void setupPaging(BaseResourceListConfiguration config, TableState state, Parameters parameters)
    {
        // number of shown resources
        state.setCurrentPage(1);
        state.setPageSize(config.getMaxResNumber());
    }
    
	/** Returns a table mode specific for this resource list subclass. */
	protected TableModel getTableModel(Resource[] resources,
		BaseResourceListConfiguration config, I18nContext i18nContext) throws TableException
	{
		return new CmsResourceListTableModel(context, integrationService, resources, i18nContext.getLocale());
	}   
}
