package net.cyklotron.cms.modules.views.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.ARLTableModel;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableFilter;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.SchemaRoleResource;

public class ResourceRoles
    extends BaseSecurityScreen
{
    private IntegrationService integrationService;

    public ResourceRoles()
    {
        integrationService = (IntegrationService)broker.getService(IntegrationService.SERVICE_NAME);
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        long resource_id = parameters.getLong("res_id", -1);
        if(resource_id == -1)
        {
            throw new ProcessingException("Couldn't find resource_id parameter");
        }
        long root_id = parameters.getLong("root_id", -1);
        if(root_id == -1)
        {
            root_id = resource_id;
        }
        
        try
        {
            Resource resource = coralSession.getStore().getResource(resource_id);
            Resource root = coralSession.getStore().getResource(root_id);
            templatingContext.put("resource", resource);
            templatingContext.put("root", root);
            ArrayList resources = new ArrayList();
            HashMap tableMap = new HashMap();
            TableFilter tableFilter = new TableFilter()
                {
                    public boolean accept(Object o)
                    {
                        return (o instanceof SchemaRoleResource);
                    }
                };
            
            for(Resource parent = resource; parent != null; parent = parent.getParent())
            {
                Resource schemaRoot = integrationService.getSchemaRoleRoot(parent.getResourceClass());
                resources.add(parent);
                if(schemaRoot != null)
                {
                    TableState state = tableService.getLocalState(data, "cms:screens:security,ResourceRoles:"+parent.getIdString());
                    if(state.isNew())
                    {
                        state.setViewType(TableConstants.VIEW_AS_TREE);
                        state.setMultiSelect(false);
                        String rootId = schemaRoot.getIdString();
                        state.setRootId(rootId);
                        state.setCurrentPage(0);
                        state.setShowRoot(false);
                        //state.setExpanded(rootId);
                        //state.setAllExpanded(true);
                        expandAll(state, schemaRoot);
                        state.setPageSize(0);
                        state.setSortColumnName("name");
                    }
                    TableModel model = new ARLTableModel(i18nContext.getLocale()());
                    ArrayList filters = new ArrayList();
                    filters.add(tableFilter);
                    TableTool helper = new TableTool(state, model, filters);
                    
                    tableMap.put(parent, helper);
                }
                if(root.equals(parent))
                {
                    break;
                }
            }
            Collections.reverse(resources);
            templatingContext.put("resources",resources);
            templatingContext.put("table_map",tableMap);
        }
        catch(TableException e)
        {
            throw new ProcessingException("Cannot create TableTool", e);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("Couldn't find discussion resource", e);
        }
    }

    private void expandAll(TableState state, Resource root)
    {
        state.setExpanded(root.getIdString());
        Resource[] resources = coralSession.getStore().getResource(root);
        for(int i = 0; i < resources.length; i++)
        {
            expandAll(state, resources[i]);
        }
    }
    
}
