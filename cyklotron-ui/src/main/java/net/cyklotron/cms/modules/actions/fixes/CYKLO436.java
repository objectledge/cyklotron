package net.cyklotron.cms.modules.actions.fixes;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id$
 */
public class CYKLO436 extends BaseCMSAction
{
    private final SiteService siteService;
    private final IntegrationService integrationService;

    public CYKLO436(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SiteService siteService, IntegrationService integrationService)
    {
        super(logger, structureService, cmsDataFactory);
        this.siteService = siteService;
        this.integrationService = integrationService;
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        SiteResource[] sites = siteService.getSites(coralSession);
        ApplicationResource[] apps = integrationService.getApplications(coralSession);
        for(SiteResource site : sites)
        {
            for(ApplicationResource app : apps)
            {
                integrationService.setApplicationEnabled(coralSession, site, app, true);
            }
        }
        templatingContext.put("result", "success");
    }
    
    /**
     * @{inheritDoc}
     */
    public boolean checkAccessRights(Context context)
        throws Exception
    {
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        return (coralSession.getUserSubject().getId() == Subject.ROOT);
    }
}
