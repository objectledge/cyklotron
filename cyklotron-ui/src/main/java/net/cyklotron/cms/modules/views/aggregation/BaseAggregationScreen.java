package net.cyklotron.cms.modules.views.aggregation;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.security.Role;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.security.CmsSecurityException;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteService;

/**
 * The default void screen assember for aggregation application.
 */
public abstract class BaseAggregationScreen
    extends BaseCMSScreen
    implements Secure
{
    /** logging facility */
    protected Logger log;

    /** structure service */
    protected SiteService siteService;

    /** aggregation service */
    protected AggregationService aggregationService;    

    /** security service */
    protected SecurityService securityService;
    

    public BaseAggregationScreen()
    {
        log = ((LoggingService)broker.
            getService(LoggingService.SERVICE_NAME)).
                getFacility("site");
        siteService = (SiteService)broker.
            getService(SiteService.SERVICE_NAME);
        aggregationService = (AggregationService)broker.
            getService(AggregationService.SERVICE_NAME);
        securityService = (SecurityService)broker.
            getService(SecurityService.SERVICE_NAME);
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
    	try
    	{
        	Role role = securityService.getRole("cms.aggregation.export.administrator",getSite());
        	return coralSession.getUserSubject().hasRole(role);
    	}
    	catch(CmsSecurityException e)
    	{
    		log.error("CmsSecurityException occured during access checking",e);
    		return false;
    	}
    }
}
