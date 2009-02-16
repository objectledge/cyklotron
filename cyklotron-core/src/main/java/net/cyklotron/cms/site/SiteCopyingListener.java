package net.cyklotron.cms.site;

/**
 * Implemented by classes that need to take part in site copying.
 *
 * @version $Id: SiteCopyingListener.java,v 1.1 2005-01-12 20:44:43 pablo Exp $
 * @author <a href="mailto:rkrzewsk@ngo.pl">Rafal Krzewski</a>
 */
public interface SiteCopyingListener
{
    /**
     * Called when an existing non-template site is copied.
     *
     * <p>The method will be called after the site Resources are successfully
     * copied.</p>
     *
     * @param source the name of the site to copy.
     * @param destination the destination site name.
     */
    public void copySite(String source, String destination);
}

