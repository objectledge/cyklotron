package net.cyklotron.cms.modules.actions.poll;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.EntityInUseException;
import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.poll.PollResource;
import net.cyklotron.cms.poll.PollResourceImpl;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeletePoll.java,v 1.2 2005-01-24 10:26:58 pablo Exp $
 */
public class DeletePoll
    extends BasePollAction
{

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();

        int pid = parameters.getInt("pid", -1);
        if(pid == -1)
        {
            throw new ProcessingException("Polls root id not found");
        }
        try
        {
            PollResource pollResource = PollResourceImpl.getPollResource(coralSession,pid);
            Resource[] questions = coralSession.getStore().getResource(pollResource);
            for(int j = 0; j < questions.length; j++)
            {
                Resource[] answers = coralSession.getStore().getResource(questions[j]);
                for(int k = 0; k < answers.length; k++)
                {
                    coralSession.getStore().deleteResource(answers[k]);
                }
                coralSession.getStore().deleteResource(questions[j]);
            }
            coralSession.getStore().deleteResource(pollResource);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("PollException: ",e);
            return;
        }
        catch(EntityInUseException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("PollException: ",e);
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }
}


