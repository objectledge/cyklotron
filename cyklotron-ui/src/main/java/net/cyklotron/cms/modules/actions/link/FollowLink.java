package net.cyklotron.cms.modules.actions.link;

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
import net.cyklotron.cms.link.BaseLinkResource;
import net.cyklotron.cms.link.BaseLinkResourceImpl;
import net.cyklotron.cms.link.LinkService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: FollowLink.java,v 1.4 2005-03-08 10:52:41 pablo Exp $
 */
public class FollowLink
    extends BaseLinkAction
{
    
    
    public FollowLink(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, LinkService linkService, WorkflowService workflowService)
    {
        super(logger, structureService, cmsDataFactory, linkService, workflowService);
        
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        int lid = parameters.getInt("lid", -1);
        if(lid == -1)
        {
            throw new ProcessingException("Link id not found");
        }
        try
        {
            BaseLinkResource linkResource = BaseLinkResourceImpl.getBaseLinkResource(coralSession,lid);
            linkService.followLink(coralSession, linkResource);
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("ARLException: ",e);
            return;
        }
        templatingContext.put("result","followed_successfully");
    }
}
