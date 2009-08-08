package net.cyklotron.cms.site;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.event.EventWhiteboard;

import net.cyklotron.cms.security.SecurityService;

/**
 * Base site listener for listener initialisation reuse.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseSiteListener.java,v 1.6 2005-05-31 17:10:12 pablo Exp $
 */
public class BaseSiteListener
{
    /** logging service */
    protected Logger log;

    /** coral session factory */
    protected CoralSessionFactory sessionFactory;

    /** cms security service */
    protected SecurityService cmsSecurityService;

    /** event whiteboard */
    protected EventWhiteboard eventWhiteboard;
    
    public BaseSiteListener(Logger logger, CoralSessionFactory sessionFactory,
        SecurityService cmsSecurityService, EventWhiteboard eventWhiteboard)
    {
        this.log = logger;
        this.sessionFactory = sessionFactory;
        this.cmsSecurityService = cmsSecurityService;
        this.eventWhiteboard = eventWhiteboard;
    }
    
    protected void deleteSiteNode(CoralSession coralSession, Resource node)
        throws Exception
    {
        Resource[] children = coralSession.getStore().getResource(node);
        for(Resource child: children)
        {
            deleteSiteNode(coralSession, child);
        }
        coralSession.getStore().deleteResource(node);
    }
}
