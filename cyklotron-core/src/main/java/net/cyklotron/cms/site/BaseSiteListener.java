package net.cyklotron.cms.site;

import net.cyklotron.cms.security.SecurityService;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSessionFactory;

/**
 * Base site listener for listener initialisation reuse.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseSiteListener.java,v 1.3 2005-01-17 14:19:42 pablo Exp $
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
