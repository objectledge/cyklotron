package net.cyklotron.cms.modules.views.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableException;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.vote.CommunityVote;
import net.cyklotron.cms.structure.vote.SortOrder;
import net.cyklotron.cms.util.CmsResourceListTableModel;
import net.cyklotron.cms.util.ProtectedValidityViewFilter;

public class CommunityVoteStatistics
    extends BaseCMSScreen
{
    private final IntegrationService integrationService;

    private final CommunityVote communityVote;

    private final CategoryQueryService categoryQueryService;

    public CommunityVoteStatistics(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, IntegrationService integrationService,
        CommunityVote communityVote, CategoryQueryService categoryQueryService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.integrationService = integrationService;
        this.communityVote = communityVote;
        this.categoryQueryService = categoryQueryService;
    }

    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            CmsData cmsData = cmsDataFactory.getCmsData(context);
            List<NavigationNodeResource> list = new ArrayList<NavigationNodeResource>();
            CmsResourceListTableModel<NavigationNodeResource> model = new CmsResourceListTableModel<NavigationNodeResource>(
                context, integrationService, list, i18nContext.getLocale());

            int cutoffDateOffset = parameters.getInt("cutoffDateOffset", 30);
            templatingContext.put("currentCutoffDateOffset", Integer.toString(cutoffDateOffset));
            String primarySort = parameters.get("primarySort", "POSITIVE");
            templatingContext.put("currentPrimarySort", primarySort);
            String secondarySort = parameters.get("secodarySort",
                "priority.validity.start/ASC");
            templatingContext.put("currentSecondarySort", secondarySort);
            String secondarySortElements[] = secondarySort.split("/");
            String secondarySortOrder = secondarySortElements[0];
            String secondarySortDirection = secondarySortElements[1];
            boolean singleSiteOnly = parameters.getBoolean("singleSite", false);
            templatingContext.put("singleSite", singleSiteOnly);
            long categoryQueryId = parameters.getLong("categoryQuery", -1L);
            templatingContext.put("categoryQuery", categoryQueryId);

            Date cutoffDate = null;
            if(cutoffDateOffset > 0)
            {
                Calendar cal = new GregorianCalendar();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.DAY_OF_MONTH, -cutoffDateOffset);
                cutoffDate = cal.getTime();
            }
            SortOrder primarySortOrder = SortOrder.valueOf(primarySort);
            Comparator<NavigationNodeResource> secondarySortOrderComparator = model.getColumn(
                secondarySortOrder).getComparator();
            if("DESC".equals(secondarySortDirection))
            {
                secondarySortOrderComparator = Collections
                    .reverseOrder(secondarySortOrderComparator);
            }

            SiteResource singleSite = singleSiteOnly ? cmsData.getSite() : null;
            CategoryQueryResource categoryQuery = null;
            if(categoryQueryId != -1L)
            {
                try            
                {
                    categoryQuery = (CategoryQueryResource)coralSession.getStore().getResource(categoryQueryId);
                }
                catch(EntityDoesNotExistException e)
                {
                    throw new ProcessingException("invalid category query id", e);
                }
            }
            
            Map<SortOrder, List<NavigationNodeResource>> results = communityVote.getResults(
                cutoffDate, EnumSet.of(primarySortOrder), secondarySortOrderComparator,
                coralSession, singleSite, categoryQuery);

            TableModel<NavigationNodeResource> tableModel = new CmsResourceListTableModel<NavigationNodeResource>(
                context, integrationService, results.get(primarySortOrder), i18nContext.getLocale());
            TableFilter<NavigationNodeResource> filter = new ProtectedValidityViewFilter<NavigationNodeResource>(
                coralSession, cmsData, coralSession.getUserSubject());
            TableState tableState = tableStateManager.getState(context,
                "cms.views.documents.CommunityVoteResults");
            if(tableState.isNew())
            {
                tableState.setTreeView(false);
                tableState.setPageSize(50);
            }
            List<TableFilter<? super NavigationNodeResource>> filters = new ArrayList<>();
            filters.add(filter);
            TableTool<NavigationNodeResource> tableTool = new TableTool<NavigationNodeResource>(
                tableState, filters, tableModel);
            templatingContext.put("table", tableTool);
            
            List<String> availablePrimarySortOrders = new ArrayList<String>();
            for(SortOrder sortOrder : EnumSet.allOf(SortOrder.class))
            {
                availablePrimarySortOrders.add(sortOrder.name());
            }
            templatingContext.put("availablePrimarySortOrders", availablePrimarySortOrders);
            try
            {
                model = new CmsResourceListTableModel<NavigationNodeResource>(
                    context, integrationService, Collections.EMPTY_LIST, i18nContext.getLocale());
            }
            catch(TableException e)
            {
                throw new ProcessingException(e);
            }
            List<String> availableSecondarySortOrders = new ArrayList<String>();
            for(TableColumn<NavigationNodeResource> column : model.getColumns())
            {
                availableSecondarySortOrders.add(column.getName());
            }
            templatingContext.put("availableSecondarySortOrders", availableSecondarySortOrders);
            
            Resource queryRoot = categoryQueryService.getCategoryQueryRoot(coralSession,
                cmsData.getSite());
            List<Resource> availableCategoryQueries = Arrays.asList(queryRoot.getChildren());
            templatingContext.put("availableCategoryQueries", availableCategoryQueries);    
            templatingContext.put("noCategoryQuery", -1L);
        }
        catch(Exception e)
        {
            throw new ProcessingException(e);
        }
    }

    @Override
    public boolean checkAccessRights(Context context)
        throws Exception
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        return cmsData.checkAdministrator(coralSession);
    }
}
