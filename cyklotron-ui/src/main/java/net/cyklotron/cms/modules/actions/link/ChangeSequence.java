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
 * @version $Id: ChangeSequence.java,v 1.2 2005-01-24 10:27:01 pablo Exp $
 */
public class ChangeSequence
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
        int offset = parameters.getInt("offset", 0);
        if(lid == -1 || pid == -1 || offset == 0)
        {
            throw new ProcessingException("pool id nor link id nor offset not found");
        }

        try
        {
            PoolResource poolResource = PoolResourceImpl.getPoolResource(coralSession, pid);
            BaseLinkResource linkResource = BaseLinkResourceImpl.getBaseLinkResource(coralSession, lid);
            List links = poolResource.getLinks();
            int position = links.indexOf(linkResource);
            if(position+offset < 0 || position+offset >= links.size())
            {
                templatingContext.put("result","illegal_sequence");
                return;
            }
            links.remove(position);
            links.add(position+offset,linkResource);
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
        templatingContext.put("result","changed_successfully");
    }
}


