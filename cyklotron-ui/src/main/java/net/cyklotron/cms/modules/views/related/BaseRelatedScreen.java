package net.cyklotron.cms.modules.views.related;

import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.related.RelatedService;

/**
 * The base screen class for related application screens
 */
public class BaseRelatedScreen
    extends BaseCMSScreen
{
    protected Logger log;
    
    protected RelatedService relatedService;
    
    public BaseRelatedScreen()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("related");
        relatedService = (RelatedService)broker.getService(RelatedService.SERVICE_NAME);
    }

    public boolean checkAccessRights(Context context)
    {
        // TODO
        return true;
    }

}
