package net.cyklotron.cms.modules.actions.security;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureService;

public abstract class BaseSecurityAction
    extends BaseCMSAction
{
    /** security service */
    protected SecurityService cmsSecurityService;

    
    public BaseSecurityAction(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SecurityService cmsSecurityService)
    {
        super(logger, structureService, cmsDataFactory);
        this.cmsSecurityService = cmsSecurityService;
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        SiteResource site = getSite(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return coralSession.getUserSubject().hasRole(site.getAdministrator());
    }
}
