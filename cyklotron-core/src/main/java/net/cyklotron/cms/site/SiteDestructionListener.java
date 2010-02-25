package net.cyklotron.cms.site;

/**
 * Implemented by classes that need to take part in site destruction.
 *
 * @version $Id: SiteDestructionListener.java,v 1.2 2005-05-31 17:10:12 pablo Exp $
 * @author <a href="mailto:rkrzewsk@ngo.pl">Rafal Krzewski</a>
 */
public interface SiteDestructionListener
{
    /**
     * Called when a site is destroyed.
     *
     * <p>The method will be called before site Resources are destroyed.</p>
     *
     * @param name the name of the site to destroy.
     */
    public void destroySite(SiteService siteService, SiteResource site);
}

