package net.cyklotron.cms.modules.views.periodicals;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;

/**
 * The default void screen assember for periodicals application.
 */
public abstract class BasePeriodicalsScreen
    extends BaseCMSScreen
{
    /** Periodicals service */
    protected PeriodicalsService periodicalsService;    

    
    
    public BasePeriodicalsScreen(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, PeriodicalsService periodicalsService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.periodicalsService = periodicalsService;
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        try
        {
            CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
            SiteResource site = getSite();
            Resource root = periodicalsService.getApplicationRoot(coralSession, site);
            Permission permission = coralSession.getSecurity().
            getUniquePermission("cms.periodicals.administer");
            return coralSession.getUserSubject().hasPermission(root, permission);
        }
        catch(Exception e)
        {
            logger.error("failed to check permissions for periodical action");
            return false;
        }
    }
}
