package net.cyklotron.cms.modules.views.aggregation;

import java.util.Arrays;

import net.cyklotron.cms.aggregation.ImportResource;
import net.cyklotron.cms.aggregation.util.ImportResourceTargetSiteComparator;
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
 * @version $Id: Exported.java,v 1.2 2005-01-25 11:23:53 pablo Exp $
 */
public class Exported
    extends BaseAggregationScreen
{
    protected TableService tableService;

    public Exported()
        throws ProcessingException
    {
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
    }

    /* 
     * (overriden)
     */
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) 
        throws ProcessingException
    {
        try
        {
            TableColumn[] columns = new TableColumn[1];
            columns[0] = new TableColumn("targetSite", new ImportResourceTargetSiteComparator(i18nContext.getLocale()()));
            ImportResource[] exports = aggregationService.getExports(getSite());
            TableState state = tableService.getLocalState(data, "cms:screens:aggregation:Imported");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
            }
            TableModel model = new ListTableModel(Arrays.asList(exports), columns);
            templatingContext.put("table", new TableTool(state, model, null));
        }
        catch (Exception e)
        {
            throw new ProcessingException("failed to retrieve export information");
        }
    }
}
