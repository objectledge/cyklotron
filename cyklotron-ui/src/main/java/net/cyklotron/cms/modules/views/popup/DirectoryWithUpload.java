package net.cyklotron.cms.modules.views.popup;

import java.util.ArrayList;
import java.util.Arrays;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.CoralTableModel;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableException;
import org.objectledge.table.TableFilter;
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
import net.cyklotron.cms.files.FilesMapResource;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.modules.views.files.BaseFilesScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.util.ProtectedViewFilter;

/**
 * Simple files directory popup screen.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: DirectoryWithUpload.java,v 1.3 2005-01-26 09:00:36 pablo Exp $
 */
public class DirectoryWithUpload
    extends BaseFilesScreen
{

    public DirectoryWithUpload(org.objectledge.context.Context context, Logger logger,
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

            TableColumn[] columns = new TableColumn[1];
            columns[0] = new TableColumn("name", new NameComparator(i18nContext.getLocale()));
            TableState state = tableStateManager.getState(context, "cms:screens:popup,Directory-List");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
                state.setSortColumnName("name");
                state.setAscSort(true);
            }
            TableModel model = new ListTableModel(Arrays.asList(files), columns);
            templatingContext.put("table", new TableTool(state, null, model));

            TableState state2 = tableStateManager.getState(context, "cms:screens:popup,Directory-Tree");
            SiteResource site = getSite();
            
            state2.setTreeView(true);
            state2.setShowRoot(true);
            //state2.setMultiSelect(false);
            ///state2.setSelected(directory.getIdString());
            state2.setSortColumnName("name");
            state2.setAscSort(true);
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
            TableModel model2 = new CoralTableModel(coralSession, i18nContext.getLocale());
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
            filters2.add(new ProtectedViewFilter(context, coralSession.getUserSubject()));
            TableTool helper = new TableTool(state2, filters2, model2);
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
