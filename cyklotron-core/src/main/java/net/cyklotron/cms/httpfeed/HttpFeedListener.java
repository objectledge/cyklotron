package net.cyklotron.cms.httpfeed;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;

import net.cyklotron.cms.banner.BannerService;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.BaseSiteListener;
import net.cyklotron.cms.site.SiteCreationListener;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * HttpFeed Site Creation Listener implementation
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: HttpFeedListener.java,v 1.4 2005-03-23 07:53:23 rafal Exp $
 */
public class HttpFeedListener 
    extends BaseSiteListener
    implements SiteCreationListener
{
    // listeners implementation ////////////////////////////////////////////////////////

    public HttpFeedListener(Logger logger, CoralSessionFactory sessionFactory,
        SecurityService cmsSecurityService, BannerService bannerService)
    {
        super(logger, sessionFactory, cmsSecurityService);
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
}
