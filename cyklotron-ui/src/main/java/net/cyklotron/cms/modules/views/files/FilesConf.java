package net.cyklotron.cms.modules.views.files;

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

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.files.ItemResource;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;


/**
 * Screen to configure files component.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FilesConf.java,v 1.5 2005-03-10 13:39:56 pablo Exp $
 */
public class FilesConf
    extends BaseFilesScreen
{


    public FilesConf(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, FilesService filesService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, filesService);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        templatingContext.put("from_component",new Boolean(true));
        String instance = parameters.get("component_instance","");
        long nodeId = parameters.getLong("node_id", -1);
        if(nodeId == -1)
        {
            Long nodeIdObj = (Long)httpContext.getSessionAttribute(COMPONENT_NODE);
            if(nodeIdObj != null) 
            {
                nodeId = nodeIdObj.longValue();
            }
        }
        else
        {
            httpContext.setSessionAttribute(COMPONENT_NODE,new Long(nodeId));
        }
        if(instance.length()==0)
        {
            instance = (String)httpContext.getSessionAttribute(COMPONENT_INSTANCE);
        }
        else
        {
            httpContext.setSessionAttribute(COMPONENT_INSTANCE,instance);
        }
        templatingContext.put("component_instance",instance);
        parameters.set("component_instance",instance);

        if(nodeId != -1)
        {
            templatingContext.put("component_node",new Long(nodeId));
            parameters.set("component_node",nodeId);
        }

        try
        {
            Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
            long dir = parameters.getLong("dir", -1L);
            if(dir == -1L)
            {
                dir = componentConfig.getLong("dir",-1L);
            }
            Resource directory = null;
            if(dir == -1)
            {
                CmsData cmsData = getCmsData();
                SiteResource site = cmsData.getSite();
                if(site == null)
                {
                    site = cmsData.getGlobalComponentsDataSite();
                }
                if(site == null)
                {
                    throw new ProcessingException("No site selected");
                }
                directory = filesService.getFilesRoot(coralSession, site);
            }
            else
            {
                directory = coralSession.getStore().getResource(dir);
            }
            Resource[] files = coralSession.getStore().getResource(directory);
            List<Resource> directories = new ArrayList<Resource>();
            for(int i = 0; i < files.length; i++)
            {
                if(files[i] instanceof ItemResource)
                {
                    directories.add(files[i]);
                }
            }
            TableColumn[] columns = new TableColumn[0];
            TableState state = tableStateManager.getState(context, "cms:screens:files,FilesConf");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
            }
            TableModel model = new ListTableModel(directories, columns);
            templatingContext.put("table", new TableTool(state, null,model));
            templatingContext.put("current_directory", directory);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("Could not find navigation node ", e);
        }
        catch(FilesException e)
        {
            throw new ProcessingException("CMS Files Exception ", e);
        }
        catch(TableException e)
        {
            throw new ProcessingException("Table Exception ", e);
        }

    }
}
