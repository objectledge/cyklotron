package net.cyklotron.cms.modules.views.documents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.NameComparator;
import net.labeo.services.table.EmptyTableModel;
import net.labeo.services.table.TableFilter;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.documents.internal.CalendarSearchMethod;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.BaseSkinableScreen;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.search.searching.HitsViewPermissionFilter;
import net.cyklotron.cms.search.searching.SearchHandler;
import net.cyklotron.cms.search.searching.SearchMethod;
import net.cyklotron.cms.search.searching.cms.LuceneSearchHandler;
import net.cyklotron.cms.site.SiteResource;

/**
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Calendar.java,v 1.1 2005-01-24 04:34:59 pablo Exp $
 */
public class Calendar
    extends BaseSkinableScreen
{
    /** search serivce for analyzer nad searcher getting. */
    protected SearchService searchService;
    private IntegrationService integrationService;

    /** table service for hit list display. */
    TableService tableService;

    /** logging facility */
    protected Logger log;

    public Calendar()
    {
        super();
        searchService = (SearchService)broker.getService(SearchService.SERVICE_NAME);
        integrationService = (IntegrationService)broker.getService(IntegrationService.SERVICE_NAME);
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).
            getFacility(SearchService.LOGGING_FACILITY);
    }

    public void prepareDefault(RunData data, Context context)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        
        // setup search date
    	java.util.Calendar calendar = java.util.Calendar.getInstance(i18nContext.getLocale()());
		calendar.setTime(cmsData.getDate());
		int day = parameters.get("day").asInt(calendar.get(java.util.Calendar.DAY_OF_MONTH));
		int month = parameters.get("month").asInt(calendar.get(java.util.Calendar.MONTH)+1);
		int year = parameters.get("year").asInt(calendar.get(java.util.Calendar.YEAR));
        calendar.set(java.util.Calendar.DAY_OF_MONTH, day);
        calendar.set(java.util.Calendar.MONTH, month-1);
        calendar.set(java.util.Calendar.YEAR, year);

        // setup search bounds 
        Date startDate = null;
        Date endDate = null;

        String period = parameters.get("period","daily");
        if(period.equals("daily"))
        {
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
            calendar.set(java.util.Calendar.MINUTE, 0);
            calendar.set(java.util.Calendar.SECOND, 0);
            startDate = calendar.getTime();
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 23);
            calendar.set(java.util.Calendar.MINUTE, 59);
            calendar.set(java.util.Calendar.SECOND, 59);
            endDate = calendar.getTime();
        }
        if(period.equals("monthly"))
        {
            calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
            calendar.set(java.util.Calendar.MINUTE, 0);
            calendar.set(java.util.Calendar.SECOND, 0);
            startDate = calendar.getTime();
            calendar.add(java.util.Calendar.MONTH, 1);
            calendar.add(java.util.Calendar.SECOND, -1);
            endDate = calendar.getTime();
        }
        if(period.equals("weekly"))
        {
            int dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK);
            int firstDayOfWeek = calendar.getFirstDayOfWeek();
            if(dayOfWeek > firstDayOfWeek)
            {
                calendar.add(java.util.Calendar.DAY_OF_MONTH, - (dayOfWeek-firstDayOfWeek));
            }
            if(dayOfWeek < firstDayOfWeek)
            {
                calendar.add(java.util.Calendar.DAY_OF_MONTH, - (dayOfWeek+7-firstDayOfWeek));
            }
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
            calendar.set(java.util.Calendar.MINUTE, 0);
            calendar.set(java.util.Calendar.SECOND, 0);
            startDate = calendar.getTime();
            calendar.add(java.util.Calendar.WEEK_OF_YEAR, 1);
            calendar.add(java.util.Calendar.SECOND, -1);
            endDate = calendar.getTime();
        }

        String range = parameters.get("range","all");
        long firstCatId = parameters.getLong("category_id_1", -1);
        long secondCatId = parameters.getLong("category_id_2", -1);

        templatingContext.put("day",new Integer(day));
        templatingContext.put("month",new Integer(month));
        templatingContext.put("year",new Integer(year));
        templatingContext.put("period", period);
		templatingContext.put("range", range);
		templatingContext.put("category_id_1",new Long(firstCatId));
		templatingContext.put("category_id_2",new Long(secondCatId));
        templatingContext.put("start_date", startDate);
        templatingContext.put("end_date", endDate);
		
		try
		{
			SiteResource site = cmsData.getSite();
			Parameters screenConfig = getConfiguration();
			Resource[] pools = null;
			long indexId = screenConfig.get("index_id").asLong(-1);
			if(indexId == -1)
			{
				Resource parent = searchService.getPoolsRoot(getSite());
				pools = coralSession.getStore().getResource(parent);
			}
			else
			{
				Resource index = coralSession.getStore().getResource(indexId);
				pools = new Resource[1];
				pools[0] = index;
			}
			CalendarSearchMethod method = new CalendarSearchMethod(
                searchService, parameters, i18nContext.getLocale()(), log, startDate, endDate);
			templatingContext.put("query", method.getQueryString());
			TableFilter filter = new HitsViewPermissionFilter(coralSession.getUserSubject(), coralSession);			
			TableState state = 
                tableService.getGlobalState(data, "cms.documents.calendar.results."+site.getName());
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
			templatingContext.put("hits_table", hitsTable);
			prepareCategories(data,context);
		}
		catch(Exception e)
		{
			throw new ProcessingException("Exception occurred",e);
		}
		
    }
    
	public void prepareCategories(RunData data, Context context)
		throws Exception
	{
		Parameters screenConfig = getConfiguration();
		NameComparator comparator = new NameComparator(i18nContext.getLocale()());
		long root1 = screenConfig.get("category_id_1").asLong(-1);
		long root2 = screenConfig.get("category_id_2").asLong(-1);
		if(root1 == -1)
		{
			templatingContext.put("categories_1", new ArrayList());
		}
		else
		{
			Resource resource = coralSession.getStore().getResource(root1);
			Resource[] resources = coralSession.getStore().getResource(resource);
			List list1 = new ArrayList();
			for(int i = 0; i < resources.length; i++)
			{
				if(resources[i] instanceof CategoryResource)
				{
					list1.add(resources[i]);
				}
			}
			Collections.sort(list1, comparator);
			templatingContext.put("categories_1", list1);
		}
		if(root2 == -1)
		{
			templatingContext.put("categories_2", new ArrayList());
		}
		else
		{
			Resource resource = coralSession.getStore().getResource(root2);
			Resource[] resources = coralSession.getStore().getResource(resource);
			List list2 = new ArrayList();
			for(int i = 0; i < resources.length; i++)
			{
				if(resources[i] instanceof CategoryResource)
				{
					list2.add(resources[i]);
				}
			}
			Collections.sort(list2, comparator);
			templatingContext.put("categories_2", list2);
		}
	}
}
