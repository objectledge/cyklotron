package net.cyklotron.cms.modules.views.appearance;

import net.labeo.services.resource.table.NameComparator;
import net.labeo.services.table.ListTableModel;
import net.labeo.services.table.TableColumn;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.style.LayoutResource;

public class LayoutList
    extends BaseAppearanceScreen
{
    protected TableService tableService;

    public LayoutList()
    {
        tableService = (TableService)broker.
            getService(TableService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();
            LayoutResource[] layouts = styleService.getLayouts(site);
            TableColumn[] columns = new TableColumn[2];
            columns[0] = new TableColumn("name", new NameComparator(i18nContext.getLocale()()));
            columns[1] = new TableColumn("description", null);
            TableModel model = new ListTableModel(layouts, columns);
            TableState state = tableService.getLocalState(data, "screens:cms:layout,LayoutList:"+
                                                     site.getName());
            if(state.isNew())
            {
                state.setViewType(TableConstants.VIEW_AS_LIST);
                state.setCurrentPage(0);
                state.setPageSize(10);
            }
            templatingContext.put("table", new TableTool(state, model, null));
        }
        catch(Exception e)
        {
            throw new ProcessingException("data access failed", e);
        }
    }
}
