package net.cyklotron.cms.modules.views.poll.ajax;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.json.AbstractJsonView;

import net.cyklotron.cms.poll.PollResource;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.util.Answer;
import net.cyklotron.cms.poll.util.Question;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * An AJAX/JSON view that provides poll results and requesting user's vote tracking status.
 * 
 * @author rafal.krzewski@objectledge.org
 */
public class Poll
    extends AbstractJsonView
{
    private final PollService pollService;

    public Poll(Context context, Logger log, PollService pollService)
    {
        super(context, log);
        this.pollService = pollService;
    }

    @Override
    protected void buildJsonStream(JsonGenerator jsonGenerator)
        throws ProcessingException, JsonGenerationException, IOException
    {
        Parameters parameters = context.getAttribute(RequestParameters.class);
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        long pollId;
        try
        {
            pollId = parameters.getLong("pid", -1);
        }
        catch(NumberFormatException e)
        {
            pollId = -1l;
        }
        if(pollId != -1)
        {
            PollResource poll = null;
            try
            {
                poll = (PollResource)coralSession.getStore().getResource(pollId);
            }
            catch(EntityDoesNotExistException e)
            {
                log.error("invalid pid " + pollId, e);
            }
            catch(ClassCastException e)
            {
                log.error("invalid pid " + pollId, e);
            }
            if(poll != null)
            {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeBooleanField("voted",
                    pollService.hasVoted(getHttpContext(), poll));

                Map<Integer, Question> questions = new HashMap<Integer, Question>();
                Map<Long, Integer> resultMap = new HashMap<Long, Integer>();
                Map<Long, Float> percentMap = new HashMap<Long, Float>();
                pollService.prepareMaps(coralSession, poll, questions, resultMap, percentMap);
                jsonGenerator.writeObjectFieldStart("results");
                for(Question question : questions.values())
                {
                    jsonGenerator.writeObjectFieldStart(Long.toString(question.getId()));
                    for(Answer answer : ((Collection<Answer>)question.getAnswers().values()))
                    {
                        jsonGenerator.writeNumberField(Long.toString(answer.getId()),
                            resultMap.get(answer.getId()));
                    }
                    jsonGenerator.writeEndObject();
                }
                jsonGenerator.writeEndObject();

                jsonGenerator.writeEndObject();
                return;
            }
        }
        else
        {
            log.error("missing request parameter pid");
        }
        jsonGenerator.writeNull();
    }

    @Override
    protected String getCallbackParameterName()
    {
        return "callback";
    }
}
