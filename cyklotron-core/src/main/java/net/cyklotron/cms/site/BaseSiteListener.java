package net.cyklotron.cms.site;

import net.labeo.Labeo;
import net.labeo.services.InitializationError;
import net.labeo.services.ServiceBroker;
import net.labeo.services.logging.LoggingFacility;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Subject;

import net.cyklotron.cms.security.SecurityService;

/**
 * Base site listener for listener initialisation reuse.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseSiteListener.java,v 1.1 2005-01-12 20:44:43 pablo Exp $
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
