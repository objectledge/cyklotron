package net.cyklotron.cms.modules.views.site;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.ComponentInitializationError;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
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
import org.objectledge.table.comparator.MapComparator;
import org.objectledge.table.generic.ListTableModel;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;

public class VirtualSiteList
    extends BaseCMSScreen
{
    protected SiteService siteService;

    protected StructureService structureService;

    protected TableColumn[] columns;

    public VirtualSiteList(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, SiteService siteService,
        StructureService structureService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.siteService = siteService;
        this.structureService = structureService;
        try
        {
            columns = new TableColumn[4];
            columns[0] = new TableColumn("domain", new MapComparator("domain"));
            columns[1] = new TableColumn("site", new MapComparator("site"));
            columns[2] = new TableColumn("primary", new MapComparator("primary"));
            columns[3] = new TableColumn("default_node_path", null);
        }
        catch(TableException e)
        {
            throw new ComponentInitializationError("failed to initialize table columns", e);
        }
    }

    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = null;
            if(parameters.isDefined("site_id"))
            {
                site = getSite();
            }
            List<Map> virtualSiteList = getVirtualSites(coralSession, site);
            if(!parameters.isDefined("text"))
            {
                TableState state = tableStateManager.getState(context,
                    "cms:screens:site,VirtualSiteList");
                if(state.isNew())
                {
                    state.setTreeView(false);
                    state.setPageSize(10);
                }
                TableModel model = new ListTableModel(virtualSiteList, columns);
                templatingContext.put("table", new TableTool(state, null, model));
            }
            else
            {
                httpContext.setContentType("text/plain");
                PrintWriter w = httpContext.getPrintWriter();
                w.println("#domain\tsite\tsite_id\thome\thome_id");
                for(Map m : virtualSiteList)
                {
                    w.print((String)m.get("domain"));
                    w.print("\t\"");
                    w.print(((Resource)m.get("site")).getName());
                    w.print("\"\t");
                    w.print(((Resource)m.get("site")).getIdString());
                    w.print("\t\"");
                    w.print((String)m.get("default_node_path"));
                    w.print("\"\t");
                    w.println(((Resource)m.get("default_node")).getIdString());
                }
                w.flush();
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to lookup information", e);
        }
    }

    private List<Map> getVirtualSites(CoralSession coralSession, SiteResource site)
        throws SiteException, ProcessingException
    {
        String[] virtuals = siteService.getVirtualServers(coralSession);
        ArrayList virtualSiteList = new ArrayList(virtuals.length);
        for(int i = 0; i < virtuals.length; i++)
        {
            SiteResource target = siteService.getSiteByAlias(coralSession, virtuals[i]);
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
                throw new ProcessingException("failed to lookup structure root node for site "
                    + site.getName());
            }
            Resource structure = res[0];
            NavigationNodeResource defaultNode = siteService.getDefaultNode(coralSession,
                virtuals[i]);
            virtualSiteDesc.put("default_node", defaultNode);
            virtualSiteDesc.put("default_node_path", defaultNode.getPath().substring(
                structure.getPath().length()));
            virtualSiteDesc.put("primary", new Boolean(siteService.isPrimaryMapping(coralSession,
                virtuals[i])));
            virtualSiteList.add(virtualSiteDesc);
        }
        return virtualSiteList;
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Role cmsAdmin = coralSession.getSecurity().getUniqueRole("cms.administrator");
        return (getSite() != null && coralSession.getUserSubject().hasRole(
            getSite().getTeamMember()))
            || coralSession.getUserSubject().hasRole(cmsAdmin);
    }
}
