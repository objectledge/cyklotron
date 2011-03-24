package net.cyklotron.cms.category.query.internal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.cache.CacheFactory;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.relation.CoralRelationQuery;
import org.objectledge.coral.relation.query.parser.TokenMgrError;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.threads.Task;
import org.objectledge.threads.ThreadPool;

import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.query.CategoryQueryException;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryRootResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.category.query.CategoryQueryUtil;
import net.cyklotron.cms.category.query.CategoryResolver;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * Implementation of Category Query Service.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryServiceImpl.java,v 1.10 2007-11-18 21:23:45 rafal Exp $
 */
public class CategoryQueryServiceImpl
	implements CategoryQueryService
{
    /** logging facility */
    private Logger log;

	/** category service */
	private CategoryService categoryService;

    /** coral session factory */
    private CoralSessionFactory sessionFactory;
    
    private final Map<String, String> sharedResultKeys = new HashMap<String,String>();
    
    private final Map<String,ResultsCacheEntry> resultsCache;
    
    private static final String CACHE_NAME = "categoryQueryResults";
            
    private static final long CACHE_EXPUNGE_INTERVAL = 60 * 1000l; // 1 minute

    /**
     * Initializes the service.
     */
    public CategoryQueryServiceImpl(Logger logger, CategoryService categoryService,
        CoralSessionFactory sessionFactory, CacheFactory cacheFactory, ThreadPool threadPool)
    {
        this.log = logger;
		this.categoryService = categoryService;
        this.sessionFactory = sessionFactory;
        Map<String,ResultsCacheEntry> cacheInstance = null;
        try
        {
            cacheInstance = cacheFactory.getInstance(CACHE_NAME, CACHE_NAME);
            threadPool.runDaemon(new ResultsCacheCleaner(cacheInstance, log));
        }
        catch(Exception e)
        {
            logger.warn("categoryQueryResults cache not configured, result caching is disabled");
        }
        resultsCache = cacheInstance;
    }

	// resource management ///////////////////////////////////////////////////////////////

    public Resource getCategoryQueryRoot(CoralSession coralSession, SiteResource site)
        throws CategoryQueryException
    {
		return getCreateResource(coralSession, site, getRoot(coralSession, site), "node", QUERY_ROOT);
    }

	public Resource getCategoryQueryPoolRoot(CoralSession coralSession, SiteResource site)
	throws CategoryQueryException
	{
		return getCreateResource(coralSession, site, getRoot(coralSession, site), "node", POOL_ROOT);
	}

	private CategoryQueryRootResource getRoot(CoralSession coralSession, SiteResource site)
	    throws CategoryQueryException
	{
		return (CategoryQueryRootResource)getCreateResource(coralSession, site, 
		    getApplications(coralSession, site), "category.query.root", ROOT);
	}

	private Resource getCreateResource(CoralSession coralSession, SiteResource site, Resource parent, String className, String name)
	    throws CategoryQueryException
	{
		Resource[] res = coralSession.getStore().getResource(parent, name);
		if (res.length == 1)
		{
			return res[0];
		}
		if (res.length == 0)
		{
			try
			{
                ResourceClass rc = coralSession.getSchema().getResourceClass(className);
                return coralSession.getStore().createResource(name, parent, rc, new HashMap());
			}
			catch (ValueRequiredException e)
			{
				throw new CategoryQueryException(
					"could not create '"+name+"' resource for site '"+site.getName()+"'", e);
			} 
            catch (EntityDoesNotExistException e)
            {
                throw new CategoryQueryException("invalid resource class "+className, e);
            }
            catch(InvalidResourceNameException e)
            {
                throw new CategoryQueryException("unexpected exception", e);
            }
		}
		throw new CategoryQueryException(
			"too many '"+name+"' resources for site '"+site.getName()+"'");
	}

	private Resource getApplications(CoralSession coralSession, SiteResource site)
	    throws CategoryQueryException
	{
		Resource[] res = coralSession.getStore().getResource(site, "applications");
		if (res.length == 1)
		{
			return res[0];
		}
		if(res.length == 0)
		{
			throw new CategoryQueryException(
				"cannot get applications resource for site '"+site.getName()+"'");
		}
		throw new CategoryQueryException(
			"too many 'applications' resources for site '"+site.getName()+"'");
	}

    public Map<CategoryResource, String> initCategorySelection(CoralSession coralSession, String items, String state)
    {
		return initCategorySelection(coralSession, CategoryQueryUtil.splitCategoryIdentifiers(items), state);
    }

	public Map<CategoryResource, String> initCategorySelection(CoralSession coralSession, String[] items, String state)
	{
        Map<CategoryResource, String> map = new HashMap<CategoryResource, String>();
		if(items == null || items.length == 0)
		{
			return map;
		}
		CategoryResolver resolver = getCategoryResolver();
		for (int i = 0; i < items.length; i++)
        {
			CategoryResource category = resolver.resolveCategoryIdentifier(items[i]);
        	if(category != null)
        	{
				map.put(category, state);
        	}
		}
		return map;
	}

    public NavigationNodeResource getResultsNode(CoralSession coralSession, SiteResource site)
        throws CategoryQueryException
    {
        return getRoot(coralSession, site).getResultsNode();
    }
    
    public CategoryQueryResource getDefaultQuery(CoralSession coralSession, SiteResource site)
        throws CategoryQueryException
    {
        return getRoot(coralSession, site).getDefaultQuery();
    }

    public void setResultsNode(CoralSession coralSession, SiteResource site, NavigationNodeResource node)
        throws CategoryQueryException
    {
        if(node != null)
        {
            if(!node.getSite().equals(site))
            {
                throw new CategoryQueryException("node "+node.getPath()+" is outside site "+
                    site.getName());
            }
        }
        CategoryQueryRootResource root = getRoot(coralSession, site);
        root.setResultsNode(node);
        root.update();
    }
        
    public void setDefaultQuery(CoralSession coralSession,SiteResource site, CategoryQueryResource query)
        throws CategoryQueryException
    {
        if(query != null)
        {
            Resource p = query;
            while(p != null && !p.equals(site))
            {
                if(p.equals(site))
                {
                    break;
                }
                p = p.getParent();
            }
            if(p == null)
            {
                throw new CategoryQueryException("query "+query.getPath()+" is outside site"+
                    site.getName());
            }
        }
        CategoryQueryRootResource root = getRoot(coralSession, site);
        root.setDefaultQuery(query);
        root.update();
    }

    // queries /////////////////////////////////////////////////////////////////////////

	public Resource[] forwardQuery(CoralSession coralSession, String query) throws Exception
	{
		if(query != null && query.length() > 0)
		{
		    CoralRelationQuery crq = coralSession.getRelationQuery();
            try
            {
                return crq.query(query, getCategoryResolver());
            }
            catch(TokenMgrError e)
            {
                throw new CategoryQueryException("Parser exception: ", e);
            }
		}
		return new Resource[0];
	}

    public Resource[] forwardQuery(CoralSession coralSession, String query, Set idSet) throws Exception
    {
        if(query != null && query.length() > 0)
        {
            CoralRelationQuery crq = coralSession.getRelationQuery();
            try
            {
                return crq.query(query, getCategoryResolver(), idSet);
            }
            catch(TokenMgrError e)
            {
                throw new CategoryQueryException("Parser exception: ", e);
            }
        }
        return new Resource[0];
    }

	private CategoryResolver resolver;

    public CategoryResolver getCategoryResolver()
    {
        if (resolver == null)
        {
            resolver = new CategoryResolver(this, categoryService, sessionFactory);
        }
        return resolver;
    }

    // results caching
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getSharedResultsKey(String key)
    {
        String sharedKey = null;
        synchronized(sharedResultKeys)
        {
            sharedKey = (String)sharedResultKeys.get(key);
            if(sharedKey == null)
            {
                sharedKey = key;
                sharedResultKeys.put(key, key);
            }
        }
        return sharedKey;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Resource[] getCachedResults(String key)
    {
        if(resultsCache == null)
        {
            // caching disabled
            return null;
        }
        else
        {
            synchronized(resultsCache)
            {
                ResultsCacheEntry entry = resultsCache.get(key);
                long currentTimeMillis = System.currentTimeMillis();
                if(entry != null && entry.isValid(currentTimeMillis))
                {
                    return entry.results;
                }
                else
                {
                    return null;
                }
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setCachedResult(String identifier, Resource[] results, long cacheInterval)
    {
        if(resultsCache != null)
        {
            synchronized(resultsCache)
            {
                ResultsCacheEntry entry = new ResultsCacheEntry(results, System.currentTimeMillis() + cacheInterval * 1000l);
                resultsCache.put(identifier, entry);
            }
        }
    }
        
    private static class ResultsCacheEntry
    {
        final private Resource[] results;
        final private long expiryTime;

        public ResultsCacheEntry(Resource[] list, long expiryTime)
        {
            this.results = list;
            this.expiryTime = expiryTime;
        }
        
        public boolean isValid(long now)
        {
            return expiryTime > now;
        }
    }   
    
    private static class ResultsCacheCleaner extends Task
    {
        private final Map<String, ResultsCacheEntry> cache;
        
        private final Logger log;
        
        private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        public ResultsCacheCleaner(Map<String,ResultsCacheEntry> cache, Logger log)
        {
            this.cache = cache;
            this.log = log;
        }
        
        public String getName()
        {
            return "CategoryQuery results cache cleaner";
        }
        
        @Override
        public void process(Context context)
            throws ProcessingException
        {
            log.debug("Cleaner thread started");
            while(!Thread.interrupted())
            {
                synchronized(this)
                {
                    try
                    {
                        wait(CACHE_EXPUNGE_INTERVAL);
                    }
                    catch(InterruptedException e)
                    {
                        return;
                    }
                }
                long now = System.currentTimeMillis();
                log.debug("woke up " + dateFormat.format(new Date(now)));
                synchronized(cache)
                {
                    Iterator<Map.Entry<String, ResultsCacheEntry>> i = cache.entrySet().iterator();
                    while(i.hasNext())
                    {
                        Map.Entry<String, ResultsCacheEntry> entry = i.next();
                        System.out.println(entry.getKey()+" will exipre at " + dateFormat.format(new Date(entry.getValue().expiryTime)));
                        if(!entry.getValue().isValid(now))
                        {
                            i.remove();
                            log.debug(entry.getKey()+" expired at " + dateFormat.format(new Date(entry.getValue().expiryTime))+ ", removed");
                        }
                        else
                        {
                            log.debug(entry.getKey()+" will exipre at " + dateFormat.format(new Date(entry.getValue().expiryTime)));
                        }
                    }
                }
            }
        }        
    }
}
