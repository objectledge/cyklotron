package net.cyklotron.cms.modules.actions.link;

import java.util.List;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.link.BaseLinkResource;
import net.cyklotron.cms.link.BaseLinkResourceImpl;
import net.cyklotron.cms.link.PoolResource;
import net.cyklotron.cms.link.PoolResourceImpl;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeleteFromPool.java,v 1.2 2005-01-24 10:27:01 pablo Exp $
 */
public class DeleteFromPool
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

        int lid = parameters.getInt("lid", -1);
        int pid = parameters.getInt("pid", -1);
        if(lid == -1 || pid == -1)
        {
            throw new ProcessingException("pool id nor link id not found");
        }

        try
        {
            PoolResource poolResource = PoolResourceImpl.getPoolResource(coralSession, pid);
            BaseLinkResource linkResource = BaseLinkResourceImpl.getBaseLinkResource(coralSession, lid);
            List links = poolResource.getLinks();
            links.remove(linkResource);
            poolResource.setLinks(links);
            poolResource.update(subject);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("LinkException: ",e);
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }
}


