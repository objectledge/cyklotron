package net.cyklotron.cms.modules.views.popup;

import java.util.ArrayList;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.table.generic.ListTableModel;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.modules.views.files.BaseFilesScreen;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 * Simple files directory popup screen.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ChooseDirectory.java,v 1.3 2005-01-26 09:00:36 pablo Exp $
 */
public class ChooseDirectory
    extends BaseFilesScreen
{

    
    public ChooseDirectory(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, FilesService filesService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, filesService);
        // TODO Auto-generated constructor stub
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            Resource filesRoot = filesService.getFilesRoot(coralSession, getSite());
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
            TableState state = tableStateManager.getState(context, "cms:screens:popup,ChooseDirectory");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
            }
            TableModel model = new ListTableModel(directories, columns);
            templatingContext.put("table", new TableTool(state, null, model));
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
