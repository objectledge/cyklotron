package net.cyklotron.cms.site;

import org.objectledge.coral.session.CoralSession;

/**
 * Implemented by classes that need to take part in site destruction.
 *
 * @version $Id: SiteDestructionValve.java,v 1.1 2005-05-31 17:10:12 pablo Exp $
 * @author <a href="mailto:rkrzewsk@ngo.pl">Rafal Krzewski</a>
 */
public interface SiteDestructionValve
{
    /**
     * Called by destruction manager.
     *
     * <p>The method will be called before site Resources are destroyed.</p>
     * @param coralSession TODO
     * @param name the name of the site to destroy.
     * @throws Exception TODO
     */
    public void clearApplication(CoralSession coralSession, 
        SiteService siteService, SiteResource site) throws Exception;
    
    
    public void clearSecurity(CoralSession coralSession, SiteService siteService, SiteResource site) throws Exception;
}

