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
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;
/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeleteVirtualSite.java,v 1.3 2005-01-25 07:48:02 pablo Exp $
 */
public class DeleteVirtualSite
    extends BaseSiteAction
{
    public DeleteVirtualSite(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SiteService siteService)
    {
        super(logger, structureService, cmsDataFactory, siteService);
        // TODO Auto-generated constructor stub
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        
        String domain = parameters.get("domain","");
        if(domain.equals(""))
        {
            templatingContext.put("result","domain_name_empty");
        }
        try
        {
            ss.removeMapping(coralSession, domain);
        }
        catch(SiteException e)
        {
            templatingContext.put("result","exception");
            logger.error("DeleteDomain",e);
            templatingContext.put("trace", new StackTrace(e));
        }
        if(templatingContext.containsKey("result"))
        {
            mvcContext.setView("site,EditVirtualSite");
            String defaultNodePath = parameters.get("default_node_path","");
            if(defaultNodePath.length() > 0)
            {
                templatingContext.put("default_node_path", defaultNodePath);
            }
        }
        else
        {
            templatingContext.put("result","deleted_successfully");
        }
    }
}
