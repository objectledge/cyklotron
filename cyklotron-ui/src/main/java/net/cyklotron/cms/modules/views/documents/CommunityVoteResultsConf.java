package net.cyklotron.cms.modules.views.documents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.query.CategoryQueryException;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.vote.SortOrder;
import net.cyklotron.cms.util.CmsResourceListTableModel;

public class CommunityVoteResultsConf
    extends BaseCMSScreen
{
    private final IntegrationService integrationService;

    private final CategoryQueryService categoryQueryService;

    public CommunityVoteResultsConf(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, IntegrationService integrationService,
        CategoryQueryService categoryQueryService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.integrationService = integrationService;
        this.categoryQueryService = categoryQueryService;
    }

    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
        templatingContext.put("config", componentConfig);
        List<String> availablePrimarySortOrders = new ArrayList<String>();
        for(SortOrder sortOrder : EnumSet.allOf(SortOrder.class))
        {
            availablePrimarySortOrders.add(sortOrder.name());
        }
        templatingContext.put("availablePrimarySortOrders", availablePrimarySortOrders);
        List<String> currentPrimarySortOrders = new ArrayList<String>();
        String[] currentPrimarySortOrderNames = componentConfig.getStrings("primarySortOrders");
        if(currentPrimarySortOrderNames.length == 0)
        {
            currentPrimarySortOrderNames = "POSITIVE,NEGATIVE,TOTAL".split(",");
        }
        for(String sortOrderName : currentPrimarySortOrderNames)
        {
            currentPrimarySortOrders.add(sortOrderName);
        }
        templatingContext.put("currentPrimarySortOrders", currentPrimarySortOrders);

        CmsResourceListTableModel<NavigationNodeResource> model;
        try
        {
            model = new CmsResourceListTableModel<NavigationNodeResource>(context,
                integrationService, Collections.EMPTY_LIST, i18nContext.getLocale());
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

        try
        {
            Resource queryRoot = categoryQueryService.getCategoryQueryRoot(coralSession,
                getCmsData().getSite());
            List<Resource> availableCategoryQueries = Arrays.asList(queryRoot.getChildren());
            templatingContext.put("availableCategoryQueries", availableCategoryQueries);
        }
        catch(CategoryQueryException e)
        {
            throw new ProcessingException(e);
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        CmsData cmsData = getCmsData();
        if(cmsData.getNode() != null)
        {
            return cmsData.getNode().canModify(coralSession, coralSession.getUserSubject());
        }
        else
        {
            return checkAdministrator(coralSession);
        }
    }
}
