package net.cyklotron.cms.modules.components.documents;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.cache.CacheFactory;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableException;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableRow;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.table.generic.EmptyTableModel;
import org.objectledge.table.generic.ListTableModel;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.documents.internal.CalendarEventsSearchMethod;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.search.searching.HitsViewPermissionFilter;
import net.cyklotron.cms.search.searching.SearchHandler;
import net.cyklotron.cms.search.searching.SearchHit;
import net.cyklotron.cms.search.searching.SearchingException;
import net.cyklotron.cms.search.searching.cms.LuceneSearchHandler;
import net.cyklotron.cms.search.searching.cms.LuceneSearchHit;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.ComponentDataCacheService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.util.SiteFilter;

/**
 * CalendarEvents component displays calendar events.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: CalendarEvents.java,v 1.8 2007-11-18 20:55:43 rafal Exp $
 */
public class CalendarEvents
    extends SkinableCMSComponent
{
    protected StructureService structureService;

    protected SearchService searchService;

    protected CacheFactory cacheService;

    protected IntegrationService integrationService;

    protected TableStateManager tableStateManager;

    protected CategoryQueryService categoryQueryService;

    protected SiteService siteService;

    private final ComponentDataCacheService componentDataCacheService;

    public CalendarEvents(org.objectledge.context.Context context, Logger logger,
        Templating templating, CmsDataFactory cmsDataFactory, SkinService skinService,
        MVCFinder mvcFinder, StructureService structureService, SearchService searchService,
        CacheFactory cacheFactory, IntegrationService integrationService,
        CategoryQueryService categoryQueryService, SiteService siteService,
        TableStateManager tableStateManager, ComponentDataCacheService componentDataCacheService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder);
        this.structureService = structureService;
        this.searchService = searchService;
        this.cacheService = cacheFactory;
        this.integrationService = integrationService;
        this.tableStateManager = tableStateManager;
        this.categoryQueryService = categoryQueryService;
        this.siteService = siteService;
        this.componentDataCacheService = componentDataCacheService;
    }

    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            Parameters config = getConfiguration();
            TableState state = tableStateManager.getState(context,
                "cached.cms.documents.calendar.events.results");
            state.setRootId(null);
            state.setTreeView(false);

            // - execute seach and put results into the context
            SearchHit[] hits = getHits(config, coralSession, i18nContext, parameters);
            if(hits == null)
            {
                return;
            }
            TableTool hitsTable = new TableTool(state, null, new ListTableModel(hits,
                (TableColumn[])null));
            templatingContext.put("hits_table", hitsTable);
        }
        catch(TableException e)
        {
            cmsDataFactory.getCmsData(context).getComponent()
                .error("Error preparing table tool", e);
        }
    }

    protected SearchHit[] getHits(Parameters config, CoralSession coralSession,
        I18nContext i18nContext, Parameters parameters)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        int cacheInterval = config.getInt("cacheInterval", 0);
        if(cacheInterval > 0L)
        {
            Object guard = componentDataCacheService.getGuard(cmsData, null);
            synchronized(guard)
            {
                SearchHit[] results = componentDataCacheService.getCachedData(cmsData, null);
                if(results == null)
                {
                    results = getHits2(config, coralSession, i18nContext, parameters);
                    componentDataCacheService.setCachedData(cmsData, null, results, cacheInterval);
                }
                return results;
            }
        }
        else
        {
            logger.warn("non-cachable category query results screen nodeId="
                + cmsData.getNode().getIdString());
            return getHits2(config, coralSession, i18nContext, parameters);
        }
    }

    private SearchHit[] getHits2(Parameters config, CoralSession coralSession,
        I18nContext i18nContext, Parameters parameters)
        throws ProcessingException
    {
        int offset = config.getInt("offset", 0);

        Calendar calendar = Calendar.getInstance(i18nContext.getLocale());
        CmsData cmsData = cmsDataFactory.getCmsData(context);

        Date startDate = null;
        Date endDate = null;

        calendar.setTime(cmsData.getDate());
        int day = parameters.getInt("day", calendar.get(java.util.Calendar.DAY_OF_MONTH));
        int month = parameters.getInt("month", calendar.get(java.util.Calendar.MONTH) + 1);
        int year = parameters.getInt("year", calendar.get(java.util.Calendar.YEAR));
        calendar.set(java.util.Calendar.DAY_OF_MONTH, day);
        calendar.set(java.util.Calendar.MONTH, month - 1);
        calendar.set(java.util.Calendar.YEAR, year);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        startDate = calendar.getTime();
        calendar.setTime(cmsData.getDate());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.add(Calendar.DAY_OF_MONTH, offset);
        endDate = calendar.getTime();

        Resource[] pools = null;
        try
        {
            long indexId = config.getLong("index_id", -1);
            if(indexId == -1)
            {
                SiteResource site = cmsData.getSite();
                Resource parent = searchService.getPoolsRoot(coralSession, site);
                pools = coralSession.getStore().getResource(parent);
            }
            else
            {
                Resource index = coralSession.getStore().getResource(indexId);
                pools = new Resource[1];
                pools[0] = index;
            }

            CalendarEventsSearchMethod method = new CalendarEventsSearchMethod(searchService,
                config, i18nContext.getLocale(), logger, startDate, endDate, "");
            TableFilter filter = new HitsViewPermissionFilter(coralSession.getUserSubject(),
                coralSession);
            TableState state = tableStateManager.getState(context,
                "cms.documents.calendar.events.results");
            method.setupTableState(state);

            // - prepare search handler
            SearchHandler searchHandler = new LuceneSearchHandler(context, searchService,
                integrationService, cmsDataFactory);

            // - execute seach and put results into the context
            ArrayList filters = new ArrayList();
            filters.add(filter);

            String categoryQueryName = config.get("categoryQueryName", "");
            Resource[] queries = coralSession.getStore().getResource(
                categoryQueryService.getCategoryQueryRoot(coralSession, cmsData.getSite()),
                categoryQueryName);

            if(queries.length == 1)
            {
                CategoryQueryResource categoryQuery = (CategoryQueryResource)queries[0];
                String[] siteNames = categoryQuery.getAcceptedSiteNames();
                Resource[] resources = categoryQueryService.forwardQuery(coralSession,
                    categoryQuery.getQuery());

                SiteFilter siteFilter = null;
                List<Long> resSiteList = new ArrayList<Long>();
                if(siteNames != null && siteNames.length > 0)
                {
                    siteFilter = new SiteFilter(coralSession, siteNames, siteService);
                    for(Resource res : resources)
                    {
                        if(siteFilter.accept(res))
                        {
                            resSiteList.add(res.getId());
                        }
                    }
                }

                if(resSiteList != null)
                {
                    final List<Long> resList = resSiteList;
                    TableFilter<Object> hitsCategoryFilter = new TableFilter<Object>()
                        {

                            public boolean accept(Object object)
                            {
                                if(object instanceof LuceneSearchHit)
                                {
                                    LuceneSearchHit hit = (LuceneSearchHit)object;
                                    return resList.contains(hit.getId());
                                }
                                else
                                {
                                    return true;
                                }
                            }
                        };
                    filters.add(hitsCategoryFilter);
                }
            }

            TableModel hitsTableModel = searchHandler.search(coralSession, pools, method, state, parameters, i18nContext); 
            TableTool hitsTable = new TableTool(state, filters, hitsTableModel);
            
            if(hitsTable == null)
            {
                hitsTable = new TableTool(state, null, new EmptyTableModel());
            }
            List rows = hitsTable.getRows();
            SearchHit[] searchHits = new SearchHit[rows.size()];
            int i = 0;
            for(Iterator iter = rows.iterator(); iter.hasNext(); i++)
            {
                TableRow row = (TableRow)iter.next();
                searchHits[i] = (SearchHit)(row.getObject());
            }
            return searchHits;
        }
        catch(TableException e)
        {
            cmsData.getComponent().error("Error preparing table tool", e);
            return null;
        }
        catch(SearchingException e)
        {
            cmsData.getComponent().error("Error while searching", e);
            return null;
        }
        catch(SearchException e)
        {
            cmsData.getComponent().error("Cannot get index pool", e);
            return null;
        }
        catch(Exception e)
        {
            cmsData.getComponent().error("Cannot execute category query", e);
            return null;
        }
    }
}
