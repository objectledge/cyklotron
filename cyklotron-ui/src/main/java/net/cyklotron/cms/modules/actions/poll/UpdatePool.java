package net.cyklotron.cms.modules.actions.poll;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.PoolResource;
import net.cyklotron.cms.poll.PoolResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdatePool.java,v 1.4 2005-03-08 10:53:05 pablo Exp $
 */
public class UpdatePool
    extends BasePollAction
{

    
    public UpdatePool(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, PollService pollService, WorkflowService workflowService)
    {
        super(logger, structureService, cmsDataFactory, pollService, workflowService);
        
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();

        int pid = parameters.getInt("pool_id", -1);
        if(pid == -1)
        {
            throw new ProcessingException("Pool id not found");
        }
        try
        {
            PoolResource poolResource = PoolResourceImpl.getPoolResource(coralSession, pid);
            String title = parameters.get("title","");
            String description = parameters.get("description","");
            if(!poolResource.getName().equals(title))
            {
                coralSession.getStore().setName(poolResource, title);
            }
            if(!poolResource.getDescription().equals(description))
            {
                poolResource.setDescription(description);
            }
            poolResource.update();
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("PollException: ",e);
            return;
        }
        templatingContext.put("result","updated_successfully");
    }
}


