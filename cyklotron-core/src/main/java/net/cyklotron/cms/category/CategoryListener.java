package net.cyklotron.cms.category;

import net.cyklotron.cms.security.CmsSecurityException;
import net.cyklotron.cms.site.BaseSiteListener;
import net.cyklotron.cms.site.SiteCreationListener;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.labeo.services.resource.Role;

/**
 * Category Site Creation Listener implementation
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryListener.java,v 1.1 2005-01-12 20:44:28 pablo Exp $
 */
public class CategoryListener
extends BaseSiteListener
implements SiteCreationListener
{
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
    public void createSite(String template, String name)
    {
        init();
        try
        {
            SiteResource site = siteService.getSite(name);
            Role administrator = site.getAdministrator();
            cmsSecurityService.createRole(administrator, 
                "cms.category.administrator", site, rootSubject);
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
}
