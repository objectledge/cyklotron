package net.cyklotron.cms.modules.actions.site;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteResourceImpl;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeleteSite.java,v 1.5 2005-05-31 17:20:51 pablo Exp $
 */
public class DeleteSite
    extends BaseSiteAction
{
    public DeleteSite(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SiteService siteService)
    {
        super(logger, structureService, cmsDataFactory, siteService);
        
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        
        try
        {
            long siteId = parameters.getLong("delete_site_id");
            SiteResource site = SiteResourceImpl.getSiteResource(coralSession, siteId);
            ss.destroySite(coralSession, site);
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            logger.error("DeleteSite: ",e);
            templatingContext.put("trace", new StackTrace(e));
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }
}
