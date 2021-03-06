package net.cyklotron.cms.modules.components.documents;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.cache.CacheFactory;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableException;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.documents.calendar.CalendarSearchParameters;
import net.cyklotron.cms.documents.calendar.CalendarSearchService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.search.PoolResource;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.search.searching.HitsViewPermissionFilter;
import net.cyklotron.cms.search.searching.SearchingException;
import net.cyklotron.cms.search.searching.cms.LuceneSearchHit;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;

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

    private final CalendarSearchService searchUtil;

    public CalendarEvents(org.objectledge.context.Context context, Logger logger,
        Templating templating, CmsDataFactory cmsDataFactory, SkinService skinService,
        MVCFinder mvcFinder, StructureService structureService, SearchService searchService,
        CacheFactory cacheFactory, IntegrationService integrationService,
        CategoryQueryService categoryQueryService, SiteService siteService,
        TableStateManager tableStateManager, CalendarSearchService searchUtil)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder);
        this.structureService = structureService;
        this.searchService = searchService;
        this.cacheService = cacheFactory;
        this.integrationService = integrationService;
        this.tableStateManager = tableStateManager;
        this.categoryQueryService = categoryQueryService;
        this.siteService = siteService;
        this.searchUtil = searchUtil;
    }

    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        Parameters config = getConfiguration();
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        try
        {
            Set<PoolResource> indexPools = searchUtil.searchPools(config, coralSession, cmsData);

            CalendarSearchParameters searchParameters = new CalendarSearchParameters(cmsData.getDate(), config.getInt(
                    "offset", 0), i18nContext.getLocale(), indexPools);

            String categoryQueryName = config.get("categoryQueryName", "");
            Resource[] queries = coralSession.getStore().getResource(
                categoryQueryService.getCategoryQueryRoot(coralSession, cmsData.getSite()),
                categoryQueryName);
            if(queries.length == 1)
            {
                CategoryQueryResource categoryQuery = (CategoryQueryResource)queries[0];
                searchParameters.setCategoryQuery(categoryQuery);
            }

            TableState state = tableStateManager.getState(context,
                "cms.documents.components.CalendarEvents/" + cmsData.getNode().getIdString() + "/"
                + cmsData.getComponent().getInstanceName());

            TableModel<LuceneSearchHit> hitsTableModel = searchUtil.search(searchParameters, false,
                context, config, state, i18nContext, coralSession, templatingContext);

            List<TableFilter<? super LuceneSearchHit>> filters = new ArrayList<>();
            TableFilter<LuceneSearchHit> filter = new HitsViewPermissionFilter<LuceneSearchHit>(
                coralSession.getUserSubject(), coralSession);
            filters.add(filter);

            TableTool<LuceneSearchHit> hitsTable = new TableTool<LuceneSearchHit>(state, filters,
                hitsTableModel);
            templatingContext.put("hits_table", hitsTable);
        }
        catch(TableException e)
        {
            cmsData.getComponent().error("Error preparing table tool", e);
        }
        catch(SearchingException e)
        {
            cmsData.getComponent().error("Error while searching", e);
        }
        catch(Exception e)
        {
            cmsData.getComponent().error("Cannot execute category query", e);
        }
    }
}
