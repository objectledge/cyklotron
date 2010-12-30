package net.cyklotron.cms.modules.views.library;

import java.util.Arrays;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.CoralTableModel;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryResourceImpl;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.search.PoolResource;
import net.cyklotron.cms.search.PoolResourceImpl;
import net.cyklotron.cms.search.SearchService;

/**
 * Configuration view for library index screen
 * 
 * @author rafal
 */
public class IndexConf
    extends BaseCMSScreen
{
    private final SearchService searchService;

    public IndexConf(Context context, Logger logger, PreferencesService preferencesService,
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
        CmsData cmsData = getCmsData();
        Parameters screenConfig = getScreenConfig();
        try
        {
            // current settings
            if(screenConfig.isDefined("category"))
            {
                CategoryResource category = CategoryResourceImpl.getCategoryResource(coralSession,
                    screenConfig.getLong("category"));
                templatingContext.put("category", category);
            }
            if(screenConfig.isDefined("search_pool"))
            {
                PoolResource searchPool = PoolResourceImpl.getPoolResource(coralSession,
                    screenConfig.getLong("search_pool"));
                templatingContext.put("search_pool", searchPool);
            }
            // available search pools
            templatingContext.put("search_pools", Arrays.asList(searchService.getPoolsRoot(
                coralSession, cmsData.getSite()).getChildren()));
        }
        catch(Exception e)
        {
            throw new ProcessingException("invalid configuration for library.Index screen in node "
                + cmsData.getNode(), e);
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
