package net.cyklotron.cms.modules.actions.aggregation;

import net.labeo.Labeo;
import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Role;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Secure;

import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteService;

/**
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: BaseAggregationAction.java,v 1.1 2005-01-24 04:35:18 pablo Exp $
 */
public abstract class BaseAggregationAction
    extends BaseCMSAction
    implements Secure
{
    /** logging facility */
    protected Logger log;

    /** structure service */
    protected SiteService siteService;
    
    /** aggregation service */
    protected AggregationService aggregationService;
    
    public BaseAggregationAction()
    {
        log = ((LoggingService)Labeo.getBroker().
            getService(LoggingService.SERVICE_NAME)).
                getFacility(SiteService.LOGGING_FACILITY);
        siteService = (SiteService)Labeo.getBroker().
            getService(SiteService.SERVICE_NAME);
        aggregationService = (AggregationService)Labeo.getBroker().
            getService(AggregationService.SERVICE_NAME);
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        Role role = coralSession.getSecurity().getUniqueRole("cms.administrator");
        return coralSession.getUserSubject().hasRole(role);
    }
}
