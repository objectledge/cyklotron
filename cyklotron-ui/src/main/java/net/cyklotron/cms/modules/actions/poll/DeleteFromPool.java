package net.cyklotron.cms.modules.actions.poll;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.generic.CrossReference;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.poll.PollResource;
import net.cyklotron.cms.poll.PollResourceImpl;
import net.cyklotron.cms.poll.PollsResource;
import net.cyklotron.cms.poll.PoolResource;
import net.cyklotron.cms.poll.PoolResourceImpl;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeleteFromPool.java,v 1.1 2005-01-24 04:34:08 pablo Exp $
 */
public class DeleteFromPool
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

        int poolId = parameters.getInt("pool_id", -1);
        int pid = parameters.getInt("pid", -1);
        if(poolId == -1 || pid == -1)
        {
            throw new ProcessingException("pool id nor poll id not found");
        }

        try
        {
            PoolResource poolResource = PoolResourceImpl.getPoolResource(coralSession, poolId);
            PollResource pollResource = PollResourceImpl.getPollResource(coralSession, pid);
            PollsResource pollsRoot = (PollsResource)poolResource.getParent();
            CrossReference refs = pollsRoot.getBindings();
            refs.remove(poolResource, pollResource);
            pollsRoot.setBindings(refs);
            pollsRoot.update(subject);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("LinkException: ",e);
            return;
        }
        templatingContext.put("result","changed_successfully");
    }
}


