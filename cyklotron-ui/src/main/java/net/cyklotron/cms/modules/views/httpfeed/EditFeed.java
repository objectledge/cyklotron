package net.cyklotron.cms.modules.views.httpfeed;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.httpfeed.HttpFeedResource;

/**
 * A list of feeds defined fo the site.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: EditFeed.java,v 1.3 2005-01-25 11:23:49 pablo Exp $
 */
public class EditFeed extends BaseHttpFeedScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
    throws ProcessingException
    {
        if(parameters.isDefined("feed_id"))
        {
            HttpFeedResource feed = getFeed(data);
            templatingContext.put("feed", feed);
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.httpfeed.modify");
    }
}
