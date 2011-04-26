package net.cyklotron.cms.modules.views.catalogue;

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
import net.cyklotron.cms.catalogue.CatalogueConfigResourceImpl;
import net.cyklotron.cms.catalogue.IndexCard;
import net.cyklotron.cms.catalogue.IndexCardTableModel;
import net.cyklotron.cms.catalogue.CatalogueService;
import net.cyklotron.cms.catalogue.CatalogueConfigResource;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;

public class Browse
    extends BaseCMSScreen
{
    private final CatalogueService catalogueService;

    public Browse(Context context, Logger logger, PreferencesService preferencesService,
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
                List<IndexCard> index;
                String query;
                if(parameters.isDefined("query") && (query = parameters.get("query")).length() > 0)
                {
                    templatingContext.put("query", query);
                    index = catalogueService.search(query, config, coralSession, locale);
                }
                else
                {
                    index = catalogueService.getAllItems(config, coralSession, locale);
                }
                IndexCardTableModel tableModel = new IndexCardTableModel(index, locale);
                TableState tableState = tableStateManager
                    .getState(context, "view:catalogue.Browse");
                if(tableState.isNew())
                {
                    tableState.setTreeView(false);
                    tableState.setSortColumnName("title");
                    tableState.setPageSize(20);
                }
                TableTool<IndexCard> tableTool = new TableTool<IndexCard>(tableState, null,
                    tableModel);
                templatingContext.put("table", tableTool);
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
        if(!cmsData.isApplicationEnabled("catalogue"))
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
