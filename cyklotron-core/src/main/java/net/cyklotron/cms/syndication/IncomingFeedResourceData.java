package net.cyklotron.cms.syndication;

import org.objectledge.parameters.Parameters;
import org.objectledge.web.HttpContext;


/**
 * Provides default values and state keeping for resource editing.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: IncomingFeedResourceData.java,v 1.1 2005-06-16 11:14:22 zwierzem Exp $
 */
public class IncomingFeedResourceData
{
    public static IncomingFeedResourceData getData(HttpContext httpContext, IncomingFeedResource feed)
    {
        String key = getDataKey(feed);
        IncomingFeedResourceData currentData = (IncomingFeedResourceData)
            httpContext.getSessionAttribute(key);
        if(currentData == null)
        {
            currentData = new IncomingFeedResourceData(feed);
            httpContext.setSessionAttribute(key, currentData);
        }
        return currentData;
    }

    public static void removeData(HttpContext httpContext, IncomingFeedResource feed)
    {
        httpContext.removeSessionAttribute(getDataKey(feed));
    }

    private static String getDataKey(IncomingFeedResource feed)
    {
        if(feed != null)
        {
            return "cms.syndication.incoming.feed.data."+feed.getIdString();
        }
        else
        {
            return "cms.syndication.incoming.feed.data.NEW";
        }
    }
    
    private String name;
    private String description;
    private int interval;
    private String template;
    private String url;

    /** <code>true</code> if the config object was created during current request. */
    private boolean newData;

    public IncomingFeedResourceData(IncomingFeedResource feed)
    {
        if(feed != null)
        {
            name = feed.getName();
            description = feed.getDescription();
            interval = feed.getInterval();
            template = feed.getTransformationTemplate();
            url = feed.getUrl();
        }
        //
        newData = true;
    } 

    public boolean isNew()
    {
        return newData;
    }

    public void update(Parameters params)
    {
        name = params.get("name", null);
        description = params.get("description", null);
        interval = params.getInt("interval", -1);
        template = params.get("template", null);
        url = params.get("url", null);

        // data was modified
        newData = false;
    }

    // getters /////////////////////////////////////////////////////////////////////////////////////

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public int getInterval()
    {
        return interval;
    }

    public String getTemplate()
    {
        return template;
    }

    public String getUrl()
    {
        return url;
    }
}
