package net.cyklotron.cms.category.query.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.cyklotron.cms.category.CategoryMapResource;
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
import net.labeo.services.BaseService;
import net.labeo.services.InitializationError;
import net.labeo.services.logging.LoggingFacility;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceClass;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.resource.generic.CrossReference;

/**
 * Implementation of Category Query Service.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryServiceImpl.java,v 1.1 2005-01-12 20:44:59 pablo Exp $
 */
public class CategoryQueryServiceImpl extends BaseService
	implements CategoryQueryService
{
    /** logging facility */
    private LoggingFacility log;

    /** resource service */
    private ResourceService resourceService;

	/** category service */
	private CategoryService categoryService;

    /** system category root */
    private CategoryMapResource categoryMap;

    /** system root subject */
    private Subject rootSubject;

    /**
     * Initializes the service.
     */
    public void init()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(LOGGING_FACILITY);
        resourceService = (ResourceService)broker.getService(ResourceService.SERVICE_NAME);
		categoryService = (CategoryService)broker.getService(CategoryService.SERVICE_NAME);
    
        categoryMap = categoryService.getCategoryMap();

        try
        {
            rootSubject = resourceService.getSecurity().getSubject(Subject.ROOT);
        }
        catch (EntityDoesNotExistException e)
        {
            throw new InitializationError("cannot get root subject", e);
        }
    }

	// resource management ///////////////////////////////////////////////////////////////

    public Resource getCategoryQueryRoot(SiteResource site)
	throws CategoryQueryException
    {
		return getCreateResource(site, getRoot(site), "node", QUERY_ROOT);
    }

	public Resource getCategoryQueryPoolRoot(SiteResource site)
	throws CategoryQueryException
	{
		return getCreateResource(site, getRoot(site), "node", POOL_ROOT);
	}

	private CategoryQueryRootResource getRoot(SiteResource site)
	throws CategoryQueryException
	{
		return (CategoryQueryRootResource)getCreateResource(site, 
            getApplications(site), "category.query.root", ROOT);
	}

	private Resource getCreateResource(SiteResource site, Resource parent, String className, String name)
	throws CategoryQueryException
	{
		Resource[] res = resourceService.getStore().getResource(parent, name);
		if (res.length == 1)
		{
			return res[0];
		}
		if (res.length == 0)
		{
			try
			{
                ResourceClass rc = resourceService.getSchema().getResourceClass(className);
                return resourceService.getStore().createResource(name, parent, rc, new HashMap(), rootSubject);
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
		}
		throw new CategoryQueryException(
			"too many '"+name+"' resources for site '"+site.getName()+"'");
	}

	private Resource getApplications(SiteResource site)
	throws CategoryQueryException
	{
		Resource[] res = resourceService.getStore().getResource(site, "applications");
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

    public Map initCategorySelection(String items, String state)
    {
		return initCategorySelection(CategoryQueryUtil.splitCategoryIdentifiers(items), state);
    }

	public Map initCategorySelection(String[] items, String state)
	{
		if(items == null || items.length == 0)
		{
			return new HashMap();
		}
		CategoryResolver resolver = getCategoryResolver();
		Map map = new HashMap();
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

    public NavigationNodeResource getResultsNode(SiteResource site)
        throws CategoryQueryException
    {
        return getRoot(site).getResultsNode();
    }
    
    public CategoryQueryResource getDefaultQuery(SiteResource site)
        throws CategoryQueryException
    {
        return getRoot(site).getDefaultQuery();
    }

    public void setResultsNode(SiteResource site, NavigationNodeResource node)
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
        getRoot(site).setResultsNode(node);
        getRoot(site).update(rootSubject);
    }
        
    public void setDefaultQuery(SiteResource site, CategoryQueryResource query)
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
        getRoot(site).setDefaultQuery(query);
        getRoot(site).update(rootSubject);
    }

    // queries /////////////////////////////////////////////////////////////////////////

	public Resource[] forwardQuery(String query) throws Exception
	{
		if(query != null && query.length() > 0)
		{
			CrossReference refs = categoryMap.getReferences();
			return refs.query(query, getCategoryResolver());
		}
		return new Resource[0];
	}

    public Resource[] forwardQuery(String query, Set idSet) throws Exception
    {
        if(query != null && query.length() > 0)
        {
            CrossReference refs = categoryMap.getReferences();
            return refs.query(query, getCategoryResolver(), idSet);
        }
        return new Resource[0];
    }

	public Resource[] reverseQuery(String query) throws Exception
	{
		if(query != null && query.length() > 0)
		{
			CrossReference refs = categoryMap.getReferences();
			return refs.queryInv(query, getCategoryResolver());
		}
		return new Resource[0];
	}

    public Resource[] reverseQuery(String query, Set idSet) throws Exception
    {
        if(query != null && query.length() > 0)
        {
            CrossReference refs = categoryMap.getReferences();
            return refs.queryInv(query, getCategoryResolver(), idSet);
        }
        return new Resource[0];
    }

	private CategoryResolver resolver;

    public CategoryResolver getCategoryResolver()
    {
        if (resolver == null)
        {
            resolver = new CategoryResolver(this);
        }
        return resolver;
    }
}
