package net.cyklotron.cms.site;

import net.cyklotron.cms.security.SecurityService;

import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Subject;

/**
 * Base site listener for listener initialisation reuse.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseSiteListener.java,v 1.2 2005-01-13 11:46:33 pablo Exp $
 */
public class BaseSiteListener
{
    /** logging service */
    protected LoggingFacility log;

    /** resource service */
    protected ResourceService resourceService;

    /** site service */
    protected SiteService siteService;

    /** cms security service */
    protected SecurityService cmsSecurityService;

    /** system root subject */
    protected Subject rootSubject;

    /** init switch */
    protected boolean initialized;

    public BaseSiteListener()
    {
        initialized = false;
    }

    protected synchronized void init()
    {
        if(!initialized)
        {
            ServiceBroker broker = Labeo.getBroker();
            log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(SiteService.LOGGING_FACILITY);
            resourceService = (ResourceService)broker.getService(ResourceService.SERVICE_NAME);
            siteService = (SiteService)broker.getService(SiteService.SERVICE_NAME);
            cmsSecurityService = (SecurityService)broker.getService(SecurityService.SERVICE_NAME);

            try
            {
                rootSubject = resourceService.getSecurity().getSubject(Subject.ROOT);
            }
            catch(EntityDoesNotExistException e)
            {
                throw new InitializationError("Couldn't find root subject");
            }
            initialized = true;
        }
    }
}
