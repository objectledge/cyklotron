package net.cyklotron.cms.modules.actions.httpfeed;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.util.URI;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.httpfeed.HttpFeedResource;
import net.cyklotron.cms.httpfeed.HttpFeedResourceImpl;

/**
 * Action for adding http feeds to the site.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: AddFeed.java,v 1.1 2005-01-24 04:35:09 pablo Exp $
 */
public class AddFeed extends BaseHttpFeedAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();

        FeedParams params = new FeedParams(data);
        if(!check(params, data, context))
        {
            return;
        }

        try
        {
            HttpFeedResource feed = getFeedResource(data, params, subject);

            feed.setUrl(params.url);
            feed.setInterval(params.interval);
            feed.setDescription(params.description);

            feed.update(subject);
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",StringUtils.stackTrace(e));
            log.error("problem adding feed", e);
            return;
        }

        templatingContext.put("result", successResult());
    }

    public class FeedParams
    {
        String name;
        String url;
        int interval;
        String description;

        public FeedParams(RunData data)
        {
            name = parameters.get("name","");
            url = parameters.get("url","");
            interval = parameters.getInt("interval", 60);
            description = parameters.get("description","");
        }
    }

    protected boolean check(FeedParams params, RunData data, Context context)
    throws ProcessingException
    {
        if(params.name.equals(""))
        {
            templatingContext.put("result","feed_name_empty");
            return false;
        }
        if(params.url.equals(""))
        {
            templatingContext.put("result","feed_url_empty");
            return false;
        }
        try
        {
            new URI(params.url);
        }
        catch(URI.MalformedURIException e)
        {
            templatingContext.put("result","feed_url_bad");
            return false;
        }
        return true;
    }

    protected HttpFeedResource getFeedResource(RunData data, FeedParams params, Subject subject)
    throws Exception
    {
        Resource parent = httpFeedService.getFeedsParent(getSite(context));
        HttpFeedResource feed = HttpFeedResourceImpl.createHttpFeedResource(coralSession,
                            params.name, parent, subject);
        feed.setFailedUpdates(0);

        return feed;
    }

    protected String successResult()
    {
        return "added_successfully";
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.httpfeed.add");
    }
}
