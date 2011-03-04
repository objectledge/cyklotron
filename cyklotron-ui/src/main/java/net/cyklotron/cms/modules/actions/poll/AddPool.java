package net.cyklotron.cms.modules.actions.poll;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.PollsResource;
import net.cyklotron.cms.poll.PollsResourceImpl;
import net.cyklotron.cms.poll.PoolResource;
import net.cyklotron.cms.poll.PoolResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: AddPool.java,v 1.5 2005-06-13 11:08:36 rafal Exp $
 */
public class AddPool
    extends BasePollAction
{

    public AddPool(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory,
        PollService pollService, WorkflowService workflowService)
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
        savePoll(httpContext, parameters);

        int psid = parameters.getInt("psid", -1);
        if(psid == -1)
        {
            throw new ProcessingException("Polls root id not found");
        }

        try
        {
            PollsResource pollsRoot = PollsResourceImpl.getPollsResource(coralSession, psid);
            String title = parameters.get("title","");
            if(title.length() == 0)
            {
                templatingContext.put("result", "invalid_title");
                return;
            }
            String description = parameters.get("description","");
            PoolResource poolResource = PoolResourceImpl.createPoolResource(coralSession, title,
                pollsRoot);
            poolResource.setDescription(description);
            poolResource.update();
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("PollException: ",e);
            return;
        }
        catch(InvalidResourceNameException e)
        {
            templatingContext.put("result", "invalid_name");
            return;
        }
        templatingContext.put("result","added_successfully");
    }
}


