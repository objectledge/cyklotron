package net.cyklotron.cms.modules.actions.link;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.link.LinkService;
import net.cyklotron.cms.link.PoolResource;
import net.cyklotron.cms.link.PoolResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeletePool.java,v 1.4 2005-03-08 10:52:41 pablo Exp $
 */
public class DeletePool
    extends BaseLinkAction
{

    public DeletePool(Logger logger, StructureService structureService,
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
        int pid = parameters.getInt("pid", -1);
        if(pid == -1)
        {
            throw new ProcessingException("Pool id not found");
        }
        try
        {
            PoolResource poolResource = PoolResourceImpl.getPoolResource(coralSession,pid);
            coralSession.getStore().deleteResource(poolResource);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("LinkException: ",e);
            return;
        }
        catch(EntityInUseException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("LinkException: ",e);
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }
}


