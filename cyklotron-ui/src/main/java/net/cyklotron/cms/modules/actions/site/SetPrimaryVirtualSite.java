package net.cyklotron.cms.modules.actions.site;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 */
public class SetPrimaryVirtualSite
    extends BaseSiteAction
{
    public SetPrimaryVirtualSite(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SiteService siteService)
    {
        super(logger, structureService, cmsDataFactory, siteService);
        
    }
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        String domain = parameters.get("domain");

        try
        {
            SiteResource site = ss.getSiteByAlias(coralSession, domain);
            ss.setPrimaryMapping(coralSession, site, domain);
        }
        catch(Exception e)
        {
            if(e instanceof ProcessingException)
            {
                throw (ProcessingException)e;
            }
            throw new ProcessingException("failed to lookup site information",e);
        }
    }
}
