package net.cyklotron.cms.modules.views.security;

import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;

import org.objectledge.pipeline.ProcessingException;

/**
 * The default void screen assember for security sub-application.
 */
public abstract class BaseSecurityScreen
    extends BaseCMSScreen
    implements Secure
{
    /** logging facility */
    protected Logger log;

    /** security service */
    protected SecurityService cmsSecurityService;

    /** table service */
    protected TableService tableService;

    public BaseSecurityScreen()
    {
        super();
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("site");
        cmsSecurityService = (SecurityService)broker.getService(SecurityService.SERVICE_NAME);
        tableService = (TableService)broker.
            getService(TableService.SERVICE_NAME);
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        SiteResource site = getSite();
        return coralSession.getUserSubject().hasRole(site.getAdministrator());
    }
}
