package net.cyklotron.cms.modules.actions.related;

import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.related.RelatedConstants;
import net.cyklotron.cms.related.RelatedService;

/**
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: BaseRelatedAction.java,v 1.1 2005-01-24 04:34:41 pablo Exp $
 */
public abstract class BaseRelatedAction
    extends BaseCMSAction
    implements RelatedConstants
{
    protected RelatedService relatedService;
    
    protected Logger log;
    
    public BaseRelatedAction()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(RelatedService.LOGGING_FACILITY);
        relatedService = (RelatedService)broker.getService(RelatedService.SERVICE_NAME);
    }

    public boolean checkAccess(RunData data)
    {
        // TODO
        return true;
    }

}


