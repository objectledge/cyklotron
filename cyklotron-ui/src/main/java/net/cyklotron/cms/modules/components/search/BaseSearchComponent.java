package net.cyklotron.cms.modules.components.search;

import net.cyklotron.cms.modules.components.BaseCMSComponent;
import net.cyklotron.cms.search.SearchService;

/**
 * The base component class for search app
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseSearchComponent.java,v 1.1 2005-01-24 04:35:15 pablo Exp $
 */
public class BaseSearchComponent
    extends BaseCMSComponent
{
    /** search service */
    protected SearchService searchService;

    public BaseSearchComponent()
    {
        ServiceBroker broker = Labeo.getBroker();
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).
            getFacility(SearchService.LOGGING_FACILITY);
        searchService = (SearchService)broker.getService(SearchService.SERVICE_NAME);
    }
}
