package net.cyklotron.cms.modules.actions.security;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.structure.StructureService;

public abstract class BaseSecurityAction
    extends BaseCMSAction
{
    /** security service */
    protected SecurityService cmsSecurityService;

    protected UserManager userManager;
    
    public BaseSecurityAction(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SecurityService cmsSecurityService,
        UserManager userManager)
    {
        super(logger, structureService, cmsDataFactory);
        this.cmsSecurityService = cmsSecurityService;
        this.userManager = userManager;
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return checkAdministrator(context);
        /**
        SiteResource site = getSite(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return coralSession.getUserSubject().hasRole(site.getAdministrator());
        */
    }
}
