package net.cyklotron.cms.modules.views.search;

import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.search.ExternalPoolResource;
import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.PoolResource;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.search.SearchUtil;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;

/**
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseSearchScreen.java,v 1.1 2005-01-24 04:35:07 pablo Exp $
 */
public class BaseSearchScreen extends BaseCMSScreen implements Secure
{
    /** logging facility */
    protected Logger log;
    
    /** search service */
    protected SearchService searchService;
    
    public BaseSearchScreen()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(SearchService.LOGGING_FACILITY);
        searchService = (SearchService)broker.getService(SearchService.SERVICE_NAME);
    }

    public IndexResource getIndex(RunData data)
        throws ProcessingException
    {
        return SearchUtil.getIndex(coralSession, data);
    }

    public PoolResource getPool(RunData data)
        throws ProcessingException
    {
        return SearchUtil.getPool(coralSession, data);
    }

    public ExternalPoolResource getExternalPool(RunData data)
        throws ProcessingException
    {
        return SearchUtil.getExternalPool(coralSession, data);
    }

    public boolean checkPermission(Context context, CoralSession coralSession, String permissionName)
        throws ProcessingException
    {
        return SearchUtil.checkPermission(coralSession, data, permissionName);
    }
}

