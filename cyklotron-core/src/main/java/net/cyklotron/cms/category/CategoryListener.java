package net.cyklotron.cms.category;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.event.EventWhiteboard;
import org.picocontainer.Startable;

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
 * @version $Id: CategoryListener.java,v 1.5 2005-05-31 17:12:42 pablo Exp $
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
    public void clearApplication(CoralSession coralSession, SiteService siteService, SiteResource site) throws Exception
    {
        Resource[] res = coralSession.getStore().getResource(site, "categories");
        if(res.length > 0)
        {
            unbindAndDelete(coralSession, res[0]);
        }
    }
    

    public void clearSecurity(CoralSession coralSession, SiteService siteService, SiteResource site) throws Exception
    {
        
    }
    
    protected void unbindAndDelete(CoralSession coralSession, Resource node)
        throws Exception
    {
        Resource[] children = coralSession.getStore().getResource(node);
        for(Resource child: children)
        {
            unbindAndDelete(coralSession, child);
        }
        if(node instanceof CategoryResource)
        {
            categoryService.deleteCategory(coralSession, (CategoryResource)node);
        }
        else
        {
            coralSession.getStore().deleteResource(node);
        }
    }
}
