package net.cyklotron.cms.banner;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.BaseSiteListener;
import net.cyklotron.cms.site.SiteCreationListener;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * Banner Listener implementation
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: BannerListener.java,v 1.4 2005-03-23 07:53:28 rafal Exp $
 */
public class BannerListener
    extends BaseSiteListener
    implements SiteCreationListener
{
    /** banner service */
    private BannerService bannerService;

    public BannerListener(Logger logger, CoralSessionFactory sessionFactory,
        SecurityService cmsSecurityService, BannerService bannerService)
    {
        super(logger, sessionFactory, cmsSecurityService);
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
}
