package net.cyklotron.cms.modules.views.site;

import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Role;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Secure;

import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.site.SiteService;

/**
 * The default void screen assember for forum application.
 */
public abstract class BaseSiteScreen
    extends BaseCMSScreen
    implements Secure
{
    /** logging facility */
    protected Logger log;

    /** structure service */
    protected SiteService siteService;

    public BaseSiteScreen()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("site");
        siteService = (SiteService)broker.getService(SiteService.SERVICE_NAME);
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        Role role = coralSession.getSecurity().getUniqueRole("cms.administrator");
        return coralSession.getUserSubject().hasRole(role);
    }
}
