package net.cyklotron.cms.modules.actions.link;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
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
import net.cyklotron.cms.link.LinkException;
import net.cyklotron.cms.link.LinkService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeleteLink.java,v 1.3 2005-01-25 07:15:09 pablo Exp $
 */
public class DeleteLink
    extends BaseLinkAction
{
    
    public DeleteLink(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, LinkService linkService, WorkflowService workflowService)
    {
        super(logger, structureService, cmsDataFactory, linkService, workflowService);
        // TODO Auto-generated constructor stub
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
            throw new ProcessingException("Links root id not found");
        }
        try
        {
            BaseLinkResource linkResource = BaseLinkResourceImpl.getBaseLinkResource(coralSession,lid);
            linkService.deleteLink(coralSession, linkResource);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("LinkException: ",e);
            return;
        }
        catch(LinkException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("LinkException: ",e);
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }
}
