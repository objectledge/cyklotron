package net.cyklotron.cms.banner;

import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.BaseSiteListener;
import net.cyklotron.cms.site.SiteCreationListener;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;

/**
 * Banner Listener implementation
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: BannerListener.java,v 1.2 2005-01-18 09:33:29 pablo Exp $
 */
public class BannerListener
    extends BaseSiteListener
    implements SiteCreationListener
{
    /** banner service */
    private BannerService bannerService;

    public BannerListener(Logger logger, CoralSessionFactory sessionFactory,
        SiteService siteService, SecurityService cmsSecurityService, BannerService bannerService)
    {
        super(logger, sessionFactory, siteService, cmsSecurityService);
        this.bannerService = bannerService;
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
    public void createSite(String template, String name)
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
}
