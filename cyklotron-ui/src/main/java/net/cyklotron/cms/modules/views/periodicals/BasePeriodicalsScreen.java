package net.cyklotron.cms.modules.views.periodicals;

import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Role;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Secure;

/**
 * The default void screen assember for periodicals application.
 */
public abstract class BasePeriodicalsScreen
    extends BaseCMSScreen
    implements Secure
{
    /** logging facility */
    protected Logger log;

    /** Periodicals service */
    protected PeriodicalsService periodicalsService;    

    public BasePeriodicalsScreen()
    {
        log = ((LoggingService)broker.
            getService(LoggingService.SERVICE_NAME)).
                getFacility(PeriodicalsService.LOGGING_FACILITY);
        periodicalsService = (PeriodicalsService)broker.getService(PeriodicalsService.SERVICE_NAME);
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
		Role role = coralSession.getSecurity().getUniqueRole("cms.administrator");
    	return coralSession.getUserSubject().hasRole(role);
    }
}
