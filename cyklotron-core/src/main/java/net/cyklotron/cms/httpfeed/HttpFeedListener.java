package net.cyklotron.cms.httpfeed;

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
 * HttpFeed Site Creation Listener implementation
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: HttpFeedListener.java,v 1.5 2005-05-31 17:10:24 pablo Exp $
 */
public class HttpFeedListener 
    extends BaseSiteListener
    implements SiteCreationListener, SiteDestructionValve, Startable
{
    // listeners implementation ////////////////////////////////////////////////////////

    private HttpFeedService httpFeedService; 
    
    public HttpFeedListener(Logger logger, CoralSessionFactory sessionFactory,
        SecurityService cmsSecurityService, EventWhiteboard eventWhiteboard,
        HttpFeedService httpFeedService)
    {
        super(logger, sessionFactory, cmsSecurityService, eventWhiteboard);
        this.httpFeedService = httpFeedService; 
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
                "cms.httpfeed.administrator", site);
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
        Resource root = null;
        try
        {
            root = httpFeedService.getFeedsParent(coralSession, site);
        }
        catch(HttpFeedException e)
        {
            // ignore it.
        }
        if(root == null)
        {
            return;
        }
        deleteSiteNode(coralSession, root);
    }
    
    public void clearSecurity(CoralSession coralSession, SiteService siteService, SiteResource site) throws Exception
    {
        
    }
}
