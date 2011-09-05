package net.cyklotron.cms.modules.views.documents.ajax;

import java.io.IOException;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.structure.NavigationNodeResource;

import org.codehaus.jackson.JsonGenerationException;
import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.json.AbstractJsonView;

public class CommunityVotes
    extends AbstractJsonView
{
    private final CmsDataFactory cmsDataFactory;

    private final PollService pollService;

    public CommunityVotes(CmsDataFactory cmsDataFactory, Context context, Logger log,
        PollService pollService)
    {
        super(context, log);
        this.cmsDataFactory = cmsDataFactory;
        this.pollService = pollService;
    }

    @Override
    protected void buildJsonStream()
        throws ProcessingException, JsonGenerationException, IOException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        NavigationNodeResource node = cmsData.getContentNode();
        
        String vote = getRequestParameters().get("vote", null);
        if(vote != null && !pollService.hasVoted(getHttpContext(), node))
        {
            if(vote.equals("positive"))
            {
                node.setVotesPositive(node.isVotesPositiveDefined() ? node.getVotesPositive() + 1 : 1);
                node.update();
            }
            else if(vote.equals("negative"))
            {
                node.setVotesNegative(node.isVotesNegativeDefined() ? node.getVotesNegative() + 1 : 1);
                node.update();
            }
            pollService.trackVote(getHttpContext(), node);
        }

        if(node != null && node.canView(coralSession, coralSession.getUserSubject()))
        {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("positive",
                node.isVotesPositiveDefined() ? node.getVotesPositive() : 0);
            jsonGenerator.writeNumberField("negative",
                node.isVotesNegativeDefined() ? node.getVotesNegative() : 0);
            jsonGenerator.writeBooleanField("voted", pollService.hasVoted(getHttpContext(), node));
            jsonGenerator.writeEndObject();
        }
        else
        {
            jsonGenerator.writeNull();
        }
    }

    @Override
    protected String getCallbackParameterName()
    {
        return "callback";
    }
}
