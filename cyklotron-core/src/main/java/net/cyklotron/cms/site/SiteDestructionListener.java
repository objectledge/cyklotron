package net.cyklotron.cms.site;

/**
 * Implemented by classes that need to take part in site destruction.
 *
 * @version $Id: SiteDestructionListener.java,v 1.1 2005-01-12 20:44:43 pablo Exp $
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
    public void destroySite(String name);
}

