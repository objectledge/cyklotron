package net.cyklotron.cms.site;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSessionFactory;

import net.cyklotron.cms.security.SecurityService;

/**
 * Base site listener for listener initialisation reuse.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseSiteListener.java,v 1.5 2005-03-23 07:53:27 rafal Exp $
 */
public class BaseSiteListener
{
    /** logging service */
    protected Logger log;

    /** coral session factory */
    protected CoralSessionFactory sessionFactory;

    /** cms security service */
    protected SecurityService cmsSecurityService;

    public BaseSiteListener(Logger logger, CoralSessionFactory sessionFactory,
        SecurityService cmsSecurityService)
    {
        this.log = logger;
        this.sessionFactory = sessionFactory;
        this.cmsSecurityService = cmsSecurityService;
    }
}
