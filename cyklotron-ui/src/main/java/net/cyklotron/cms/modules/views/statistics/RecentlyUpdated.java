package net.cyklotron.cms.modules.views.statistics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.DateAttributeHandler;
import org.objectledge.coral.query.QueryResults;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.ModificationTimeComparator;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.database.Database;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteResourceImpl;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * CMS Statistics main screen.
 *
 */
public class RecentlyUpdated extends BaseStatisticsScreen
{
    public static final String CONFIGURATION_PREFIX = "statistics.RecentlyUpdated.";
    
    private SiteService siteService;
    
    public RecentlyUpdated(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, Database database, UserManager userManager,
        CategoryService categoryService, SiteService siteService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, database,
                        userManager, categoryService);
        this.siteService = siteService;
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) throws ProcessingException
    {
        SimpleDateFormat df = new SimpleDateFormat(DateAttributeHandler.DATE_TIME_FORMAT);
        SiteResource[] allSites = siteService.getSites(coralSession);
        List<SiteResource> allSitesList = new ArrayList<SiteResource>();
        for(SiteResource site: allSites)
        {
            allSitesList.add(site);
        }
        Collections.sort(allSitesList, new NameComparator(i18nContext.getLocale()));
        templatingContext.put("allSites", allSitesList);
        
        Parameters systemPreferences = preferencesService.getSystemPreferences(coralSession);
        Date today = new Date();
        Date updateStart = null;
        Date updateEnd = null;
        int offset = 0;
        
        // prepare the conditions...
        if (parameters.get("update_start","").length() > 0)
        {
            updateStart = new Date(parameters.getLong("update_start"));
        }
        else
        {
            updateStart = new Date(systemPreferences.getLong(CONFIGURATION_PREFIX+"update_start", today.getTime()));
        }
        templatingContext.put("update_start", updateStart);
        
        if (parameters.get("update_end","").length() > 0)
        {
            updateEnd = new Date(parameters.getLong("update_end"));
        }
        else
        {
            updateEnd = new Date(systemPreferences.getLong(CONFIGURATION_PREFIX+"update_end", today.getTime()));
        }
        templatingContext.put("update_end", updateEnd);
        if (parameters.get("offset","").length() > 0)
        {
            offset = parameters.getInt("offset");
        }
        else
        {
            offset = systemPreferences.getInt(CONFIGURATION_PREFIX+"offset",7);
        }
        templatingContext.put("offset", offset);

        long[] siteIds = parameters.getLongs("selected_site_id");
        if(siteIds.length == 0)
        {
            siteIds = systemPreferences.getLongs(CONFIGURATION_PREFIX+"selected_site_id");
        }
        Set<Long> selectedSites = new HashSet<Long>();
        List<SiteResource> sites = new ArrayList<SiteResource>();
        try
        {
            for(int i = 0; i < siteIds.length; i++)
            {
                SiteResource site = SiteResourceImpl.getSiteResource(coralSession, siteIds[i]);
                selectedSites.add(siteIds[i]);
                sites.add(site);
            }
            templatingContext.put("selectedSites", selectedSites);
            Collections.sort(sites, new NameComparator(i18nContext.getLocale()));
            templatingContext.put("sites", sites);
        }
        catch (Exception e)
        {
            throw new ProcessingException("Exception during data preparation", e);
        }
        
        boolean range = false;
        if(parameters.isDefined("range"))
        {
            range = parameters.getBoolean("range", false);
        }
        else
        {
            range = systemPreferences.getBoolean(CONFIGURATION_PREFIX+"range", false);
        }
        templatingContext.put("range", range);
        if(!range)
        {
            Calendar calendar = Calendar.getInstance();
            updateEnd = calendar.getTime();
            calendar.add(Calendar.MINUTE, -1 *  offset);
            updateStart = calendar.getTime();
        }
        StringBuilder sb = new StringBuilder("FIND RESOURCE FROM documents.document_node" +
                " WHERE customModificationTime > '");
        sb.append(df.format(updateStart));
        sb.append("'");
        if (range) 
        {
            sb.append(" AND ");
            sb.append("customModificationTime < '");
            sb.append(df.format(updateEnd));
            sb.append("'");
        }
        String query = sb.toString();
        templatingContext.put("query", query);

        
        HashMap<Long, List<NavigationNodeResource>> sitesMap = new HashMap<Long, List<NavigationNodeResource>>();
        try
        {
            QueryResults results = coralSession.getQuery().executeQuery(query);
            Resource[] nodes = results.getArray(1);
            for (int i = 0; i < nodes.length; i++)
            {
                if(nodes[i] instanceof NavigationNodeResource)
                {
                    NavigationNodeResource node = (NavigationNodeResource)nodes[i];
                    if(node != null)
                    {
                        long siteId = node.getSite().getId();
                        if(selectedSites.contains(siteId))
                        {
                            List<NavigationNodeResource> list = sitesMap.get(siteId);
                            if(list == null)
                            {
                                list = new ArrayList<NavigationNodeResource>();
                                sitesMap.put(siteId, list);
                            }
                            list.add(node);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new ProcessingException("Exception occured during query execution", e);
        }
        Comparator modificationTimeComparator = new ModificationTimeComparator();
        for(List<NavigationNodeResource> list : sitesMap.values())
        {
            Collections.sort(list, modificationTimeComparator);
            Collections.reverse(list);
        }
        templatingContext.put("sitesMap", sitesMap);
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        try
        {
            Role role = coralSession.getSecurity().getUniqueRole("cms.administrator");
            return coralSession.getUserSubject().hasRole(role);
        }
        catch(Exception e)
        {
            logger.error("Subject has no rights to view this screen",e);
            return false;
        }
    }
}