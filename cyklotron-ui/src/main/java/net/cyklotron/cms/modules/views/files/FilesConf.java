package net.cyklotron.cms.modules.views.files;

import java.util.ArrayList;
import java.util.List;

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
import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.ItemResource;
import net.cyklotron.cms.site.SiteResource;


/**
 * Screen to configure files component.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: FilesConf.java,v 1.1 2005-01-24 04:34:12 pablo Exp $
 */
public class FilesConf
    extends BaseFilesScreen
{
    private TableService tableService;

    public FilesConf()
    {
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
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
                dir = componentConfig.get("dir").asLong(-1L);
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
                directory = filesService.getFilesRoot(site);
            }
            else
            {
                directory = coralSession.getStore().getResource(dir);
            }
            Resource[] files = coralSession.getStore().getResource(directory);
            List directories = new ArrayList();
            for(int i = 0; i < files.length; i++)
            {
                if(files[i] instanceof ItemResource)
                {
                    directories.add(files[i]);
                }
            }
            TableColumn[] columns = new TableColumn[0];
            TableState state = tableService.getLocalState(data, "cms:screens:files,FilesConf");
            if(state.isNew())
            {
                state.setViewType(TableConstants.VIEW_AS_LIST);
                state.setPageSize(10);
            }
            TableModel model = new ListTableModel(directories, columns);
            templatingContext.put("table", new TableTool(state, model, null));
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
