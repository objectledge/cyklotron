package net.cyklotron.cms.modules.views.documents;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryPoolResourceImpl;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.documents.calendar.CalendarSearchParameters;
import net.cyklotron.cms.documents.calendar.CalendarSearchService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.search.PoolResource;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.search.searching.HitsViewPermissionFilter;
import net.cyklotron.cms.search.searching.cms.LuceneSearchHit;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Calendar.java,v 1.8 2008-10-30 17:54:28 rafal Exp $
 */
public class CalendarEvents
    extends BaseSkinableDocumentScreen
{
    /** search serivce for analyzer nad searcher getting. */
    protected SiteService siteService;

    protected SearchService searchService;

    protected IntegrationService integrationService;

    protected CategoryQueryService categoryQueryService;

    private final CalendarSearchService searchUtil;

    public CalendarEvents(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        SiteService siteService, StructureService structureService,
        CategoryQueryService categoryQueryService, StyleService styleService,
        SkinService skinService, MVCFinder mvcFinder, TableStateManager tableStateManager,
        SearchService searchService, IntegrationService integrationService,
        CalendarSearchService searchUtil)
    {
        super(context, logger, preferencesService, cmsDataFactory, structureService, styleService,
                        skinService, mvcFinder, tableStateManager);
        this.siteService = siteService;
        this.searchService = searchService;
        this.integrationService = integrationService;
        this.categoryQueryService = categoryQueryService;
        this.searchUtil = searchUtil;
    }

    @Override
    public void prepareDefault(Context context)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        final CoralSession coralSession = context.getAttribute(CoralSession.class);
        I18nContext i18nContext = I18nContext.getI18nContext(context);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        Parameters config = getScreenConfig();

        try
        {
            Set<PoolResource> indexPools = searchUtil.searchPools(config, coralSession, cmsData);

            CalendarSearchParameters searchParameters;
            int offset = parameters.getInt("period", 1) - 1; // offset = 0 means one full day
            if(parameters.isDefined("year") && parameters.isDefined("month")
                && parameters.isDefined("day"))
            {
                searchParameters = new CalendarSearchParameters(parameters.getInt("year"),
                    parameters.getInt("month"), parameters.getInt("day"), offset,
                    i18nContext.getLocale(), indexPools);
            }
            else
            {
                searchParameters = new CalendarSearchParameters(cmsData.getDate(), offset,
                    i18nContext.getLocale(), indexPools);
            }

            String range = parameters.get("range", "ongoing");
            String textQuery = parameters.get("text_query", "");
            searchParameters.setTextQuery(textQuery);

            long queryId = parameters.getLong("query_id", -1);
            if(queryId != -1)
            {
                CategoryQueryResource categoryQuery = (CategoryQueryResource)coralSession
                    .getStore().getResource(queryId);
                searchParameters.setCategoryQuery(categoryQuery);
            }

            templatingContext.put("day", searchParameters.getStartDay());
            templatingContext.put("month", searchParameters.getStartMonth());
            templatingContext.put("year", searchParameters.getStartYear());
            templatingContext.put("offset", searchParameters.getEndOffset());
            templatingContext.put("range", range);
            templatingContext.put("text_query", textQuery);
            templatingContext.put("query_id", queryId);
            templatingContext.put("start_date", searchParameters.getStartDate());
            templatingContext.put("end_date", searchParameters.getEndDate());

            // category queries for selection in the UI
            long queryPoolId = config.getLong("query_pool_id", -1);
            if(queryPoolId != -1)
            {
                CategoryQueryPoolResource queryPool = CategoryQueryPoolResourceImpl
                    .getCategoryQueryPoolResource(coralSession, queryPoolId);
                templatingContext.put("queries", queryPool.getQueries());
            }

            List<TableFilter<? super LuceneSearchHit>> filters = new ArrayList<>();
            TableFilter<LuceneSearchHit> filter = new HitsViewPermissionFilter<LuceneSearchHit>(
                coralSession.getUserSubject(), coralSession);
            filters.add(filter);

            TableState state = tableStateManager.getState(context,
                "cms.documents.screens.CalendarEvents/" + cmsData.getNode().getIdString());

            TableModel<LuceneSearchHit> hitsTableModel = searchUtil.search(searchParameters, false,
                context, parameters, state, i18nContext, coralSession, templatingContext);
            TableTool<LuceneSearchHit> hitsTable = new TableTool<LuceneSearchHit>(state, filters,
                hitsTableModel);

            templatingContext.put("hits_table", hitsTable);
            prepareCategories(context, false);
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occurred", e);
        }
    }
}
