package net.cyklotron.cms.modules.views.aggregation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.cyklotron.cms.site.SiteResource;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.PermissionAssignment;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Role;
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

/**
 * 
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ShareResource.java,v 1.2 2005-01-24 10:27:09 pablo Exp $
 */
public class ShareResource
    extends BaseAggregationScreen
{
    protected TableService tableService;

    public ShareResource() throws ProcessingException
    {
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        long resourceId = parameters.getLong("res_id", -1L);
        if(resourceId == -1)
        {
            throw new ProcessingException("Resource id not parameter not found");
        }
        try
        {
            Resource resource = coralSession.getStore().getResource(resourceId);
            templatingContext.put("resource",resource);
            Permission permission = coralSession.getSecurity().getUniquePermission("cms.aggregation.import");
            templatingContext.put("permission",permission);
            Role importerRole = coralSession.getSecurity().getUniqueRole("cms.aggregation.importer");
            
            PermissionAssignment[] assignments = resource.getPermissionAssignments();
            Map grantsMap = new HashMap();
            Map recursiveGrantsMap = new HashMap();
            Map inheritedGrantsMap = new HashMap();
            for(int i = 0; i < assignments.length; i++)
            {
                if(assignments[i].getPermission().equals(permission))
                {
                    if(assignments[i].isInherited())
                    {
                        recursiveGrantsMap.put(assignments[i].getRole(), new Boolean(true));
                    }
                    else
                    {
                        grantsMap.put(assignments[i].getRole(), new Boolean(true));
                    }
                }
            }
            templatingContext.put("grants_map",grantsMap);
            templatingContext.put("recursive_grants_map",recursiveGrantsMap);

            Resource parent = resource.getParent();
            while(parent != null)
            {
                assignments = parent.getPermissionAssignments();
                for(int i = 0; i < assignments.length; i++)
                {
                    if(assignments[i].isInherited() && assignments[i].getPermission().equals(permission))
                    {
                        if(!inheritedGrantsMap.containsKey(assignments[i].getRole()))
                        {
                            inheritedGrantsMap.put(assignments[i].getRole(), parent);
                        }
                    }
                }
                parent = parent.getParent();
            }
            templatingContext.put("inherited_grants_map",inheritedGrantsMap);

            SiteResource[] siteResources = siteService.getSites();
            List sites = new ArrayList();
            for(int i = 0; i < siteResources.length; i++)
            {
                if(siteResources[i].getTeamMember().isSubRole(importerRole))
                {
                    sites.add(siteResources[i]);
                }
            }
            TableColumn[] columns = new TableColumn[3];
            columns[0] = new TableColumn("name", new NameComparator(i18nContext.getLocale()()));
            columns[1] = new TableColumn("creator", new CreatorNameComparator(i18nContext.getLocale()()));
            columns[2] = new TableColumn("creation_time", new CreationTimeComparator());
            TableState state = tableService.getLocalState(data, "cms:screens:aggregation:ImporterAssignments");
            if(state.isNew())
            {
                state.setViewType(TableConstants.VIEW_AS_LIST);
                state.setPageSize(10);
            }
            TableModel model = new ListTableModel(sites, columns);
            templatingContext.put("table", new TableTool(state, model, null));
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("failed to initialize table toolkit", e);
        }
        catch(TableException e)
        {
            throw new ProcessingException("failed to initialize table toolkit", e);
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        try
        {
            SiteResource node = getSite();
            Permission permission = coralSession.getSecurity()
                .getUniquePermission("cms.aggregation.export");
            return coralSession.getUserSubject().hasPermission(node, permission);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to check security", e);
        }
    }
}

