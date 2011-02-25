package net.cyklotron.cms.modules.actions.poll;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.poll.PollResource;
import net.cyklotron.cms.poll.PollResourceImpl;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.VoteResource;
import net.cyklotron.cms.poll.VoteResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeletePoll.java,v 1.4 2005-03-08 10:53:05 pablo Exp $
 */
public class DeleteVote
    extends BasePollAction
{

    
    public DeleteVote(Logger logger, StructureService structureService,
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
        int vid = parameters.getInt("vid", -1);
        if(vid == -1)
        {
            throw new ProcessingException("Vote id not found");
        }
        try
        {
            VoteResource vote = VoteResourceImpl.getVoteResource(coralSession, vid);
            Resource[] answers = coralSession.getStore().getResource(vote);
            for(int i = 0; i < answers.length; i++)
            {
                Resource[] ballots = coralSession.getStore().getResource(answers[i]);
                for(int j = 0; j < ballots.length; j++)
                {
                    coralSession.getStore().deleteResource(ballots[j]);
                }
                coralSession.getStore().deleteResource(answers[i]);
            }
            coralSession.getStore().deleteResource(vote);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("VoteException: ",e);
            return;
        }
        catch(EntityInUseException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("VoteException: ",e);
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }
}


