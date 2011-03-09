package net.cyklotron.cms.modules.actions.poll;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.confirmation.EmailConfirmationRequestResource;
import net.cyklotron.cms.confirmation.EmailConfirmationRequestService;
import net.cyklotron.cms.poll.AnswerResource;
import net.cyklotron.cms.poll.AnswerResourceImpl;
import net.cyklotron.cms.poll.BallotResource;
import net.cyklotron.cms.poll.BallotResourceImpl;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.VoteResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;


public class CreateBallot
    extends BasePollAction
{

    private EmailConfirmationRequestService emailConfirmationRequestService;

    public CreateBallot(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, PollService pollService, WorkflowService workflowService,
        EmailConfirmationRequestService emailConfirmationRequestService)
    {
        super(logger, structureService, cmsDataFactory, pollService, workflowService);
        this.emailConfirmationRequestService = emailConfirmationRequestService;
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        Parameters screenConfig = cmsData.getEmbeddedScreenConfig();

        String cookie = parameters.get("cookie", "");
        if(cookie.length() == 0)
        {
            templatingContext.put("result", "parameter_not_found");
            return;
        }
        templatingContext.put("cookie", cookie);

        try
        {
            EmailConfirmationRequestResource req = emailConfirmationRequestService
                .getEmailConfirmationRequest(coralSession, cookie);
            if(req == null)
            {
                throw new ProcessingException("Vote not found");
            }
            else
            {
                VoteResource voteResource = pollService.getVote(coralSession, screenConfig);
                AnswerResource answerResource = AnswerResourceImpl.getAnswerResource(coralSession,
                    Long.parseLong(req.getData(), -1));
                if(voteResource != null && voteResource.equals(answerResource.getParent()))
                {
                    BallotResource ballotResource = BallotResourceImpl.createBallotResource(
                        coralSession, cookie, answerResource, req.getEmail());
                    ballotResource.setAnswerId(answerResource.getId());
                    ballotResource.update();
                }
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
            logger.error("Exception in poll,CreateBallot action", e);
            return;
        }

        templatingContext.put("result", "responded_successfully");
        templatingContext.put("already_voted", Boolean.TRUE);
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("poll"))
        {
            logger.debug("Application 'poll' not enabled in site");
            return false;
        }
        return true;
    }
}
