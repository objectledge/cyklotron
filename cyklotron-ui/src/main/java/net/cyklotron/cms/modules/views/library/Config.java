package net.cyklotron.cms.modules.views.library;

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
import net.cyklotron.cms.library.LibraryConfigResource;
import net.cyklotron.cms.library.LibraryService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;

public class Config
    extends BaseCMSScreen
{
    private final LibraryService libraryService;

    private final SearchService searchService;

    public Config(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        LibraryService libraryService, SearchService searchService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.libraryService = libraryService;
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
        LibraryConfigResource config = libraryService.getConfig(cmsData.getSite(), coralSession);
        if(config.isCategoryDefined())
        {
            templatingContext.put("category", config.getCategory());
        }
        if(config.isSearchPoolDefined())
        {
            templatingContext.put("search_pool", config.getSearchPool());
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
