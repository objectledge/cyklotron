package net.cyklotron.cms.modules.actions.link;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.link.PoolResource;
import net.cyklotron.cms.link.PoolResourceImpl;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdatePool.java,v 1.1 2005-01-24 04:34:56 pablo Exp $
 */
public class UpdatePool
    extends BaseLinkAction
{

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();

        String title = parameters.get("title","");
        String description = parameters.get("description","");
        if(title.length() < 1 || title.length() > 32)
        {
            templatingContext.put("result","invalid_title");
            return;
        }
        if(description.length() < 1 || description.length() > 255)
        {
            templatingContext.put("result","invalid_description");
            return;
        }

        int pid = parameters.getInt("pid", -1);
        if(pid == -1)
        {
            throw new ProcessingException("Poll id not found");
        }
        try
        {
            PoolResource poolResource = PoolResourceImpl.getPoolResource(coralSession, pid);
            if(!poolResource.getName().equals(title))
            {
                coralSession.getStore().setName(poolResource, title);
            }
            if(!poolResource.getDescription().equals(description))
            {
                poolResource.setDescription(description);
            }
            poolResource.update(subject);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("PollException: ",e);
            return;
        }
        templatingContext.put("result","updated_successfully");
    }
}


