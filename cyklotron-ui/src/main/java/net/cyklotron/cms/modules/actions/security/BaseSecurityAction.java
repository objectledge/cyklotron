package net.cyklotron.cms.modules.actions.security;

import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;

public abstract class BaseSecurityAction
    extends BaseCMSAction
{
    /** security service */
    protected SecurityService cmsSecurityService;

    public BaseSecurityAction()
    {
        super();
        cmsSecurityService = (SecurityService)broker.getService(SecurityService.SERVICE_NAME);
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        SiteResource site = getSite(context);
        return coralSession.getUserSubject().hasRole(site.getAdministrator());
    }
}
