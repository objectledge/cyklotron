package net.cyklotron.cms.category.query.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.relation.CoralRelationQuery;
import org.objectledge.coral.relation.query.parser.TokenMgrError;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

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
    
    /**
     * Initializes the service.
     */
    public CategoryQueryServiceImpl(Logger logger, CategoryService categoryService,
        CoralSessionFactory sessionFactory)
    {
        this.log = logger;
		this.categoryService = categoryService;
        this.sessionFactory = sessionFactory;
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
}
