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
 * @version $Id: Periodicals.java,v 1.2 2005-01-25 11:24:04 pablo Exp $
 */
public class Periodicals 
    extends BasePeriodicalsScreen
{
    protected TableService tableService;

    public Periodicals()
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
            columns[0] = new TableColumn("name", new NameComparator(i18nContext.getLocale()()));
            PeriodicalResource[] periodicals = periodicalsService.
                getPeriodicals(getSite());
            TableState state = tableService.getLocalState(data, "cms:screens:periodicals:Periodicals");
            if(state.isNew())
            {
                state.setTreeView(false);
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
