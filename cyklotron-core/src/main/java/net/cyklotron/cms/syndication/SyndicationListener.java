package net.cyklotron.cms.syndication;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.event.EventWhiteboard;

import net.cyklotron.cms.security.CmsSecurityException;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.BaseSiteListener;
import net.cyklotron.cms.site.SiteCreationListener;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * Syndication Site Creation Listener implementation
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SyndicationListener.java,v 1.1 2005-06-16 11:14:22 zwierzem Exp $
 */
public class SyndicationListener 
    extends BaseSiteListener
    implements SiteCreationListener
{
    public SyndicationListener(Logger logger, CoralSessionFactory sessionFactory,
        SecurityService cmsSecurityService, EventWhiteboard eventWhiteboard)
    {
        super(logger, sessionFactory, cmsSecurityService, eventWhiteboard);
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
        try
        {
            CoralSession coralSession = sessionFactory.getRootSession();
            SiteResource site = siteService.getSite(coralSession, name);
            setupRoles(coralSession, site);
        }
        catch(SiteException e)
        {
            log.error("Could not get site root: ",e);
        }
        catch(CmsSecurityException e)
        {
            log.error("Could not create the site: ",e);
        }
    }

    public void setupRoles(CoralSession coralSession, SiteResource site) throws CmsSecurityException
    {
        Role administrator = site.getAdministrator();
        cmsSecurityService.createRole(coralSession, administrator, 
            "cms.syndication.administrator", site);
        cmsSecurityService.createRole(coralSession, administrator, 
            "cms.syndication.infeed.administrator", site);
        cmsSecurityService.createRole(coralSession, administrator, 
            "cms.syndication.outfeed.administrator", site);
    }
}
