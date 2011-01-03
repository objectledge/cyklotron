package net.cyklotron.cms.modules.views.library;

import java.util.List;
import java.util.Locale;

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
import net.cyklotron.cms.library.LibraryConfigResource;
import net.cyklotron.cms.library.LibraryService;
import net.cyklotron.cms.library.ProblemReportItem;
import net.cyklotron.cms.library.ProblemReportTableModel;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;

public class Problems
    extends BaseCMSScreen
{
    private final LibraryService libraryService;

    public Problems(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager, LibraryService libraryService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.libraryService = libraryService;
    }

    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        SiteResource site = getCmsData().getSite();
        LibraryConfigResource config = libraryService.getConfig(site, coralSession);
        if(config.isCategoryDefined() && config.isSearchPoolDefined())
        {
            templatingContext.put("application_configured", "true");
            Locale locale = i18nContext.getLocale();
            try
            {
                List<ProblemReportItem> report = libraryService.getProblemReport(site, coralSession, locale);
                ProblemReportTableModel tableModel = new ProblemReportTableModel(report, locale);
                TableState tableState = tableStateManager.getState(context, "view:library.Browse");
                if(tableState.isNew())
                {
                    tableState.setTreeView(false);
                    tableState.setSortColumnName("resource");
                    tableState.setPageSize(20);
                }
                TableTool<ProblemReportItem> tableTool = new TableTool<ProblemReportItem>(tableState, null,
                    tableModel);
                templatingContext.put("table", tableTool);
            }
            catch(Exception e)
            {
                throw new ProcessingException("internal error", e);
            }
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
