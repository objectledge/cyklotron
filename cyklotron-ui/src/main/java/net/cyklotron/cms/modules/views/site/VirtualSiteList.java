package net.cyklotron.cms.modules.views.site;

import java.util.ArrayList;
import java.util.HashMap;

import net.labeo.services.resource.Resource;
import net.labeo.services.table.ListTableModel;
import net.labeo.services.table.MapComparator;
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

import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;

public class VirtualSiteList
    extends BaseCMSScreen
{
    protected SiteService siteService;

    protected StructureService structureService;

    protected TableService tableService;

    protected TableColumn[] columns;

    public VirtualSiteList()
        throws ProcessingException
    {
        tableService = (TableService)broker.
            getService(TableService.SERVICE_NAME);
        siteService = (SiteService)broker.
            getService(SiteService.SERVICE_NAME);
        structureService = (StructureService)broker.
            getService(StructureService.SERVICE_NAME);
        try
        {
            columns = new TableColumn[4];
            columns[0] = new TableColumn("domain", new MapComparator("domain"));
            columns[1] = new TableColumn("site", new MapComparator("site"));
            columns[2] = new TableColumn("primary", new MapComparator("primary"));
            columns[3] = new TableColumn("default_node", null);
        }
        catch(TableException e)
        {
            throw new ProcessingException("failed to initialize table columns", e);
        }
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = null;
            if(parameters.isDefined("site_id"))
            {
                site = getSite();
            }
            String[] virtuals = siteService.getVirtualServers();
            ArrayList virtualSiteList = new ArrayList(virtuals.length);
            for(int i = 0; i < virtuals.length; i++)
            {
                SiteResource target = siteService.getSiteByAlias(virtuals[i]);
                if(site != null && !target.equals(site))
                {
                    continue;
                }
                HashMap virtualSiteDesc = new HashMap(3);
                virtualSiteDesc.put("domain", virtuals[i]);
                virtualSiteDesc.put("site", target);
                Resource[] res = coralSession.getStore().getResource(target, "structure");
                if(res.length == 0)
                {
                    throw new ProcessingException("failed to lookup structure root node for site "+site.getName());
                }
                Resource structure = res[0];
                NavigationNodeResource defaultNode = siteService.getDefaultNode(virtuals[i]);
                virtualSiteDesc.put("default_node", defaultNode.getPath().
                                    substring(structure.getPath().length()));
                virtualSiteDesc.put("primary", new Boolean(siteService.isPrimaryMapping(virtuals[i])));
                virtualSiteList.add(virtualSiteDesc);
            }
            TableState state = tableService.getLocalState(data, "cms:screens:site,VirtualSiteList");
            if(state.isNew())
            {
                state.setViewType(TableConstants.VIEW_AS_LIST);
                state.setPageSize(10);
            }
            TableModel model = new ListTableModel(virtualSiteList, columns);
            templatingContext.put("table", new TableTool(state, model, null));
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to lookup information", e);
        }
    }
}
