package net.cyklotron.cms.modules.components.files;

import java.util.ArrayList;
import java.util.Arrays;

import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Resource;
import net.labeo.services.table.ListTableModel;
import net.labeo.services.table.TableColumn;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.util.ProtectedViewFilter;

/**
 * Files component.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Files.java,v 1.1 2005-01-24 04:35:30 pablo Exp $
 */

public class Files
    extends SkinableCMSComponent
{
    private FilesService filesService;

    private TableService tableService;

    public Files()
    {
        filesService = (FilesService)broker.getService(FilesService.SERVICE_NAME);
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME))
            .getFacility(FilesService.LOGGING_FACILITY);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession) throws ProcessingException
    {
        try
        {
            if(getSite(context) != null)
            {
                Resource filesRoot = filesService.getFilesRoot(getSite(context));
                Parameters componentConfig = getConfiguration();
                Resource directory = null;
                long dir = parameters.getLong("dir_id", -1L);
                if(dir == -1L)
                {
                    dir = componentConfig.get("dir").asLong(-1L);
                }
                if(dir == -1L)
                {
                    directory = filesRoot;
                    dir = directory.getId();
                }
                else
                {
                    directory = coralSession.getStore().getResource(dir);
                }
                Resource[] files = coralSession.getStore().getResource(directory);
                TableColumn[] columns = new TableColumn[0];
                TableState state = tableService.getGlobalState(data, "cms:components:files,Files");
                if(state.isNew())
                {
                    state.setViewType(TableConstants.VIEW_AS_LIST);
                    state.setPageSize(10);
                }
                TableModel model = new ListTableModel(Arrays.asList(files), columns);
                ArrayList filters = new ArrayList();
                filters.add(new ProtectedViewFilter(coralSession.getUserSubject()));
                TableTool helper = new TableTool(state, model, filters);
                
                templatingContext.put("table", helper);
                templatingContext.put("current_directory", directory);
            }
            else
            {
                componentError(context, "No site selected");
            }
        }
        catch(Exception e)
        {
            componentError(context, "Exception", e);
        }
    }
}
