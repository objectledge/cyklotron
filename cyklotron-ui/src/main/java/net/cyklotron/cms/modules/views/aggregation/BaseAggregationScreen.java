package net.cyklotron.cms.modules.views.aggregation;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.security.CmsSecurityException;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteService;

/**
 * The default void screen assember for aggregation application.
 */
public abstract class BaseAggregationScreen
    extends BaseCMSScreen
{
    /** structure service */
    protected SiteService siteService;

    /** aggregation service */
    protected AggregationService aggregationService;    

    /** security service */
    protected SecurityService securityService;

    public BaseAggregationScreen(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        SiteService siteService, AggregationService aggregationService, 
        SecurityService securityService, TableStateManager tableStateManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.siteService = siteService;
        this.aggregationService = aggregationService;
        this.securityService = securityService;
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
    	try
    	{
        	Role role = securityService.getRole(coralSession, "cms.aggregation.export.administrator",getSite());
        	return coralSession.getUserSubject().hasRole(role);
    	}
    	catch(CmsSecurityException e)
    	{
    		logger.error("CmsSecurityException occured during access checking",e);
    		return false;
    	}
    }
}
