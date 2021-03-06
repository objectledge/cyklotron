package net.cyklotron.cms.modules.views.catalogue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.catalogue.CatalogueConfigResourceImpl;
import net.cyklotron.cms.catalogue.CatalogueService;
import net.cyklotron.cms.catalogue.IndexCard;
import net.cyklotron.cms.catalogue.Problem;
import net.cyklotron.cms.catalogue.ProblemReportItem;
import net.cyklotron.cms.catalogue.ProblemReportTableModel;
import net.cyklotron.cms.catalogue.CatalogueConfigResource;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;

public class Problems
    extends BaseCMSScreen
{
    private final CatalogueService catalogueService;

    public Problems(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        CatalogueService catalogueService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.catalogueService = catalogueService;
    }

    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getCmsData().getSite();
            long cid = parameters.getLong("cid");
            CatalogueConfigResource config = CatalogueConfigResourceImpl
                .getCatalogueConfigResource(coralSession, cid);
            templatingContext.put("config", config);
            
            if(config.isCategoryDefined() && config.isSearchPoolDefined())
            {
                templatingContext.put("applicationConfigured", "true");
                Locale locale = i18nContext.getLocale();
                Set<Problem> filter;
                if(parameters.isDefined("filterDefined"))
                {
                    filter = new HashSet<Problem>();
                    String[] filterItems = parameters.getStrings("filter");
                    for(String filterItem : filterItems)
                    {
                        filter.add(Problem.valueOf(filterItem));
                    }
                    templatingContext.put("filter", filter);
                }
                else
                {
                    filter = EnumSet.allOf(Problem.class);
                }

                List<ProblemReportItem> report = catalogueService.getProblemReport(config,
                    coralSession, locale);

                Set<Problem> problemTypes = new HashSet<Problem>();
                for(ProblemReportItem item : report)
                {
                    problemTypes.addAll(item.getProblems());
                }
                List<Problem> problemTypeList = new ArrayList<Problem>(problemTypes);
                Collections.sort(problemTypeList);
                templatingContext.put("problemTypes", problemTypeList);

                List<ProblemReportItem> filteredReport = catalogueService.filterProblemReport(
                    report, filter);
                ProblemReportTableModel tableModel = new ProblemReportTableModel(filteredReport,
                    locale);
                TableState tableState = tableStateManager.getState(context, "view:library.Browse");
                if(tableState.isNew())
                {
                    tableState.setTreeView(false);
                    tableState.setSortColumnName("resource");
                    tableState.setPageSize(20);
                }
                TableTool<ProblemReportItem> tableTool = new TableTool<ProblemReportItem>(
                    tableState, null, tableModel);
                templatingContext.put("table", tableTool);
                templatingContext.put("propertyOrder", IndexCard.Property.ORDER);
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("internal error", e);
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("library"))
        {
            logger.debug("Application 'search' not enabled in site");
            return false;
        }
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
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
