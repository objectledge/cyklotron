package net.cyklotron.cms.modules.views.popup;

import java.util.ArrayList;
import java.util.Arrays;

import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.ARLTableModel;
import net.labeo.services.resource.table.NameComparator;
import net.labeo.services.table.ListTableModel;
import net.labeo.services.table.TableColumn;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableFilter;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesMapResource;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.modules.views.files.BaseFilesScreen;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.util.ProtectedViewFilter;

/**
 * Simple files directory popup screen.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: DirectoryWithUpload.java,v 1.2 2005-01-25 11:23:55 pablo Exp $
 */
public class DirectoryWithUpload
    extends BaseFilesScreen
{
    private FilesService filesService;
    
    private TableService tableService;

    public DirectoryWithUpload()
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

            TableColumn[] columns = new TableColumn[1];
            columns[0] = new TableColumn("name", new NameComparator(i18nContext.getLocale()()));
            TableState state = tableService.getLocalState(data, "cms:screens:popup,Directory-List");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
                state.setSortColumnName("name");
                state.setSortDir(TableConstants.SORT_ASC);
            }
            TableModel model = new ListTableModel(Arrays.asList(files), columns);
            templatingContext.put("table", new TableTool(state, model, null));

            TableState state2 = tableService.getLocalState(data, "cms:screens:popup,Directory-Tree");
            SiteResource site = getSite();
            
            state2.setViewType(TableConstants.VIEW_AS_TREE);
            state2.setShowRoot(true);
            state2.setMultiSelect(false);
            state2.setSelected(directory.getIdString());
            state2.setSortColumnName("name");
            state2.setSortDir(TableConstants.SORT_ASC);
            Resource temp = directory;

            
            while(temp!=null && !temp.equals(site.getParent()))
            {
                state2.setExpanded(temp.getIdString());
                temp = temp.getParent();
            }
            if(temp != null)
            {
                state2.setRootId(temp.getIdString());
                state2.setExpanded(temp.getIdString());
            }
            if(state2.getSortColumnName() == null)
            {
                state2.setSortColumnName("creation.time");
            }
            TableModel model2 = new ARLTableModel(i18nContext.getLocale()());
            ArrayList filters2 = new ArrayList();
            filters2.add(new TableFilter()
                            {
                                public boolean accept(Object o)
                                {
                                    if(o instanceof SiteResource)
                                    {
                                        return (!((SiteResource)o).getTemplate());
                                    }
                                    return ((o instanceof DirectoryResource) || 
                                            (o instanceof FilesMapResource));
                                }
                            });
            filters2.add(new ProtectedViewFilter(coralSession.getUserSubject()));
            TableTool helper = new TableTool(state2, model2, filters2);
            templatingContext.put("table2", helper);
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
