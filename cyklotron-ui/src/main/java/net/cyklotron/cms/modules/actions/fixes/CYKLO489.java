package net.cyklotron.cms.modules.actions.fixes;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.CmsNodeResourceImpl;
import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.SkinResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

/**
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id$
 */
public class CYKLO489 extends BaseCMSAction
{
    private final SiteService siteService;
    private final SkinService skinService;

    public CYKLO489(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SiteService siteService, SkinService skinService)
    {
        super(logger, structureService, cmsDataFactory);
        this.siteService = siteService;
        this.skinService = skinService;
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource[] sites = siteService.getSites(coralSession);
            for(SiteResource site : sites)
            {
                SkinResource[] skins = skinService.getSkins(coralSession, site);
                for(SkinResource skin : skins)
                {
                    if(coralSession.getStore().getResource(skin, "system_screens").length == 0)
                    {
                        CmsNodeResourceImpl.createCmsNodeResource(coralSession, "system_screens", skin);
                    }
                }
            }
            sites = siteService.getTemplates(coralSession);
            for(SiteResource site : sites)
            {
                SkinResource[] skins = skinService.getSkins(coralSession, site);
                for(SkinResource skin : skins)
                {
                    if(coralSession.getStore().getResource(skin, "system_screens").length == 0)
                    {
                        CmsNodeResourceImpl.createCmsNodeResource(coralSession, "system_screens", skin);
                    }
                }
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("Invalid name exception", e);
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
