package net.cyklotron.cms.modules.actions.httpfeed;

import net.labeo.services.resource.EntityInUseException;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.httpfeed.HttpFeedResource;

/**
 * Action for deleting http feeds from the site.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DeleteFeed.java,v 1.1 2005-01-24 04:35:09 pablo Exp $
 */
public class DeleteFeed extends BaseHttpFeedAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
   
        HttpFeedResource feed = getFeed(data);
        try
        {
            coralSession.getStore().deleteResource(feed);
            parameters.remove("feed_id");
        }
        catch(EntityInUseException e)
        {
            templatingContext.put("result","exception");
            log.error("EntityInUseException: ",e);
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.httpfeed.delete");
    }
}
