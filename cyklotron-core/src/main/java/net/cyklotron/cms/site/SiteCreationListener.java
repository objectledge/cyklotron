package net.cyklotron.cms.site;

/**
 * Implemented by classes that need to take part in site creation.
 *
 * @version $Id: SiteCreationListener.java,v 1.2 2005-03-23 07:53:27 rafal Exp $
 * @author <a href="mailto:rkrzewsk@ngo.pl">Rafal Krzewski</a>
 */
public interface SiteCreationListener
{
    /**
     * Called when a new site is created.
     *
     * <p>The method will be called after the site Resources are successfully
     * copied from the template.</p>
     *
     * @param template the site template name.
     * @param name the site name.
     */
    public void createSite(SiteService siteService, String template, String name);
}

