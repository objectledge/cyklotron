package net.cyklotron.cms.modules.components.category;

import java.util.Map;
import java.util.Set;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.category.components.BaseResourceListConfiguration;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.labeo.services.cache.CacheService;
import net.labeo.services.resource.Resource;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Base component for displaying lists of resources assigned to queried categories.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseResourceList.java,v 1.1 2005-01-24 04:35:10 pablo Exp $
 */
public abstract class BaseResourceList
extends BaseCategoryComponent
{
    /** Table service used to display resource lists. */
    protected TableService tableService;

	/** category query service */
	protected CategoryQueryService categoryQueryService;

    protected CacheService cacheService;

    public BaseResourceList()
    {
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
		categoryQueryService = (CategoryQueryService)broker.getService(CategoryQueryService.SERVICE_NAME);
        cacheService = (CacheService) broker.getService(CacheService.SERVICE_NAME);
    }

	public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
		throws ProcessingException
	{
		CmsData cmsData = cmsDataFactory.getCmsData(context);

		net.cyklotron.cms.category.components.BaseResourceList resList = getResourceList();

		BaseResourceListConfiguration config = resList.createConfig(data);

		// setup config		
		Parameters componentConfig = cmsData.getComponent().getConfiguration();
		config.shortInit(componentConfig);
		
		// get resources based on category query
		Resource[] resources = getResources(resList, data, config);

		// setup table tool
		TableState state = tableService.getGlobalState(data, resList.getTableStateName(data));
		TableTool tool = resList.getTableTool(data, config, state, resources);
		templatingContext.put("table", tool);

		// setup header
		templatingContext.put("header", config.getHeader());
	}

    /**
     * TODO: Make this kind of cacheing available as a static/tool/service code 
     */
    protected Resource[] getResources(
        net.cyklotron.cms.category.components.BaseResourceList resList,
        RunData data, BaseResourceListConfiguration config) throws ProcessingException
    {
        long cacheInterval = (long) config.getCacheInterval();
        if(cacheInterval > 0L)
        {
            // get cache instance
            Map cache = cacheService.getInstance("resourcelist", "resourcelist");
            // create cached resource list key
            CmsData cmsData = cmsDataFactory.getCmsData(context); 
            String key = cmsData.getNode().getIdString() + "." + cmsData.getComponent().getInstanceName();
            // get cached resource list together with creation time
            CacheEntry entry = (CacheEntry) cache.get(key);
            // check entry validity
            if(entry == null ||
            System.currentTimeMillis() - entry.timeStamp > cacheInterval*1000L)
            {
                Resource[] ress = getResources2(resList, data, config);
                entry = new CacheEntry(ress, System.currentTimeMillis());
                synchronized (cache)
                {
                    cache.put(key, entry);
                }
            }
            return entry.list;
        }
        return getResources2(resList, data, config);
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

    protected Resource[] getResources2(
        net.cyklotron.cms.category.components.BaseResourceList resList,
        RunData data, BaseResourceListConfiguration config) throws ProcessingException
    {
        // get resources based on category query
        String query = resList.getQuery(data, config);
        Set idSet = resList.getIdSet(data, config);
        try
        {
            if(idSet != null)
            {
                return categoryQueryService.forwardQuery(query, idSet);
            }
            else
            {
                return categoryQueryService.forwardQuery(query);
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
