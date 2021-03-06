package net.cyklotron.cms.syndication;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.event.EventWhiteboard;
import org.picocontainer.Startable;

import net.cyklotron.cms.security.CmsSecurityException;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.BaseSiteListener;
import net.cyklotron.cms.site.SiteCreationListener;
import net.cyklotron.cms.site.SiteDestructionValve;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * Syndication Site Creation Listener implementation
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SyndicationListener.java,v 1.4 2007-11-18 21:23:33 rafal Exp $
 */
public class SyndicationListener 
    extends BaseSiteListener
    implements SiteCreationListener, SiteDestructionValve, Startable
{
    
    private SyndicationService syndicationService;
    
    public SyndicationListener(Logger logger, CoralSessionFactory sessionFactory,
        SecurityService cmsSecurityService, EventWhiteboard eventWhiteboard,
        SyndicationService syndicationService)
    {
        super(logger, sessionFactory, cmsSecurityService, eventWhiteboard);
        this.syndicationService = syndicationService;
        eventWhiteboard.addListener(SiteCreationListener.class,this,null);
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
        finally
        {
            coralSession.close();
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

    /**
     * {@inheritDoc}
     */
    public void clearApplication(CoralSession coralSession, SiteService siteService, SiteResource site) throws Exception
    {
        Resource root = syndicationService.getAppParent(coralSession, site);
        if(root == null)
        {
            return;
        }
        deleteSiteNode(coralSession, root);
    }
    
    public void clearSecurity(CoralSession coralSession, SiteService siteService, SiteResource site) throws Exception
    {
        
    }

    public void start()
    {
    }

    public void stop()
    {
    }
}
