package net.cyklotron.cms.modules.views.aggregation;

import java.util.Arrays;

import net.cyklotron.cms.aggregation.ImportResource;
import net.cyklotron.cms.aggregation.util.ImportResourceSourceSiteComparator;
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

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: Imported.java,v 1.1 2005-01-24 04:34:51 pablo Exp $
 */
public class Imported 
    extends BaseAggregationScreen
{
    protected TableService tableService;

    public Imported()
        throws ProcessingException
    {
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
    }

    /* 
     * (overriden)
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession) 
        throws ProcessingException
    {
        try
        {
            TableColumn[] columns = new TableColumn[1];
            columns[0] = new TableColumn("sourceSite", new ImportResourceSourceSiteComparator(i18nContext.getLocale()()));
            ImportResource[] imports = aggregationService.getImports(getSite());
            TableState state = tableService.getLocalState(data, "cms:screens:aggregation:Imported");
            if(state.isNew())
            {
                state.setViewType(TableConstants.VIEW_AS_LIST);
                state.setPageSize(10);
            }
            TableModel model = new ListTableModel(Arrays.asList(imports), columns);
            templatingContext.put("table", new TableTool(state, model,null));
        }
        catch (Exception e)
        {
            throw new ProcessingException("failed to retrieve import information");
        }
    }
}
