package net.cyklotron.cms.modules.actions.site;

import net.labeo.Labeo;
import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Role;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Secure;

import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: BaseSiteAction.java,v 1.1 2005-01-24 04:35:11 pablo Exp $
 */
public abstract class BaseSiteAction
    extends BaseCMSAction
    implements Secure
{
    /** logging facility */
    protected Logger log;

    /** structure service */
    protected SiteService ss;
    
    public BaseSiteAction()
    {
        log = ((LoggingService)Labeo.getBroker().getService(LoggingService.SERVICE_NAME)).getFacility(SiteService.LOGGING_FACILITY);
        ss = (SiteService)Labeo.getBroker().getService(SiteService.SERVICE_NAME);
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        Role role = coralSession.getSecurity().getUniqueRole("cms.administrator");
        return coralSession.getUserSubject().hasRole(role);
    }
}
