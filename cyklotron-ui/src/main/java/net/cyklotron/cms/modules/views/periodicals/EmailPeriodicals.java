package net.cyklotron.cms.modules.views.periodicals;

import java.util.Arrays;

import net.cyklotron.cms.periodicals.PeriodicalResource;
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

/**
 * Periodicals screen. 
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: EmailPeriodicals.java,v 1.1 2005-01-24 04:34:37 pablo Exp $
 */
public class EmailPeriodicals 
    extends BasePeriodicalsScreen
{
    protected TableService tableService;

    public EmailPeriodicals()
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
            columns[0] = new TableColumn("name", new NameComparator(i18nContext.getLocale()()));
            PeriodicalResource[] periodicals = periodicalsService.
                getEmailPeriodicals(getSite());
            TableState state = tableService.getLocalState(data, "cms:screens:periodicals:EmailPeriodicals");
            if(state.isNew())
            {
                state.setViewType(TableConstants.VIEW_AS_LIST);
                state.setSortColumnName("name");
                state.setPageSize(10);
            }
            TableModel model = new ListTableModel(Arrays.asList(periodicals), columns);
            templatingContext.put("periodicals", new TableTool(state, model, null));
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve information", e);                
        }
    }
}
