package net.cyklotron.cms.site;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSessionFactory;

import net.cyklotron.cms.security.SecurityService;

/**
 * Base site listener for listener initialisation reuse.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseSiteListener.java,v 1.4 2005-02-09 22:21:08 rafal Exp $
 */
public class BaseSiteListener
{
    /** logging service */
    protected Logger log;

    /** coral session factory */
    protected CoralSessionFactory sessionFactory;

    /** site service */
    protected SiteService siteService;

    /** cms security service */
    protected SecurityService cmsSecurityService;

    public BaseSiteListener(Logger logger, CoralSessionFactory sessionFactory,
        SiteService siteService, SecurityService cmsSecurityService)
    {
        this.log = logger;
        this.sessionFactory = sessionFactory;
        this.siteService = siteService;
        this.cmsSecurityService = cmsSecurityService;
    }
}
