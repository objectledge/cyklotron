package net.cyklotron.cms.modules.components.documents;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.labeo.services.cache.CacheService;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.table.EmptyTableModel;
import net.labeo.services.table.ListTableModel;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableFilter;
import net.labeo.services.table.TableRow;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.documents.internal.CalendarSearchMethod;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.search.searching.HitsViewPermissionFilter;
import net.cyklotron.cms.search.searching.SearchHandler;
import net.cyklotron.cms.search.searching.SearchHit;
import net.cyklotron.cms.search.searching.SearchingException;
import net.cyklotron.cms.search.searching.cms.LuceneSearchHandler;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureService;

/**
 * CalendarEvents component displays calendar events.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: CalendarEvents.java,v 1.1 2005-01-24 04:35:16 pablo Exp $
 */
public class CalendarEvents
    extends SkinableCMSComponent
{
    StructureService structureService;
    SearchService searchService;
    CacheService cacheService;
    private IntegrationService integrationService;
	/** table service for hit list display. */
	TableService tableService;

    public CalendarEvents()
    {
        structureService = (StructureService)broker.getService(StructureService.SERVICE_NAME);
		searchService = (SearchService)broker.getService(SearchService.SERVICE_NAME);
        integrationService = (IntegrationService)broker.getService(IntegrationService.SERVICE_NAME);
        cacheService = (CacheService)broker.getService(CacheService.SERVICE_NAME);
		tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
			Parameters config = getConfiguration();
			TableState state = 
                tableService.getGlobalState(data, "cached.cms.documents.calendar.events.results");
			state.setRootId(null);
            state.setViewType(TableConstants.VIEW_AS_LIST);
            
			// - execute seach and put results into the context
            SearchHit[] hits = getHits(data, config);
            if(hits == null)
            {
                return;
            }
			TableTool hitsTable = new TableTool(state, new ListTableModel(hits, null), null);
            templatingContext.put("hits_table", hitsTable);
        }
        catch(TableException e)
        {
            cmsDataFactory.getCmsData(context).getComponent().error("Error preparing table tool", e);
        }
    }

    protected SearchHit[] getHits(RunData data, Parameters config)
        throws ProcessingException
    {
        long cacheInterval = (long) config.get("cacheInterval").asLong(0L);
        if(cacheInterval > 0L)
        {
            // get cache instance
            Map cache = cacheService.getInstance("calendarevents", "calendarevents");
            // create cached data key
            CmsData cmsData = cmsDataFactory.getCmsData(context); 
            String key = cmsData.getNode().getIdString()+"."+cmsData.getComponent().getInstanceName();
            // get cached data together with creation time
            CacheEntry entry = (CacheEntry) cache.get(key);
            // check entry validity
            if(entry == null ||
            System.currentTimeMillis() - entry.timeStamp > cacheInterval*1000L)
            {
                SearchHit[] list = getHits2(data, config);
                if(list == null)
                {
                    return null;
                }
                entry = new CacheEntry(list, System.currentTimeMillis());
                synchronized (cache)
                {
                    cache.put(key, entry);
                }
            }
            return entry.list;
        }
        return getHits2(data, config);
    }
    
    private class CacheEntry
    {
        SearchHit[] list;
        long timeStamp;
        /**
         * @param ress
         * @param l
         */
        public CacheEntry(SearchHit[] list, long timeStamp)
        {
            this.list = list;
            this.timeStamp = timeStamp;
        }
    }

    private SearchHit[] getHits2(RunData data, Parameters config)
        throws ProcessingException
    {
        int startOffset = config.get("start_offset").asInt(0);
        int endOffset = config.get("end_offset").asInt(0);

        Calendar calendar = Calendar.getInstance(i18nContext.getLocale()());
        CmsData cmsData = cmsDataFactory.getCmsData(context);

        Date startDate = null;
        Date endDate = null;

        calendar.setTime(cmsData.getDate());        
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, -startOffset);
        startDate = calendar.getTime();
        calendar.setTime(cmsData.getDate());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.add(Calendar.DAY_OF_MONTH, endOffset);
        endDate = calendar.getTime();

        Resource[] pools = null;
        try
        {
            long indexId = config.get("index_id").asLong(-1);
            if(indexId == -1)
            {
                SiteResource site = cmsData.getSite();
                Resource parent = searchService.getPoolsRoot(site);
                pools = coralSession.getStore().getResource(parent);
            }
            else
            {
                Resource index = coralSession.getStore().getResource(indexId);
                pools = new Resource[1];
                pools[0] = index;
            }
        }
        catch(SearchException e)
        {
            cmsData.getComponent().error("Cannot get index pool", e);
            return null;
        }
        catch(EntityDoesNotExistException e)
        {
            cmsData.getComponent().error("No index pool with selected id", e);
            return null;
        }

        try
        {
            CalendarSearchMethod method = new CalendarSearchMethod(
                searchService, config, i18nContext.getLocale()(), log, startDate, endDate);
            TableFilter filter = new HitsViewPermissionFilter(coralSession.getUserSubject(), coralSession);           
            TableState state = 
                tableService.getGlobalState(data, "cms.documents.calendar.events.results");
            method.setupTableState(state);
            
            
            // - prepare search handler
            SearchHandler searchHandler = 
                new LuceneSearchHandler(searchService, coralSession, integrationService);
                    
            // - execute seach and put results into the context
            ArrayList filters = new ArrayList();
            filters.add(filter);
            TableTool hitsTable = searchHandler.search(pools, method, state, filters, data);
            if(hitsTable == null)
            {
                hitsTable = new TableTool(state, new EmptyTableModel(), null);
            }
            List rows = hitsTable.getRows();
            SearchHit[] searchHits = new SearchHit[rows.size()];
            int i = 0;
            for (Iterator iter = rows.iterator(); iter.hasNext(); i++)
            {
                TableRow row = (TableRow) iter.next();
                searchHits[i] = (SearchHit) (row.getObject());
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
    }
}
