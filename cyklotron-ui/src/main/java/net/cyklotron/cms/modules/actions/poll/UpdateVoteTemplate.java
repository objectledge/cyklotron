package net.cyklotron.cms.modules.actions.poll;

import java.io.StringReader;
import java.io.StringWriter;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.MergingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.VoteResource;
import net.cyklotron.cms.poll.VoteResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.workflow.WorkflowService;

/**
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdatePoll.java,v 1.8 2005-10-10 13:46:00 rafal Exp $
 */
public class UpdateVoteTemplate 
    extends BasePollAction
{
    protected Templating templating;
    
    private final PollService pollService;

    public UpdateVoteTemplate(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, PollService pollService, WorkflowService workflowService, Templating templating)
    {
        super(logger, structureService, cmsDataFactory, pollService, workflowService);
        this.pollService =  pollService;
        this.templating = templating;
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        int vid = parameters.getInt("vid", -1);

        if(vid == -1)
        {
            throw new ProcessingException("Vote id not found");
        }
        
        String contents = parameters.get("contents");
        try
        {
            VoteResource vote = VoteResourceImpl.getVoteResource(coralSession, vid);
            pollService.setVoteConfiramationTicketContents(vote, contents);
            templatingContext.put("vid", vid);
            
            TemplatingContext blankContext = templating.createContext();
            StringReader in = new StringReader(contents);
            StringWriter out = new StringWriter();
            try
            {
                templating.merge(blankContext, in, out, "<component template>");
            }
            catch(MergingException e)
            {
                templatingContext.put("result", "template_saved_parse_error");
                templatingContext.put("parse_trace", new StackTrace(e));                
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
        if(templatingContext.containsKey("result"))
        {
            mvcContext.setView("periodicals.EditTemplate");
        }
        else
        {
            templatingContext.put("result","updated_successfully");
        }
    }
}