package net.cyklotron.cms.modules.views.aggregation;

import java.util.Arrays;

import net.cyklotron.cms.site.SiteResource;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.table.CreationTimeComparator;
import net.labeo.services.resource.table.CreatorNameComparator;
import net.labeo.services.resource.table.NameComparator;
import net.labeo.services.table.ListTableModel;
import net.labeo.services.table.TableColumn;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableException;
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
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ImporterList.java,v 1.1 2005-01-24 04:34:51 pablo Exp $
 */
public class ImporterList
    extends BaseAggregationScreen
{
    protected TableService tableService;

    public ImporterList()
        throws ProcessingException
    {
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource[] sites = siteService.getSites();
            TableColumn[] columns = new TableColumn[3];
            columns[0] = new TableColumn("name", new NameComparator(i18nContext.getLocale()()));
            columns[1] = new TableColumn("creator", new CreatorNameComparator(i18nContext.getLocale()()));
            columns[2] = new TableColumn("creation_time", new CreationTimeComparator());
            TableState state = tableService.getLocalState(data, "cms:screens:aggregation:ImporterList");
            if(state.isNew())
            {
                state.setViewType(TableConstants.VIEW_AS_LIST);
                state.setPageSize(10);
            }
            TableModel model = new ListTableModel(Arrays.asList(sites), columns);
            templatingContext.put("table", new TableTool(state, model, null));
            Role importerRole = coralSession.getSecurity().getUniqueRole("cms.aggregation.importer");
            templatingContext.put("importer_role",importerRole);
        }
        catch(TableException e)
        {
            throw new ProcessingException("failed to initialize table toolkit", e);
        }
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        Role role = coralSession.getSecurity().getUniqueRole("cms.administrator");
        return coralSession.getUserSubject().hasRole(role);
    }
}
