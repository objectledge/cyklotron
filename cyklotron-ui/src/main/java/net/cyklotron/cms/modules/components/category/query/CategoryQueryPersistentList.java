package net.cyklotron.cms.modules.components.category.query;

import java.util.List;
import java.util.Set;

import javax.servlet.http.Cookie;

import org.jcontainer.dna.Logger;
import org.objectledge.cache.CacheFactory;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.components.BaseResourceList;
import net.cyklotron.cms.category.components.BaseResourceListConfiguration;
import net.cyklotron.cms.category.query.CategoryQueryException;
import net.cyklotron.cms.category.query.CategoryQueryPersistentListConfiguration;
import net.cyklotron.cms.category.query.CategoryQueryPersistentResourceList;
import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryResourceImpl;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.ComponentDataCacheService;
import net.cyklotron.cms.structure.StructureService;

/**
 * Category Query Persistent List component.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryList.java,v 1.2 2005-01-25 11:24:24 pablo Exp $
 */
public class CategoryQueryPersistentList
    extends SkinableCMSComponent
{
    private CategoryQueryService categoryQueryService;

    private TableStateManager tableStateManager;

    private SiteService siteService;

    private IntegrationService integrationService;

    private CacheFactory cacheFactory;

    private final StructureService structureService;

    private final ComponentDataCacheService componentDataCacheService;

    public CategoryQueryPersistentList(org.objectledge.context.Context context, Logger logger,
        Templating templating, CmsDataFactory cmsDataFactory, SkinService skinService,
        MVCFinder mvcFinder, CategoryQueryService categoryQueryService,
        TableStateManager tableStateManager, SiteService siteService,
        IntegrationService integrationService, CacheFactory cacheFactory,
        StructureService structureService, ComponentDataCacheService componentDataCacheService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder);
        this.categoryQueryService = categoryQueryService;
        this.tableStateManager = tableStateManager;
        this.siteService = siteService;
        this.integrationService = integrationService;
        this.cacheFactory = cacheFactory;
        this.structureService = structureService;
        this.componentDataCacheService = componentDataCacheService;
    }

    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        CategoryQueryPersistentListConfiguration config = new CategoryQueryPersistentListConfiguration(
            getConfiguration());

        try
        {
            SiteResource site = cmsDataFactory.getCmsData(context).getSite();

            templatingContext.put("header", config.getHeader());

            String queryPoolName = config.getQueryPoolName();
            if(queryPoolName == null)
            {
                componentError(context, "category query pool not configured");
                return;
            }
            Resource[] res = coralSession.getStore().getResource(
                categoryQueryService.getCategoryQueryPoolRoot(coralSession, getSite(context)),
                queryPoolName);
            if(res.length == 0)
            {
                componentError(context, "configured category query pool not found");
                return;
            }
            if(res.length > 1)
            {
                componentError(context, "too many category query pool not found");
                return;
            }

            // get query list and setup table tool
            CategoryQueryPoolResource queryPool = (CategoryQueryPoolResource)res[0];
            List<CategoryQueryResource> queries = queryPool.getQueries();
            if(queries.size() == 0)
            {
                componentError(context, "configured category query pool has no queries");
                return;
            }

            CategoryQueryResource selected_query = (CategoryQueryResource)queries.get(0);
            Long queryId = getSelectedQuery(httpContext, parameters, queryPool);
            if(queryId != null)
            {
                selected_query = CategoryQueryResourceImpl.getCategoryQueryResource(coralSession,
                    queryId);
                if(selected_query != null && queryPool.getQueries().contains(selected_query))
                {
                    setCookie(httpContext, parameters, config);
                    templatingContext.put("selected_query_id", queryId);
                }
            }
            
            templatingContext.put("qpid", queryPool.getIdString());
            templatingContext.put("queries", queries);

            CategoryQueryPersistentResourceList resList = new CategoryQueryPersistentResourceList(
                context, integrationService, cmsDataFactory, categoryQueryService, siteService, structureService,
                selected_query, config);
            Resource[] resources = null;
            try
            {
                resources = getResources(coralSession, resList, config);
            }
            catch(Exception e)
            {
                componentError(context, "Cannot execute category query", e);
                return;
            }

            TableState state = tableStateManager.getState(context, resList.getTableStateName());
            TableTool table = resList.getTableTool(coralSession, context, config, state, resources);
            templatingContext.put("table", table);

        }
        catch(CategoryQueryException e)
        {
            componentError(context, "category query pool root not found");
            return;
        }
        catch(EntityDoesNotExistException e)
        {
            componentError(context, "category query does not exists");
            e.printStackTrace();
        }
    }

    public Long getSelectedQuery(HttpContext httpContext, Parameters parameters,
        CategoryQueryPoolResource queryPool)
        throws ProcessingException
    {
        try
        {
            if(parameters.isDefined("query_id"))
            {
                return parameters.getLong("query_id", 0);
            }
            if(queryPool == null)
            {
                return null;
            }
            String cookieKey = "queryPool_" + queryPool.getId();
            Cookie[] cookies = httpContext.getRequest().getCookies();
            if(cookies != null)
            {
                for(int i = 0; i < cookies.length; i++)
                {
                    if(cookies[i].getName().equals(cookieKey))
                    {
                        return Long.parseLong(cookies[i].getValue());
                    }
                }
            }
            return null;
        }
        catch(Exception e)
        {
            throw new ProcessingException("exception occured", e);
        }
    }
    
    private void setCookie(HttpContext httpContext, Parameters parameters,
        CategoryQueryPersistentListConfiguration config)
    {
        Long queryId = parameters.getLong("query_id", 0);

        if(queryId > 0)
        {
            Long queryPoolId = parameters.getLong("qpid", -1L);
            String cookieKey = "queryPool_" + queryPoolId;
            Cookie cookie = new Cookie(cookieKey, queryId.toString());
            cookie.setMaxAge(12 * 30 * 24 * 3600);

            if(!config.getDomain().isEmpty())
            {
                cookie.setDomain(config.getDomain());
            }
            
            if(config.isPathIncluded())
            {
                StringBuilder path = new StringBuilder();
                path.append(httpContext.getRequest().getContextPath());
                if(!httpContext.getRequest().getServletPath().startsWith("/"))
                {
                    path.append('/');
                }
                String servletPath = httpContext.getRequest().getServletPath();
                if(servletPath.endsWith("/"))
                {
                    servletPath = servletPath.substring(0, servletPath.length() - 1);
                }
                path.append(servletPath);
                cookie.setPath(path.toString());
            }else{
                cookie.setPath("/");
            }
            
            httpContext.getResponse().addCookie(cookie);
        }
    }

    private Resource[] getResources(CoralSession coralSession,
        CategoryQueryPersistentResourceList resList, CategoryQueryPersistentListConfiguration config)
        throws Exception
    {        
        CmsData cmsData = cmsDataFactory.getCmsData(context); 
        int cacheInterval = config.getCacheInterval();
        if(cacheInterval > 0L)
        {
            Object guard = componentDataCacheService.getGuard(cmsData);            
            synchronized(guard)
            {
                Resource[] results = componentDataCacheService.getCachedData(cmsData);
                if(results == null)
                {
                    results = getResources2(coralSession, resList, config);
                    componentDataCacheService.setCachedData(cmsData, results, cacheInterval);
                }
                return results;
            }
        }
        else
        {
            logger.warn("non-cachable category query results screen nodeId="
                + cmsData.getNode().getIdString());
            return getResources2(coralSession, resList, config);
        }
    }

    private Resource[] getResources2(CoralSession coralSession, BaseResourceList resList,
        BaseResourceListConfiguration config)
        throws Exception
    {
        String query = resList.getQuery(coralSession, config);
        Set idSet = resList.getIdSet(coralSession, config);
        if(idSet != null)
        {
            return categoryQueryService.forwardQuery(coralSession, query, idSet);
        }
        else
        {
            return categoryQueryService.forwardQuery(coralSession, query);
        }
    }
}
