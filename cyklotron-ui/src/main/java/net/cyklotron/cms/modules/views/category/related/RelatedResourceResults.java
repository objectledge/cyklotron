package net.cyklotron.cms.modules.views.category.related;

import org.jcontainer.dna.Logger;
import org.objectledge.cache.CacheFactory;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.components.BaseResourceListConfiguration;
import net.cyklotron.cms.category.components.RelatedResourceListConfiguration;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.documents.DocumentNodeResourceImpl;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.BaseSkinableScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.ComponentDataCacheService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

import bak.pcj.set.LongSet;

/**
 * Category Query Resutls screen.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryResults.java,v 1.7 2008-10-30 17:54:28 rafal Exp $
 */
public class RelatedResourceResults
    extends BaseSkinableScreen
{
    /** category query service */
    protected CategoryService categoryService;

    protected CategoryQueryService categoryQueryService;

    protected SiteService siteService;

    protected IntegrationService integrationService;

    protected CacheFactory cacheFactory;

    private final ComponentDataCacheService componentDataCacheService;

    private final String DOC_ID_PARAM = "doc_id";

    public RelatedResourceResults(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        StructureService structureService, StyleService styleService, SkinService skinService,
        MVCFinder mvcFinder, TableStateManager tableStateManager, CategoryService categoryService,
        CategoryQueryService categoryQueryService, SiteService siteService,
        IntegrationService integrationService, ComponentDataCacheService componentDataCacheService)
    {
        super(context, logger, preferencesService, cmsDataFactory, structureService, styleService,
                        skinService, mvcFinder, tableStateManager);
        this.categoryService = categoryService;
        this.categoryQueryService = categoryQueryService;
        this.siteService = siteService;
        this.integrationService = integrationService;
        this.componentDataCacheService = componentDataCacheService;
    }

    public void prepareDefault(Context context)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        NavigationNodeResource node = null;
        Parameters screenConf = getScreenConfig();

        try
        {
            if(parameters.isDefined(DOC_ID_PARAM))
            {
                node = DocumentNodeResourceImpl.getNavigationNodeResource(coralSession,
                    parameters.getLong(DOC_ID_PARAM));
            }
            if(node == null)
            {
                node = cmsData.getContentNode();
            }

            net.cyklotron.cms.category.components.BaseResourceList resList = new net.cyklotron.cms.category.components.RelatedResourceList(
                context, integrationService, cmsDataFactory, categoryQueryService, categoryService,
                node);
            // setup config
            RelatedResourceListConfiguration config = RelatedResourceListConfiguration
                .getConfig(context);
            config.shortInit(screenConf);
            // get resources based on category query
            Resource[] resources = getResources(coralSession, resList, config);
            // setup table tool
            TableState state = tableStateManager
                .getState(context, getTableStateName(cmsData, node));
            TableTool tool = resList.getTableTool(coralSession, context, config, state, resources);
            templatingContext.put("table", tool);
            templatingContext.put("document", node);
            templatingContext.put("header", config.getHeader());
        }
        catch(Exception e)
        {
            screenError(cmsData.getNode(), context, e.getMessage());
        }
    }

    /** Returns a table state name unique for this resource list component. */
    public String getTableStateName(CmsData cmsData, NavigationNodeResource node)
    {
        return "net.cyklotron.cms.category.related.related_resource_results."
            + cmsData.getNode().getIdString() + "." + node.getIdString() + "."
            + cmsData.getComponent().getInstanceName();
    }

    protected Resource[] getResources(CoralSession coralSession,
        net.cyklotron.cms.category.components.BaseResourceList resList,
        BaseResourceListConfiguration config)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        int cacheInterval = config.getCacheInterval();
        if(cacheInterval > 0L)
        {
            Object guard = componentDataCacheService.getGuard(cmsData, null);
            synchronized(guard)
            {
                Resource[] results = componentDataCacheService.getCachedData(cmsData, null);
                if(results == null)
                {
                    results = getResources2(coralSession, resList, config);
                    componentDataCacheService.setCachedData(cmsData, null, results, cacheInterval);
                }
                return results;
            }
        }
        else
        {
            logger.warn("non-cachable resource list nodeId=" + cmsData.getNode().getIdString()
                + " instance=" + cmsData.getComponent().getInstanceName());
            return getResources2(coralSession, resList, config);
        }
    }

    protected Resource[] getResources2(CoralSession coralSession,
        net.cyklotron.cms.category.components.BaseResourceList resList,
        BaseResourceListConfiguration config)
        throws ProcessingException
    {
        // get resources based on category query
        String query = resList.getQuery(coralSession, config);
        LongSet idSet = resList.getIdSet(coralSession, config);
        try
        {
            if(idSet != null)
            {
                return categoryQueryService.forwardQuery(coralSession, query, idSet);
            }
            else
            {
                return categoryQueryService.forwardQuery(coralSession, query);
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("Cannot execute category query", e);
        }
    }

}
