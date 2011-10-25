package net.cyklotron.cms.category;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.AmbigousEntityNameException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.relation.Relation;
import org.objectledge.coral.relation.RelationModification;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.SubtreeVisitor;
import org.objectledge.event.EventWhiteboard;
import org.objectledge.visitor.DispatchOrder;
import org.objectledge.visitor.Visitor;
import org.picocontainer.Startable;

import net.cyklotron.cms.category.internal.CategoryServiceImpl;
import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryRootResource;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.BaseSiteListener;
import net.cyklotron.cms.site.SiteCreationListener;
import net.cyklotron.cms.site.SiteDestructionValve;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * Category Site Creation Listener implementation
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryListener.java,v 1.6 2007-01-21 17:09:35 pablo Exp $
 */
public class CategoryListener
extends BaseSiteListener
implements SiteCreationListener, SiteDestructionValve, Startable
{
    private CategoryService categoryService;
    
    public CategoryListener(Logger logger, CoralSessionFactory sessionFactory,
        SecurityService cmsSecurityService, EventWhiteboard eventWhiteboard,
        CategoryService categoryService)
    {
        super(logger, sessionFactory, cmsSecurityService, eventWhiteboard);
        this.categoryService = categoryService;
        eventWhiteboard.addListener(SiteCreationListener.class,this,null);
    }

    /**
     * {@inheritDoc}
     */
    public void start()
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void stop()
    {
    }

    // listeners implementation ////////////////////////////////////////////////////////

    /**
     * Called when a new site is created.
     *
     * <p>The method will be called after the site Resources are successfully
     * copied from the template.</p>
     *
     * @param template the site template name.
     * @param name the site name.
     */
    public void createSite(SiteService siteService, String template, String name)
    {
        CoralSession coralSession = sessionFactory.getRootSession();
        try
        {
            SiteResource site = siteService.getSite(coralSession, name);
            Role administrator = site.getAdministrator();
            cmsSecurityService.createRole(coralSession, administrator, 
                "cms.category.administrator", site);
            Role role = cmsSecurityService.createRole(coralSession, administrator, 
                "cms.category.classifier", site);
            
        }
        catch(Exception e)
        {
            log.error("Could not get site root: ",e);
        }
        finally
        {
            coralSession.close();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void clearApplication(final CoralSession coralSession, SiteService siteService,
        SiteResource site)
        throws Exception
    {
        Resource[] res = coralSession.getStore().getResource(site, "categories");
        if(res.length > 0)
        {
            Visitor<Resource> visitor = new SubtreeVisitor()
                {
                    @SuppressWarnings("unused")
                    @DispatchOrder(1)
                    public void visit(CategoryResource category)
                        throws EntityDoesNotExistException, AmbigousEntityNameException
                    {
                        Relation rel = coralSession.getRelationManager().getRelation(
                            CategoryServiceImpl.CATEGORY_RESOURCE_CLASS_RELATION_NAME);
                        RelationModification mod = new RelationModification();
                        mod.remove(category);
                        coralSession.getRelationManager().updateRelation(rel, mod);

                        rel = coralSession.getRelationManager().getRelation(
                            CategoryServiceImpl.CATEGORY_RESOURCE_RELATION_NAME);
                        mod = new RelationModification();
                        mod.remove(category);
                        coralSession.getRelationManager().updateRelation(rel, mod);
                    }
                };
            visitor.traverseDepthFirst(res[0]);
        }

        res = coralSession.getStore().getResource(site, "applications");
        if(res.length > 0)
        {
            res = coralSession.getStore().getResource(res[0], "category_query");
            if(res.length > 0)
            {
                if(res[0] instanceof CategoryQueryRootResource)
                {
                    CategoryQueryRootResource cqr = (CategoryQueryRootResource)res[0];
                    cqr.setDefaultQuery(null);
                    cqr.setResultsNode(null);
                    cqr.update();

                    Visitor<Resource> visitor = new SubtreeVisitor()
                        {
                            @SuppressWarnings("unused")
                            @DispatchOrder(1)
                            public void visit(CategoryQueryPoolResource pool)
                            {
                                pool.setQueries(null);
                                pool.update();
                            }
                        };
                    visitor.traverseDepthFirst(res[0]);
                }
            }
        }
    }
    

    public void clearSecurity(CoralSession coralSession, SiteService siteService, SiteResource site) throws Exception
    {
        
    }
}
