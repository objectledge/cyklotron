package net.cyklotron.cms.modules.views.statistics;

import net.labeo.services.authentication.AuthenticationService;
import net.labeo.services.database.DatabaseService;
import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Role;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.site.SiteResource;

/**
 * link application base screen
 * @version $Id: BaseStatisticsScreen.java,v 1.1 2005-01-24 04:35:03 pablo Exp $
 */
public class BaseStatisticsScreen extends BaseCMSScreen
{
    /** logging facility */
    protected Logger log;
    
    /** database service */
    protected DatabaseService databaseService;
    
    /** authentication service */
    protected AuthenticationService authenticationService;

	/** category service */
	protected CategoryService categoryService;

    public BaseStatisticsScreen()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME))
        	.getFacility("statistics");
        databaseService = (DatabaseService)broker.getService(DatabaseService.SERVICE_NAME);
		authenticationService = (AuthenticationService)broker.
			getService(AuthenticationService.SERVICE_NAME);
		categoryService = (CategoryService)broker.getService(CategoryService.SERVICE_NAME);
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        try
        {
 			SiteResource site = getSite();
 			Role role = null;
 			if(site != null)
 			{
 				role = site.getAdministrator();
 			}
 			else
 			{
 				role = coralSession.getSecurity().getUniqueRole("cms.administrator");
 			}
 			return coralSession.getUserSubject().hasRole(role);
        }
        catch(ProcessingException e)
        {
            log.error("Subject has no rights to view this screen",e);
            return false;
        }
    }
}
