package net.cyklotron.cms.modules.actions.link;

import java.util.ArrayList;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.link.LinkRootResource;
import net.cyklotron.cms.link.LinkRootResourceImpl;
import net.cyklotron.cms.link.PoolResource;
import net.cyklotron.cms.link.PoolResourceImpl;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: AddPool.java,v 1.1 2005-01-24 04:34:56 pablo Exp $
 */
public class AddPool
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
        if(description.length() > 256)
        {
            templatingContext.put("result","invalid_description");
            return;
        }


        int lsid = parameters.getInt("lsid", -1);
        if(lsid == -1)
        {
            throw new ProcessingException("Links root id not found");
        }

        try
        {
            LinkRootResource linksRoot = LinkRootResourceImpl.getLinkRootResource(coralSession, lsid);
            PoolResource poolResource = PoolResourceImpl.createPoolResource(coralSession, title, linksRoot, subject);
            poolResource.setDescription(description);
            poolResource.setLinks(new ArrayList());
            poolResource.update(subject);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("LinkException: ",e);
            return;
        }
        catch(ValueRequiredException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("LinkException: ",e);
            return;
        }
        templatingContext.put("result","added_successfully");
    }
}


