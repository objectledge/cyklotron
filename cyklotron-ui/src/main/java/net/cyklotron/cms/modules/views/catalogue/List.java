package net.cyklotron.cms.modules.views.catalogue;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.CoralTableModel;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.catalogue.CatalogueConfigResource;
import net.cyklotron.cms.catalogue.CatalogueService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;

public class List
    extends BaseCMSScreen
{
    private final CatalogueService catalogueService;

    public List(Context context, Logger logger, PreferencesService preferencesService,
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
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        Resource configRoot = catalogueService.getConfigRoot(cmsData.getSite(), coralSession);
        TableModel<CatalogueConfigResource> model = new CoralTableModel<CatalogueConfigResource>(
            coralSession, i18nContext.getLocale());
        TableState state = tableStateManager.getState(context, "catalogue.list."
            + cmsData.getSite().getId());
        if(state.isNew())
        {
            state.setTreeView(false);
            state.setRootId(configRoot.getIdString());
            state.setSortColumnName("name");
        }
        try
        {
            TableTool<CatalogueConfigResource> table = new TableTool<CatalogueConfigResource>(
                state, null, model);
            templatingContext.put("table", table);
        }
        catch(TableException e)
        {
            throw new ProcessingException(e);
        }
    }
    
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("catalogue"))
        {
            logger.debug("Application 'catalogue' not enabled in site");
            return false;
        }
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkAdministrator(coralSession);
    }
}
