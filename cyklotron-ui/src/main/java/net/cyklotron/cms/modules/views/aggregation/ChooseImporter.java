package net.cyklotron.cms.modules.views.aggregation;

import java.util.ArrayList;
import java.util.List;

import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.CreationTimeComparator;
import org.objectledge.coral.table.comparator.CreatorNameComparator;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableConstants;
import org.objectledge.table.TableException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableTool;
import org.objectledge.table.generic.ListTableModel;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.site.SiteResource;

/**
 * Aggregation - screen to choose target site to import the resource.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ChooseImporter.java,v 1.3 2005-01-25 11:23:53 pablo Exp $
 */
public class ChooseImporter
    extends BaseAggregationScreen
{
    protected TableService tableService;

    public ChooseImporter()
        throws ProcessingException
    {
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        long resourceId = parameters.getLong("res_id", -1L);
        if(resourceId == -1L)
        {
            throw new ProcessingException("Couldn't find res_id parameter");
        }
        
        try
        {
            Role importerRole = coralSession.getSecurity().getUniqueRole("cms.aggregation.importer");
            Resource resource = coralSession.getStore().getResource(resourceId);
            templatingContext.put("resource", resource);
            Permission permission = coralSession.getSecurity().getUniquePermission("cms.aggregation.import");
            SiteResource[] siteResources = siteService.getSites();
            List sites = new ArrayList();
            for(int i = 0; i < siteResources.length; i++)
            {
                if(siteResources[i].getSiteRole().hasPermission(resource, permission))
                {
                    // be sure that admin didn't revoke import rights after
                    // the permission was granted.
                    if(siteResources[i].getTeamMember().isSubRole(importerRole))
                    {
                        sites.add(siteResources[i]);
                    }
                }
            }
            TableColumn[] columns = new TableColumn[3];
            columns[0] = new TableColumn("name", new NameComparator(i18nContext.getLocale()()));
            columns[1] = new TableColumn("creator", new CreatorNameComparator(i18nContext.getLocale()()));
            columns[2] = new TableColumn("creation_time", new CreationTimeComparator());
            TableState state = tableService.getLocalState(data, "cms:screens:aggregation:ChooseImporter");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
            }
            TableModel model = new ListTableModel(sites, columns);
            templatingContext.put("table", new TableTool(state, model, null));
        }
        catch(TableException e)
        {
            throw new ProcessingException("failed to initialize table toolkit", e);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("failed to lookup the resource", e);
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        Role role = coralSession.getSecurity().getUniqueRole("cms.aggregation.importer");
        return coralSession.getUserSubject().hasRole(role);
    }
}
