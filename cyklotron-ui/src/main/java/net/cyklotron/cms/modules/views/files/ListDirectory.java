package net.cyklotron.cms.modules.views.files;

import java.util.ArrayList;
import java.util.Arrays;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
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

import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.util.ProtectedViewFilter;

/**
 * Directory listing screen.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ListDirectory.java,v 1.1 2005-01-24 04:34:12 pablo Exp $
 */
public class ListDirectory
    extends BaseFilesScreen
{
    /** table service */
    private TableService tableService = null;
    
    public ListDirectory()
    {
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long directoryId = parameters.get("dir_id").asLong(filesService.getFilesRoot(getSite()).getId());
            Resource directory = coralSession.getStore().getResource(directoryId);
            templatingContext.put("current_directory",directory);
            Resource[] resources = coralSession.getStore().getResource(directory);
            TableState state = tableService.getLocalState(data, "cms:screens:files,ListDirectory");
            if(state.isNew())
            {
                state.setViewType(TableConstants.VIEW_AS_LIST);
                state.setPageSize(10);
                state.setSortColumnName("name");
            }
            
            TableColumn[] columns = new TableColumn[3];
            columns[0] = new TableColumn("name", new NameComparator(i18nContext.getLocale()()));
            columns[1] = new TableColumn("creator", new CreatorNameComparator(i18nContext.getLocale()()));
            columns[2] = new TableColumn("creation_time", new CreationTimeComparator());
            TableModel model = new ListTableModel(Arrays.asList(resources), columns);
            try
            {
                ArrayList filters = new ArrayList();
                filters.add(new ProtectedViewFilter(coralSession.getUserSubject()));
                TableTool helper = new TableTool(state, model, filters);
                templatingContext.put("table", helper);
            }
            catch(TableException e)
            {
                throw new ProcessingException("failed to initialize table", e);
            }
        }
        catch(FilesException e)
        {
            throw new ProcessingException("FilesException ",e);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("FilesException ",e);
        }
        catch(TableException e)
        {
            throw new ProcessingException("FilesException ",e);
        }
    }    
}

