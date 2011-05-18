package net.cyklotron.cms.documents.internal;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableRow;
import org.objectledge.table.TableRowSet;
import org.objectledge.table.TableState;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.search.searching.SearchHandler;
import net.cyklotron.cms.search.searching.SearchHit;
import net.cyklotron.cms.search.searching.SearchingException;
import net.cyklotron.cms.search.searching.cms.LuceneSearchHandler;
import net.cyklotron.cms.search.searching.cms.LuceneSearchHit;

import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

public class CalendarEventsSearchUtil
{
    private final SearchService searchService;

    private final Logger logger;

    private final IntegrationService integrationService;

    private final CmsDataFactory cmsDataFactory;

    public CalendarEventsSearchUtil(SearchService searchService,
        IntegrationService integrationService, CmsDataFactory cmsDataFactory, Logger logger)
    {
        this.searchService = searchService;
        this.integrationService = integrationService;
        this.cmsDataFactory = cmsDataFactory;
        this.logger = logger;
    }

    public TableModel<? extends SearchHit> search(CalendarEventsSearchParameters searchParameters,
        Context context, Parameters parameters, I18nContext i18nContext, CoralSession coralSession)
        throws SearchingException
    {
        CalendarEventsSearchMethod method = new CalendarEventsSearchMethod(searchService,
            parameters, i18nContext.getLocale(), searchParameters, logger);

        SearchHandler<LuceneSearchHit> searchHandler = new LuceneSearchHandler(context, searchService,
            integrationService, cmsDataFactory);

        TableState allHits = new TableState("<local>", -1);
        allHits.setPageSize(-1);
        
        Resource[] pools = searchParameters.getIndexPools().toArray(
            new Resource[searchParameters.getIndexPools().size()]);
        TableModel<LuceneSearchHit> hitsTableModel = searchHandler.search(coralSession, pools, method,
            allHits, parameters, i18nContext);
        
        TableRow<LuceneSearchHit>[] hits = hitsTableModel.getRowSet(allHits, null).getRows();
        LongSet docIds = new LongOpenHashSet(hits.length);
        for(TableRow<LuceneSearchHit> hit : hits)
        {
            docIds.add(hit.getObject().getId());
        }

        // run category query with docIds initial set
        
        // intersect lucene search results wiht category query results
        
        return hitsTableModel;
    }
}
