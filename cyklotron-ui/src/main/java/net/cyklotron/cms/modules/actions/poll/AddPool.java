package net.cyklotron.cms.modules.actions.poll;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.poll.PollsResource;
import net.cyklotron.cms.poll.PollsResourceImpl;
import net.cyklotron.cms.poll.PoolResource;
import net.cyklotron.cms.poll.PoolResourceImpl;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: AddPool.java,v 1.2 2005-01-24 10:26:58 pablo Exp $
 */
public class AddPool
    extends BasePollAction
{

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();
        savePoll(data);

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
            PoolResource poolResource = PoolResourceImpl.createPoolResource(coralSession, title, pollsRoot, subject);
            poolResource.setDescription(description);
            poolResource.update(subject);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("PollException: ",e);
            return;
        }
        catch(ValueRequiredException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("PollException: ",e);
            return;
        }
        templatingContext.put("result","added_successfully");
    }
}


