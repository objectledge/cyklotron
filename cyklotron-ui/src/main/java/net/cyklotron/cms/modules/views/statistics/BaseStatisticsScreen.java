package net.cyklotron.cms.modules.views.statistics;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.database.Database;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;

/**
 * link application base screen
 * @version $Id: BaseStatisticsScreen.java,v 1.4 2007-02-25 14:19:03 pablo Exp $
 */
public abstract class BaseStatisticsScreen extends BaseCMSScreen
{
    /** database service */
    protected Database databaseService;
    
    /** authentication service */
    protected UserManager userManager;

	/** category service */
	protected CategoryService categoryService;

    
    
    
    public BaseStatisticsScreen(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, Database database, UserManager userManager,
        CategoryService categoryService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.databaseService = database;
        this.userManager = userManager;
        this.categoryService = categoryService;
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
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
            logger.error("Subject has no rights to view this screen",e);
            return false;
        }
    }
}
