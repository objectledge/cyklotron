package net.cyklotron.cms.search.searching;

import java.util.ArrayList;

import org.apache.lucene.search.Query;

import net.labeo.services.ServiceBroker;
import net.labeo.services.logging.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.CoralSession;
import net.labeo.services.table.EmptyTableModel;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableFilter;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.util.ObjectUtils;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.search.ExternalPoolResource;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.search.searching.cms.LuceneSearchHandler;
import net.cyklotron.cms.site.SiteResource;

/**
 * Searching implementation.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchScreen.java,v 1.3 2005-01-20 06:52:40 pablo Exp $
 */
public class SearchScreen
{
    /** logging facility */
    private Logger log;

    /** search service for getting searchers. */
    private SearchService searchService;
    private IntegrationService integrationService;

    /** resource service */
    private CoralSession resourceService;

    /** table service for hit list display. */
    private TableService tableService;
                            
    private TableFilter filter;

    public SearchScreen(ServiceBroker broker, TableFilter filter)
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(SearchService.LOGGING_FACILITY);

        resourceService = (CoralSession)broker.getService(CoralSession.SERVICE_NAME);
        searchService = (SearchService)broker.getService(SearchService.SERVICE_NAME);
        integrationService = (IntegrationService)broker.getService(IntegrationService.SERVICE_NAME);
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
        
        this.filter = filter;
    }
    
    public void prepare(RunData data, Context context)
        throws ProcessingException
    {
        // determine search method
        SearchMethod method = null;
        if(data.getParameters().get("field").asString(null) != null)
        {
            method =
                new AdvancedSearchMethod(searchService, data.getParameters(), data.getLocale());
        }
        else
        {
            method =
                new SimpleSearchMethod(searchService, data.getParameters(), data.getLocale());
        }

        // get the query
        Query query = null;
        try
        {
            query = method.getQuery();
            context.put("query", method.getQueryString());
        }
        catch(Exception e)
        {
            query = null;
            context.put("query", method.getErrorQueryString());
            context.put("result", "bad_query");
            return;
        }

        // do not search if there is no query
        if(query == null)
        {
            return;
        }

        SiteResource site = CmsData.getCmsData(data).getSite();

        // get pools
        Resource[] pools = null;
        try
        {
            long poolId = data.getParameters().get("pool_id").asLong(-1);
            if(poolId == -1)
            {
                Resource parent = searchService.getPoolsRoot(site);
                pools = resourceService.getStore().getResource(parent);
            }
            else
            {
                Resource poolResource = resourceService.getStore().getResource(poolId);
                context.put("selected_pool", poolResource);
                pools = new Resource[1];
                pools[0] = poolResource;
            }
        }
        catch(EntityDoesNotExistException e)
        {
            context.put("result", "exception");
            context.put("trace", StringUtils.stackTrace(e));
            log.error("cannot get a chosen index pool", e);
            return;
        }
        catch(SearchException e)
        {
            context.put("result", "exception");
            context.put("trace", StringUtils.stackTrace(e));
            log.error("problem while retrieving index pools", e);
            return;
        }


        // search
        // - prepare display state
        TableState state = tableService.getGlobalState(data, "cms.search.results."+site.getName());
        method.setupTableState(state);
        // - prepare search handler
        SearchHandler searchHandler = null;
        if(pools.length == 1 && pools[0] instanceof ExternalPoolResource)
        {
            ExternalPoolResource extPool = (ExternalPoolResource)pools[0];
            searchHandler = (SearchHandler)(ObjectUtils.instantiate(extPool.getSearchHandler()));
        }
        else
        {
            searchHandler = 
                new LuceneSearchHandler(searchService, resourceService, integrationService);
        }
        // - execute seach and put results into the context
        TableTool hitsTable = null;
        try
        {
            ArrayList filters = new ArrayList();
            filters.add(filter);
            hitsTable = searchHandler.search(pools, method, state, filters);
        }
        catch(SearchingException e1)
        {
            throw new ProcessingException("Problem while searching", e1);
        }
        if(hitsTable == null)
        {
            try
            {
                hitsTable = new TableTool(state, new EmptyTableModel(), null);
            }
            catch(TableException e)
            {
                // there is nothing we can do
            }
        }
        
        context.put("hits_table", hitsTable);
    }
}
