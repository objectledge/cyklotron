package net.cyklotron.cms.modules.actions.httpfeed;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.httpfeed.HttpFeedException;
import net.cyklotron.cms.httpfeed.HttpFeedResource;

/**
 * Action for updating http feeds in the site.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdateFeed.java,v 1.2 2005-01-24 10:27:54 pablo Exp $
 */
public class UpdateFeed extends AddFeed
{
    protected boolean check(FeedParams params, RunData data, Context context)
    throws ProcessingException
    {
        boolean result = super.check(params, data, context);
        if(!result)
        {
            return false;
        }
        
        HttpFeedResource currentFeed = getFeed(data);
        if(currentFeed.getName().equals(params.name))
        {
            return true;
        }

        Resource[] res;
        try
        {
            Resource parent = httpFeedService.getFeedsParent(getSite(context));
            res = coralSession.getStore().getResource(parent, params.name);
        }
        catch(HttpFeedException e)
        {
            throw new ProcessingException("Problems getting http feed parent for site", e);
        }
            
        if(res.length == 0)
        {
            return true;
        }
        else if(res.length == 1)
        {
            templatingContext.put("result","feed_with_same_name_exists");
            return false;
        }
        else
        {
            templatingContext.put("result","many_feeds_with_same_name");
            return false;
        }
    }
    
    protected HttpFeedResource getFeedResource(RunData data, FeedParams params, Subject subject)
    throws Exception
    {
        HttpFeedResource feed = getFeed(data);
        coralSession.getStore().setName(feed, params.name);
        return feed;
    }

    protected String successResult()
    {
        return "updated_successfully";
    }
    
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.httpfeed.modify");
    }
}
