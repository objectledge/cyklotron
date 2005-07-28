package net.cyklotron.cms.modules.views.structure;

import java.util.ArrayList;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.structure.table.NavigationTableModel;
import net.cyklotron.cms.style.StyleService;
import net.cyklotron.cms.util.SiteFilter;

/**
 * Navigation node information screen.
 */
public class MoveToArchive
    extends BaseStructureScreen
{
        
    
    public MoveToArchive(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StructureService structureService,
        StyleService styleService, SiteService siteService, RelatedService relatedService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        structureService, styleService, siteService, relatedService);
        
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long srcNodeId = parameters.getLong("src_node_id");
            templatingContext.put("src_node_id", srcNodeId);
            
            boolean reset = parameters.getBoolean("reset",false);
            boolean selected = parameters.isDefined("dst_node_id");
            
            NavigationNodeResource homePage = getHomePage();
            Parameters prefs = preferencesService.getNodePreferences(homePage);
            boolean defaultDefined = prefs.isDefined(ARCHIVE_DESTINATION_PAGE_ID);
            boolean confirmation = false;
            if(selected || (defaultDefined && !reset))
            {
                confirmation = true;
            }
            templatingContext.put("confirmation", confirmation);
            
            if(confirmation)
            {
                NavigationNodeResource dstNode = null;
                if(selected)
                {
                    dstNode = NavigationNodeResourceImpl.
                        getNavigationNodeResource(coralSession, parameters.getLong("dst_node_id"));
                }
                else
                {
                    dstNode = NavigationNodeResourceImpl.
                        getNavigationNodeResource(coralSession, prefs.getLong(ARCHIVE_DESTINATION_PAGE_ID));
                }
                templatingContext.put("destinationNode", dstNode);
                NavigationNodeResource srcNode = NavigationNodeResourceImpl.
                        getNavigationNodeResource(coralSession, srcNodeId);
                templatingContext.put("sourceNode", srcNode);
            }
            else
            {
                Resource rootNode = siteService.getSitesRoot(coralSession);
                TableState state = tableStateManager.getState(context, getClass().getName());
                if(state.isNew())
                {
                    state.setRootId(rootNode.getIdString());
                    state.setExpanded(rootNode.getIdString());
                    state.setTreeView(true);
                    state.setShowRoot(false);
                }
                Subject user = coralSession.getUserSubject();
                TableModel model = new NavigationTableModel(coralSession, i18nContext.getLocale());
                ArrayList<TableFilter> filters = new ArrayList<TableFilter>();
                SiteResource[] sites = siteService.getSites(coralSession);
                List<SiteResource> accepted = new ArrayList<SiteResource>();
                for(SiteResource site: sites)
                {
                    Role admin = site.getAdministrator();
                    if(admin != null && user.hasRole(admin))
                    {
                        accepted.add(site);
                    }
                }
                SiteResource[] acceptedSites = new SiteResource[accepted.size()];
                accepted.toArray(acceptedSites);
                filters.add(new SiteFilter(acceptedSites));
                filters.add(new TreeFilter(homePage.getSite()));
                TableTool helper = new TableTool(state, filters, model);
                templatingContext.put("table", helper);
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occured", e);
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkAdministrator(coralSession);
    }
    
    private class TreeFilter implements TableFilter
    {
        private SiteResource site;
        
        public TreeFilter(SiteResource site)
        {
            this.site = site;
        }
        
        public boolean accept(Object o)
        {
            if(!(o instanceof Resource))
            {
                return false;
            }
            if(o instanceof SiteResource)
            {
                return !site.equals(o);
            }
            if(o instanceof NavigationNodeResource)
            {
                return true;
            }
            if(((Resource)o).getName().equals("structure"))
            {
                return true;
            }
            return false;
        }
    }
}
