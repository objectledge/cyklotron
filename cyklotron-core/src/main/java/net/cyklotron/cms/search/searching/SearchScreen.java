package net.cyklotron.cms.search.searching;

import java.util.ArrayList;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.search.ExternalPoolResource;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.search.searching.cms.LuceneSearchHandler;
import net.cyklotron.cms.site.SiteResource;

import org.apache.lucene.search.Query;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.Instantiator;
import org.objectledge.coral.modules.views.BaseCoralView;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableException;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.table.generic.EmptyTableModel;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.mvc.MVCContext;

/**
 * Searching implementation.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchScreen.java,v 1.4 2005-01-20 10:31:13 pablo Exp $
 */
public class SearchScreen
    extends BaseCoralView
{
    /** logging facility */
    private Logger logger;

    /** search service for getting searchers. */
    private SearchService searchService;
 
    private IntegrationService integrationService;

    /** table service for hit list display. */
    private TableStateManager tableStateManager;

    private CmsDataFactory cmsDataFactory;
    
    private TableFilter filter;
    
    private Instantiator instantiator;

    public SearchScreen(Context templatingContext, Logger logger, 
          TableStateManager tableStateManager, SearchService searchService,
          IntegrationService integrationService, CmsDataFactory cmsDataFactory,
          TableFilter filter, Instantiator instantiator)
    {
        super(templatingContext);
        this.logger = logger;
        this.searchService = searchService;
        this.tableStateManager = tableStateManager;
        this.integrationService = integrationService;
        this.cmsDataFactory = cmsDataFactory;
        this.filter = filter;
        this.instantiator = instantiator;
    }
    
    /**
     * {@inheritDoc}
     */
    public void process(Parameters parameters, TemplatingContext templatingContext,
                        MVCContext mvcContext, 
                        I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        // determine search method
        SearchMethod method = null;
        if(parameters.get("field",null) != null)
        {
            method =
                new AdvancedSearchMethod(searchService, parameters, i18nContext.getLocale());
        }
        else
        {
            method =
                new SimpleSearchMethod(searchService, parameters, i18nContext.getLocale());
        }

        // get the query
        Query query = null;
        try
        {
            query = method.getQuery(coralSession);
            templatingContext.put("query", method.getQueryString(coralSession));
        }
        catch(Exception e)
        {
            query = null;
            templatingContext.put("query", method.getErrorQueryString());
            templatingContext.put("result", "bad_query");
            return;
        }

        // do not search if there is no query
        if(query == null)
        {
            return;
        }

        SiteResource site = cmsData.getSite();

        // get pools
        Resource[] pools = null;
        try
        {
            long poolId = parameters.getLong("pool_id",-1);
            if(poolId == -1)
            {
                Resource parent = searchService.getPoolsRoot(coralSession, site);
                pools = coralSession.getStore().getResource(parent);
            }
            else
            {
                Resource poolResource = coralSession.getStore().getResource(poolId);
                templatingContext.put("selected_pool", poolResource);
                pools = new Resource[1];
                pools[0] = poolResource;
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e).toString());
            logger.error("cannot get a chosen index pool", e);
            return;
        }


        // search
        // - prepare display state
        TableState state = tableStateManager.getState(context, "cms.search.results."+site.getName());
        method.setupTableState(state);
        // - prepare search handler
        SearchHandler searchHandler = null;
        if(pools.length == 1 && pools[0] instanceof ExternalPoolResource)
        {
            ExternalPoolResource extPool = (ExternalPoolResource)pools[0];
            try
            {
                Class clazz = instantiator.loadClass(extPool.getSearchHandler());
                searchHandler = (SearchHandler)(instantiator.newInstance(clazz));
            }
            catch(Exception e)
            {
                templatingContext.put("result", "exception");
                templatingContext.put("trace", new StackTrace(e).toString());
                logger.error("cannot instantiate search handler", e);
                return;
            }
        }
        else
        {
            searchHandler = 
                new LuceneSearchHandler(context, searchService, integrationService, cmsDataFactory);
        }
        // - execute seach and put results into the templatingContext
        TableTool hitsTable = null;
        try
        {
            ArrayList filters = new ArrayList();
            filters.add(filter);
            hitsTable = searchHandler.search(coralSession, pools, method, state, filters, parameters, i18nContext);
        }
        catch(SearchingException e1)
        {
            throw new ProcessingException("Problem while searching", e1);
        }
        if(hitsTable == null)
        {
            try
            {
                hitsTable = new TableTool(state, null, new EmptyTableModel());
            }
            catch(TableException e)
            {
                // there is nothing we can do
            }
        }
        templatingContext.put("hits_table", hitsTable);
    }
}
