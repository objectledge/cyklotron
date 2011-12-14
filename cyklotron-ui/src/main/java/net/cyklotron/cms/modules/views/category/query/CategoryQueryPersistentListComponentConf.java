package net.cyklotron.cms.modules.views.category.query;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.CoralTableModel;
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
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.components.BaseResourceListConfiguration;
import net.cyklotron.cms.category.query.CategoryQueryException;
import net.cyklotron.cms.category.query.CategoryQueryPersistentListConfiguration;
import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.category.BaseResourceListComponentConf;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * Category Query Persistent List component configuration screen.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryPersistentListComponentConf.java,v 1.5 2005-06-15 12:51:05 zwierzem
 *          Exp $
 */
public class CategoryQueryPersistentListComponentConf
    extends BaseResourceListComponentConf
{
    public CategoryQueryPersistentListComponentConf(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, CategoryService categoryService,
        SiteService siteService, IntegrationService integrationService,
        CategoryQueryService categoryQueryService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        categoryService, siteService, integrationService, categoryQueryService);
    }

    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        // prepares the config
        super.process(parameters, mvcContext, templatingContext, httpContext, i18nContext, coralSession);
   
        CategoryQueryPersistentListConfiguration config = (CategoryQueryPersistentListConfiguration)getConfig();

        CmsData cmsData = getCmsData();
        SiteResource site = cmsData.getSite();
        if(site == null)
        {
            site = cmsData.getGlobalComponentsDataSite();
        }
        if(site == null)
        {
            throw new ProcessingException("No site selected");
        }

        // get currently selected pool
        try
        {
            String queryPoolName = config.getQueryPoolName();
            if(queryPoolName != null)
            {
                Resource[] res = coralSession.getStore().getResource(
                    categoryQueryService.getCategoryQueryPoolRoot(coralSession, site),
                    queryPoolName);
                if(res.length == 1)
                {
                    CategoryQueryPoolResource queryPool = (CategoryQueryPoolResource)res[0];
                    templatingContext.put("selected_pool", queryPool);
                }
                else if(res.length > 1)
                {
                    throw new ProcessingException(
                        "too many category query pools with the same name");
                }
            }
        }
        catch(CategoryQueryException e)
        {
            templatingContext.put("result", "exception");
            logger.error("CategoryQueryException: ", e);
            return;
        }

        // setup table tool with pools
        try
        {
            Resource queryRoot = categoryQueryService.getCategoryQueryPoolRoot(coralSession, site);
            TableState state = tableStateManager
                .getState(context, "cms:category,query,CategoryQueryPersistentListComponentConf:"
                    + site.getIdString());
            if(state.isNew())
            {
                state.setRootId(queryRoot.getIdString());
                state.setTreeView(false);
                state.setShowRoot(false);
            }
            TableTool table = new TableTool(state, null, new CoralTableModel(coralSession,
                i18nContext.getLocale()));
            templatingContext.put("table", table);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve information", e);
        }
    }

    @Override
    protected BaseResourceListConfiguration getConfig()
        throws ProcessingException
    {
        return CategoryQueryPersistentListConfiguration.getConfig(context);
    }
}
