package net.cyklotron.cms.modules.views.site;

import net.labeo.services.resource.table.CreationTimeComparator;
import net.labeo.services.resource.table.CreatorNameComparator;
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

/**
 *
 */
public class TemplateList
    extends BaseSiteScreen
{
    protected TableService tableService;

    public TemplateList()
        throws ProcessingException
    {
        tableService = (TableService)broker.
            getService(TableService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource[] templates = siteService.getTemplates();
            TableColumn[] columns = new TableColumn[4];
            columns[0] = new TableColumn("name", new NameComparator(i18nContext.getLocale()()));
            columns[1] = new TableColumn("creator", new CreatorNameComparator(i18nContext.getLocale()()));
            columns[2] = new TableColumn("creation_date", new CreationTimeComparator());
            columns[3] = new TableColumn("description", null);
            TableModel model = new ListTableModel(templates, columns);
            TableState state = tableService.getLocalState(data, "cms:screens:site,TemplateList");
            if(state.isNew())
            {
                state.setViewType(TableConstants.VIEW_AS_LIST);
                state.setPageSize(10);
            }
            templatingContext.put("table", new TableTool(state, model,null));
        }
        catch(Exception e)
        {
            if(e instanceof ProcessingException)
            {
                throw (ProcessingException)e;
            }
            else
            {
                throw new ProcessingException("failed to load node list", e);
            }
        }
    }
}


