package net.cyklotron.cms.modules.views.popup;

import java.util.ArrayList;
import java.util.List;

import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.modules.views.files.BaseFilesScreen;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
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
 * Simple files directory popup screen.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ChooseDirectory.java,v 1.2 2005-01-25 11:23:55 pablo Exp $
 */
public class ChooseDirectory
    extends BaseFilesScreen
{
    private FilesService filesService;
    
    private TableService tableService;

    public ChooseDirectory()
    {
        filesService = (FilesService)broker.getService(FilesService.SERVICE_NAME);
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME))
            .getFacility(FilesService.LOGGING_FACILITY);
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            Resource filesRoot = filesService.getFilesRoot(getSite());
            Resource directory = null;
            long dirId = parameters.getLong("dir_id", -1L);
            if(dirId == -1L)
            {
                directory = filesRoot;
            }
            else
            {
                directory = coralSession.getStore().getResource(dirId);
            }
            Resource[] files = coralSession.getStore().getResource(directory);
			List directories = new ArrayList();
			for(int i = 0; i < files.length; i++)
			{
				if(files[i] instanceof DirectoryResource)
				{
					directories.add(files[i]);
				}
			}
            TableColumn[] columns = new TableColumn[0];
            TableState state = tableService.getLocalState(data, "cms:screens:popup,ChooseDirectory");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
            }
            TableModel model = new ListTableModel(directories, columns);
            templatingContext.put("table", new TableTool(state, model, null));
            templatingContext.put("current_directory", directory);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("FileServerException",e);
        }
        catch(FilesException e)
        {
            throw new ProcessingException("FileServerException",e);
        }
        catch(TableException e)
        {
            throw new ProcessingException("FileServerException",e);
        }
    }
}
