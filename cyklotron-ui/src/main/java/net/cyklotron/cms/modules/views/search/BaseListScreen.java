package net.cyklotron.cms.modules.views.search;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.ARLTableModel;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.site.SiteResource;

/**
 * A base list screen for search app.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseListScreen.java,v 1.1 2005-01-24 04:35:07 pablo Exp $
 */
public abstract class BaseListScreen extends BaseSearchScreen
{
    /** table service for list display. */
    TableService tableService = null;

    public BaseListScreen()
    {
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
    throws ProcessingException
    {
        CmsData cmsData = getCmsData();
        SiteResource site = cmsData.getSite();
        if(site == null)
        {
            site = cmsData.getGlobalComponentsDataSite();
        }
        if(site == null)
        {
            throw new ProcessingException("No site seletcted");
        }
        try
        {
            TableState state = tableService.getLocalState(data, getTableStateName(site));
            if(state.isNew())
            {
                Resource root = getTableRoot(site);
                state.setRootId(root.getIdString());

                state.setSortColumnName("name");
                state.setViewType(TableConstants.VIEW_AS_LIST);
            }
        
            setupTableState(state, data);
            
            TableModel model = new ARLTableModel(i18nContext.getLocale()());
            templatingContext.put("table", new TableTool(state, model, null));
        }
        catch(SearchException e)
        {
            throw new ProcessingException("cannot get root for list view", e);
        }
        catch(TableException e)
        {
            throw new ProcessingException("table tool setup failed", e);
        }
    }
    
    protected abstract String getTableStateName(SiteResource site);
    protected abstract Resource getTableRoot(SiteResource site)
        throws SearchException;
    protected abstract void setupTableState(TableState state, RunData data);
}
