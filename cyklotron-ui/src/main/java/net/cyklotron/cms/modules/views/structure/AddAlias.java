package net.cyklotron.cms.modules.views.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.query.MalformedQueryException;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.PermissionAssignment;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.filter.ResourceSetFilter;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.table.comparator.MapComparator;
import org.objectledge.table.comparator.StringComparator;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.CmsTool;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.structure.table.NavigationTableModel;
import net.cyklotron.cms.style.StyleService;
import net.cyklotron.cms.util.ProtectedViewFilter;

public class AddAlias
    extends BaseStructureScreen
{
    public AddAlias(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StructureService structureService,
        StyleService styleService, SiteService siteService, RelatedService relatedService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        structureService, styleService, siteService, relatedService);
    }

    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {

        Long site_id = parameters.getLong("site_id", -1L);
        try
        {
            ArrayList<Map<String, String>> siteList = getSiteList(coralSession, i18nContext);
            templatingContext.put("site_list", siteList);

            SiteResource site = (SiteResource)coralSession.getStore().getResource(site_id);
            NavigationNodeResource homepage = structureService.getRootNode(coralSession, site);
            TableState state = tableStateManager.getState(context, this.getClass().getName());

            state.setRootId(homepage.getIdString());
            state.setExpanded(homepage.getIdString());
            state.setAllExpanded(false);
            if(state.isNew())
            {
                state.setTreeView(true);
                state.setShowRoot(true);
                state.setSortColumnName("sequence");
            }
            TableModel model = new NavigationTableModel(coralSession, i18nContext.getLocale());
            ArrayList filters = new ArrayList();
            filters.add(new ProtectedViewFilter(coralSession, coralSession.getUserSubject()));

            String search = parameters.get("search", "");
            templatingContext.put("search", search);
            if(search.length() != 0)
            {
                String query = "FIND RESOURCE FROM documents.document_node WHERE title LIKE_NC '%" + search + "%'";
                if(search.matches("([0-9]*)")){
                    query += " OR id = " + search;  
                }
                QueryResults results = coralSession.getQuery().executeQuery(query);
                filters.add(new ResourceSetFilter(results.getList(1), true));
                state.setAllExpanded(true);
            }
            TableTool tabelTool = new TableTool(state, filters, model);
            templatingContext.put("table", tabelTool);
            templatingContext.put("site_id", site.getId());
            templatingContext.put("node_id", parameters.getLong("node_id"));
            templatingContext.put("res_alias_filter", new CheckAliasPermission(coralSession));
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("Site does not exist", e);
        }
        catch(StructureException e)
        {
            throw new ProcessingException("Site does not exist", e);
        }
        catch(MalformedQueryException e)
        {
            throw new ProcessingException("Query exception", e);
        }
        catch(TableException e)
        {
            throw new ProcessingException("Cannot create TableTool", e);
        }

    }

    private ArrayList<Map<String, String>> getSiteList(CoralSession coralSession,
        I18nContext i18nContext)
        throws ProcessingException
    {
        Set<SiteResource> linkableSites = new HashSet<SiteResource>();
        Permission addInboundAliasPermission = coralSession.getSecurity().getUniquePermission(
            "cms.structure.add_inbound_alias");
        Permission viewPermission = coralSession.getSecurity().getUniquePermission(
            "cms.structure.view");
        Subject userSubject = coralSession.getUserSubject();
        for(PermissionAssignment pa : addInboundAliasPermission.getPemrissionAssignments())
        {
            if(userSubject.hasRole(pa.getRole())
                && userSubject.hasPermission(pa.getResource(), viewPermission))
            {
                SiteResource site = CmsTool.getSite(pa.getResource());
                linkableSites.add(site);
            }
        }

        ArrayList<Map<String, String>> siteList = new ArrayList<Map<String, String>>();
        for(SiteResource site : linkableSites)
        {
            Map<String, String> siteDesc = new HashMap<String, String>();
            siteDesc.put("id", site.getIdString());
            siteDesc.put("name", site.getName());
            siteList.add(siteDesc);
        }
        Collections.sort(siteList, new MapComparator<String, String>("name", StringComparator
            .getInstance(i18nContext.getLocale())));
        return siteList;
    }

    public class CheckAliasPermission
    {
        private final Permission addInboundAliasPermission;

        private final Permission viewPermission;

        private final CoralSession coralSession;

        public CheckAliasPermission(CoralSession coralSession)
        {
            this.addInboundAliasPermission = coralSession.getSecurity().getUniquePermission(
                "cms.structure.add_inbound_alias");
            this.viewPermission = coralSession.getSecurity().getUniquePermission("cms.structure.view");
            this.coralSession = coralSession;
        }

        public boolean accept(Resource resource)
        {
            if(resource.getResourceClass().getName().equals("documents.document_node"))
            {
                Subject userSubject = coralSession.getUserSubject();
                return userSubject.hasPermission(resource, viewPermission)
                    && userSubject.hasPermission(resource, addInboundAliasPermission);
            }
            else
            {
                return false;
            }
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        if(parameters.isDefined("site_id"))
        {
            return true; // checkPermission(context, coralSession, "cms.");
        }
        else
        {
            return true; // checkPermission(context, coralSession, "cms.");
        }
    }

}
