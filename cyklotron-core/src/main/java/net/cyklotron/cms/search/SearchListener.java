package net.cyklotron.cms.search;

import net.labeo.services.resource.Role;

import net.cyklotron.cms.security.CmsSecurityException;
import net.cyklotron.cms.site.BaseSiteListener;
import net.cyklotron.cms.site.SiteCreationListener;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;

/**
 * Search Listener implementation
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SearchListener.java,v 1.1 2005-01-12 20:44:36 pablo Exp $
 */
public class SearchListener extends BaseSiteListener implements SiteCreationListener
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
                "cms.search.administrator", site, rootSubject);
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
