package net.cyklotron.cms.modules.views.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.CoralTableModel;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableException;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.SchemaRoleResource;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.security.SecurityService;

public class ResourceRoles
    extends BaseSecurityScreen
{
    private IntegrationService integrationService;

    
    public ResourceRoles(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, SecurityService securityService,
        IntegrationService integrationService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        securityService);
        this.integrationService = integrationService;
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
                Resource schemaRoot = integrationService.getSchemaRoleRoot(coralSession, parent.getResourceClass());
                resources.add(parent);
                if(schemaRoot != null)
                {
                    TableState state = tableStateManager.getState(context, "cms:screens:security,ResourceRoles:"+parent.getIdString());
                    if(state.isNew())
                    {
                        state.setTreeView(true);
                        String rootId = schemaRoot.getIdString();
                        state.setRootId(rootId);
                        state.setCurrentPage(0);
                        state.setShowRoot(false);
                        //state.setExpanded(rootId);
                        //state.setAllExpanded(true);
                        expandAll(coralSession, state, schemaRoot);
                        state.setPageSize(0);
                        state.setSortColumnName("name");
                    }
                    TableModel model = new CoralTableModel(coralSession, i18nContext.getLocale());
                    ArrayList filters = new ArrayList();
                    filters.add(tableFilter);
                    TableTool helper = new TableTool(state, filters, model );
                    
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

    private void expandAll(CoralSession coralSession, TableState state, Resource root)
    {
        state.setExpanded(root.getIdString());
        Resource[] resources = coralSession.getStore().getResource(root);
        for(int i = 0; i < resources.length; i++)
        {
            expandAll(coralSession, state, resources[i]);
        }
    }
    
}
