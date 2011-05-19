package net.cyklotron.cms.documents.internal;

import java.util.ArrayList;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableRow;
import org.objectledge.table.TableState;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.query.CategoryQueryException;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.search.SearchService;
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

    private final CategoryQueryService categoryQueryService;

    public CalendarEventsSearchUtil(SearchService searchService,
        IntegrationService integrationService, CmsDataFactory cmsDataFactory,
        CategoryQueryService catetoryQueryService, Logger logger)
    {
        this.searchService = searchService;
        this.integrationService = integrationService;
        this.cmsDataFactory = cmsDataFactory;
        this.categoryQueryService = catetoryQueryService;
        this.logger = logger;
    }

    public TableModel<? extends SearchHit> search(CalendarEventsSearchParameters searchParameters,
        Context context, Parameters parameters, I18nContext i18nContext, CoralSession coralSession)
        throws SearchingException
    {
        CalendarEventsSearchMethod method = new CalendarEventsSearchMethod(searchService,
            parameters, i18nContext.getLocale(), searchParameters, logger);

        LuceneSearchHandler searchHandler = new LuceneSearchHandler(context, searchService,
            integrationService, cmsDataFactory);

        Resource[] pools = searchParameters.getIndexPools().toArray(
            new Resource[searchParameters.getIndexPools().size()]);
        TableModel<LuceneSearchHit> hitsTableModel = searchHandler.search(coralSession, pools,
            method, null, parameters, i18nContext);

        if(searchParameters.getCategoryQuery() == null)
        {
            return hitsTableModel;
        }
        else
        {
            TableState allHits = new TableState("<local>", -1);
            allHits.setPageSize(-1);

            TableRow<LuceneSearchHit>[] rows = hitsTableModel.getRowSet(allHits, null).getRows();
            LongSet docIds = new LongOpenHashSet(rows.length);
            for(TableRow<LuceneSearchHit> row : rows)
            {
                docIds.add(row.getObject().getId());
            }

            try
            {
                // run category query, limited to document set returned by lucene search
                docIds = categoryQueryService.forwardQueryIds(coralSession, searchParameters
                    .getCategoryQuery().getQuery(), docIds);

                // retain only those documents present in category query results
                List<LuceneSearchHit> filteredHits = new ArrayList<LuceneSearchHit>(docIds.size());
                for(TableRow<LuceneSearchHit> row : rows)
                {
                    if(docIds.contains(row.getObject().getId()))
                    {
                        filteredHits.add(row.getObject());
                    }
                }

                return searchHandler.hitsTableModel(filteredHits, coralSession);
            }
            catch(CategoryQueryException e)
            {
                throw new SearchingException("category query failed", e);
            }
        }
    }
}
