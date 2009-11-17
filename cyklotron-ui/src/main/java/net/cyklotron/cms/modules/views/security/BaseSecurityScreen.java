package net.cyklotron.cms.modules.views.security;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.security.SecurityService;

/**
 * The default void screen assember for security sub-application.
 */
public abstract class BaseSecurityScreen
    extends BaseCMSScreen
{
    /** security service */
    protected SecurityService cmsSecurityService;

    public BaseSecurityScreen(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, SecurityService securityService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.cmsSecurityService = securityService;
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return checkAdministrator(context.getAttribute(CoralSession.class));
    }
}
