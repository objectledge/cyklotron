package net.cyklotron.cms.modules.views.catalogue;

import java.util.Arrays;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.catalogue.CatalogueConfigResource;
import net.cyklotron.cms.catalogue.CatalogueConfigResourceImpl;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;

public class Config
    extends BaseCMSScreen
{
    private final SearchService searchService;

    public Config(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        SearchService searchService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.searchService = searchService;
    }

    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        // current settings
        CmsData cmsData = getCmsData();
        String cid = parameters.get("cid", "new");
        if(!cid.equals("new") && !templatingContext.containsKey("result"))
        {
            try
            {
                CatalogueConfigResource config = CatalogueConfigResourceImpl
                    .getCatalogueConfigResource(coralSession, Long.parseLong(cid));
                templatingContext.put("name", config.getName());

                if(config.isCategoryDefined())
                {
                    templatingContext.put("category", config.getCategory());
                }
                if(config.isSearchPoolDefined())
                {
                    templatingContext.put("search_pool", config.getSearchPool());
                }
            }
            catch(Exception e)
            {
                throw new ProcessingException("invalid parameter cid=" + parameters.get("cid"));
            }
        }

        // available search pools
        try
        {
            templatingContext.put("search_pools", Arrays.asList(searchService.getPoolsRoot(
                coralSession, cmsData.getSite()).getChildren()));
        }
        catch(SearchException e)
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
            logger.debug("Application 'catalogue' not enabled in site");
            return false;
        }
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkAdministrator(coralSession);
    }
}
