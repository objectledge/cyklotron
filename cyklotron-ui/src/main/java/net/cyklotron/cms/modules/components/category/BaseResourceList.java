package net.cyklotron.cms.modules.components.category;

import java.util.Map;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.cache.CacheFactory;
import org.objectledge.context.Context;
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
import net.cyklotron.cms.CmsNodeResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.components.BaseResourceListConfiguration;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.SkinService;

/**
 * Base component for displaying lists of resources assigned to queried categories.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseResourceList.java,v 1.3 2005-01-27 04:59:00 pablo Exp $
 */
public abstract class BaseResourceList
extends BaseCategoryComponent
{
    /** Table service used to display resource lists. */
    protected TableStateManager tableStateManager;

    /** category query service */
    protected CategoryQueryService categoryQueryService;

    protected CacheFactory cacheFactory;

    protected IntegrationService integrationService;
    
    protected PreferencesService preferencesService;
    
    public BaseResourceList(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder,
        CategoryService categoryService, SiteService siteService, 
        TableStateManager tableStateManager, CategoryQueryService categoryQueryService,
        CacheFactory cacheFactory, IntegrationService integrationService,
        PreferencesService preferencesService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder, categoryService,
                        siteService);
        this.tableStateManager =tableStateManager; 
		this.categoryQueryService = categoryQueryService;
        this.cacheFactory = cacheFactory;
        this.integrationService = integrationService;
        this.preferencesService = preferencesService;
    }

	public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
		throws ProcessingException
	{
		CmsData cmsData = cmsDataFactory.getCmsData(context);

		net.cyklotron.cms.category.components.BaseResourceList resList = getResourceList();

		BaseResourceListConfiguration config = resList.createConfig();

		// setup config		
		Parameters componentConfig = cmsData.getComponent().getConfiguration();
		config.shortInit(componentConfig);
		
        CmsNodeResource origin = preferencesService.getNodePreferenceOrigin(cmsData.getNode(), 
              cmsData.getComponent().getConfigurationPrefix()+"cacheInterval");
              config.setConfigOriginNode(origin);
		// get resources based on category query
		Resource[] resources = getResources(coralSession, resList, config);

		// setup table tool
		TableState state = tableStateManager.getState(context, resList.getTableStateName());
		TableTool tool = resList.getTableTool(coralSession, context, config, state, resources);
		templatingContext.put("table", tool);

		// setup header
		templatingContext.put("header", config.getHeader());
	}

    /**
     * TODO: Make this kind of cacheing available as a static/tool/service code 
     */
    protected Resource[] getResources(CoralSession coralSession,
        net.cyklotron.cms.category.components.BaseResourceList resList,
        BaseResourceListConfiguration config) throws ProcessingException
    {
        long cacheInterval = (long) config.getCacheInterval();
        if(cacheInterval > 0L)
        {
            // get cache instance
            Map cache = null;
            try
            {
                cacheFactory.getInstance("resourcelist", "resourcelist");
            }
            catch(Exception e)
            {
                throw new ProcessingException(e);
            }
            // create cached resource list key
            
            
            
            CmsData cmsData = cmsDataFactory.getCmsData(context); 
            //String key = cmsData.getNode().getIdString() + "." + cmsData.getComponent().getInstanceName();
            CmsNodeResource node = getCacheKeyNode(config, cmsData);
            String key = node.getIdString() + "." + cmsData.getComponent().getInstanceName();
            
            // get cached resource list together with creation time
            CacheEntry entry = (CacheEntry) cache.get(key);
            // check entry validity
            if(entry == null ||
            System.currentTimeMillis() - entry.timeStamp > cacheInterval*1000L)
            {
                Resource[] ress = getResources2(coralSession, resList, config);
                entry = new CacheEntry(ress, System.currentTimeMillis());
                synchronized (cache)
                {
                    cache.put(key, entry);
                }
            }
            return entry.list;
        }
        else
        {
            CmsData cmsData = cmsDataFactory.getCmsData(context);
            log.warn("non-cachable resource list nodeId="+cmsData.getNode().getIdString()+
                " instance="+cmsData.getComponent().getInstanceName());
            return getResources2(coralSession, resList, config);
        }
    }

    /**
     * @param config
     * @param cmsData
     * @return
     */
    protected CmsNodeResource getCacheKeyNode(BaseResourceListConfiguration config, CmsData cmsData)
    {
        CmsNodeResource node = config.getConfigOriginNode();
        if(node == null)
        {
            node = cmsData.getNode(); 
        }
        return node;
    }
    
    private class CacheEntry
    {
        Resource[] list;
        long timeStamp;
        /**
         * @param ress
         * @param l
         */
        public CacheEntry(Resource[] list, long timeStamp)
        {
            this.list = list;
            this.timeStamp = timeStamp;
        }
    }

    protected Resource[] getResources2(CoralSession coralSession,
        net.cyklotron.cms.category.components.BaseResourceList resList,
        BaseResourceListConfiguration config) throws ProcessingException
    {
        // get resources based on category query
        String query = resList.getQuery(coralSession, config);
        Set idSet = resList.getIdSet(coralSession, config);
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
            cmsDataFactory.getCmsData(context).getComponent().error("Cannot execute category query", e);
            return null;
        }
    }
    
    protected abstract net.cyklotron.cms.category.components.BaseResourceList getResourceList();
}
