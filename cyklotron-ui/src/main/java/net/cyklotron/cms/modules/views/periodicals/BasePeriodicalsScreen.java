package net.cyklotron.cms.modules.views.periodicals;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.preferences.PreferencesService;

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
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
		Role role = coralSession.getSecurity().getUniqueRole("cms.administrator");
    	return coralSession.getUserSubject().hasRole(role);
    }
}
