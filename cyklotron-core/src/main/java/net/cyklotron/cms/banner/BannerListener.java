package net.cyklotron.cms.banner;

import org.jcontainer.dna.Logger;
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
 * Banner Listener implementation
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: BannerListener.java,v 1.5 2005-05-31 17:09:58 pablo Exp $
 */
public class BannerListener
    extends BaseSiteListener
    implements SiteCreationListener, SiteDestructionValve, Startable
{
    /** banner service */
    private BannerService bannerService;

    public BannerListener(Logger logger, CoralSessionFactory sessionFactory,
        SecurityService cmsSecurityService, EventWhiteboard eventWhiteboard,
         BannerService bannerService)
    {
        super(logger, sessionFactory, cmsSecurityService, eventWhiteboard);
        this.bannerService = bannerService;
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

    //  --------------------       listeners implementation  ----------------------
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
            BannersResource bannersRoot = bannerService.getBannersRoot(coralSession, site);
            Resource[] nodes = coralSession.getStore().getResource(site, "security");
            if(nodes.length != 1)
            {
                log.error("Security node for site couldn't be found");
            }
            cmsSecurityService.createRole(coralSession, site.getAdministrator(), 
                "cms.banner.banners.administrator", bannersRoot);
        }
        catch(Exception e)
        {
            log.error("BannerListener Exception: ",e);
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
        Resource[] res = coralSession.getStore().getResource(site, "applications");
        if(res.length > 0)
        {
            res = coralSession.getStore().getResource(res[0], "banners");
            if(res.length > 0)
            {
                deleteSiteNode(coralSession, res[0]);
            }
        }    
    }


    public void clearSecurity(CoralSession coralSession, SiteService siteService, SiteResource site) throws Exception
    {
        Resource[] res = coralSession.getStore().getResource(site, "applications");
        if(res.length > 0)
        {
            res = coralSession.getStore().getResource(res[0], "banners");
            if(res.length > 0)
            {
                deleteSiteNode(coralSession, res[0]);
            }
        }    
    }
}
