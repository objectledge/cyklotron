package net.cyklotron.cms.modules.actions.link;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.link.BaseLinkResource;
import net.cyklotron.cms.link.BaseLinkResourceImpl;
import net.cyklotron.cms.link.LinkException;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeleteLink.java,v 1.1 2005-01-24 04:34:57 pablo Exp $
 */
public class DeleteLink
    extends BaseLinkAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        int lid = parameters.getInt("lid", -1);
        if(lid == -1)
        {
            throw new ProcessingException("Links root id not found");
        }
        try
        {
            BaseLinkResource linkResource = BaseLinkResourceImpl.getBaseLinkResource(coralSession,lid);
            linkService.deleteLink(linkResource);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("LinkException: ",e);
            return;
        }
        catch(LinkException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("LinkException: ",e);
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }
}
