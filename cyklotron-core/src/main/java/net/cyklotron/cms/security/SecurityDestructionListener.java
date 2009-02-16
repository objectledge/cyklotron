package net.cyklotron.cms.security;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.event.EventWhiteboard;

import net.cyklotron.cms.site.BaseSiteListener;
import net.cyklotron.cms.site.SiteDestructionValve;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * Aggregation Listener implementation
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: SecurityDestructionListener.java,v 1.1 2005-05-31 17:12:10 pablo Exp $
 */
public class SecurityDestructionListener 
    extends BaseSiteListener 
    implements SiteDestructionValve
{
    public SecurityDestructionListener(Logger logger, CoralSessionFactory sessionFactory,
        SecurityService cmsSecurityService, EventWhiteboard eventWhiteboard)
    {
        super(logger, sessionFactory, cmsSecurityService, eventWhiteboard);
    }
    
    // listeners implementation ////////////////////////////////////////////////////////

    /**
     * {@inheritDoc}
     */
    public void clearApplication(CoralSession coralSession, SiteService siteService, SiteResource site) throws Exception
    {
        cmsSecurityService.cleanupSite(coralSession, site);
        Resource[] res = coralSession.getStore().getResource(site, "security");
        if(res.length > 0)
        {
            deleteSiteNode(coralSession, res[0]);
        }
    }
    
    public void clearSecurity(CoralSession coralSession, SiteService siteService, SiteResource site) throws Exception
    {
        
    }
}
