package net.cyklotron.cms.modules.views.statistics;

import java.util.ArrayList;
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
import org.objectledge.coral.session.CoralSession;
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
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
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

    public CommunityVoteStatistics(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, IntegrationService integrationService,
        CommunityVote communityVote)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.integrationService = integrationService;
        this.communityVote = communityVote;
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

            Calendar cal = new GregorianCalendar();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.DAY_OF_MONTH, -cutoffDateOffset);
            Date cutoffDate = cal.getTime();
            SortOrder primarySortOrder = SortOrder.valueOf(primarySort);
            Comparator<NavigationNodeResource> secondarySortOrderComparator = model.getColumn(
                secondarySortOrder).getComparator();
            if("DESC".equals(secondarySortDirection))
            {
                secondarySortOrderComparator = Collections
                    .reverseOrder(secondarySortOrderComparator);
            }

            Map<SortOrder, List<NavigationNodeResource>> results = communityVote.getResults(
                cutoffDate, EnumSet.of(primarySortOrder), secondarySortOrderComparator,
                coralSession);

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
            TableTool<NavigationNodeResource> tableTool = new TableTool<NavigationNodeResource>(
                tableState, Collections.singletonList(filter), tableModel);
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
