package net.cyklotron.cms.httpfeed.internal;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import net.cyklotron.cms.httpfeed.HttpFeedException;
import net.cyklotron.cms.httpfeed.HttpFeedResource;
import net.cyklotron.cms.httpfeed.HttpFeedService;
import net.cyklotron.cms.site.SiteResource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;


/**
 * Implementation of HttpFeed Service.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HttpFeedServiceImpl.java,v 1.6 2005-06-06 16:43:41 zwierzem Exp $
 */
public class HttpFeedServiceImpl
    implements HttpFeedService
{
    /** logging facility */
    private Logger log;

    /** number of attempts for getting the component contents via http. */
    private int downloadAttempts = 3;
    
    /**
     * Initializes the service.
     */
    public HttpFeedServiceImpl(Configuration config, Logger logger)
    {
        log = logger;
        downloadAttempts = config.getChild("download.attempts").getValueAsInteger(downloadAttempts);
    }

    /** Returns a parent resource for feeds defined for the site
     *
     * @param site the site resource for which feeds are defined.
     *
     */
    public Resource getFeedsParent(CoralSession coralSession, SiteResource site)
    throws HttpFeedException
    {
        Resource[] res = coralSession.getStore().getResource(site, SITE_FEEDS);
        if(res.length > 1)
        {
            throw new HttpFeedException("more than one httpfeed root found for site '"+
                                        site.getName()+"' with id "+site.getIdString());
        }
        else if(res.length == 0)
        {
            throw new HttpFeedException("cannot find httpfeed root for site '"+
                                        site.getName()+"' with id "+site.getIdString());
        }
        return res[0];
    }

    /** Returns all feeds defined for the site
     *
     * @param site the site resource for which feeds are defined.
     *
     */
    public HttpFeedResource[] getFeeds(CoralSession coralSession, SiteResource site)
    throws HttpFeedException
    {
        Resource parent = getFeedsParent(coralSession, site);
        Resource[] res = coralSession.getStore().getResource(parent);
        HttpFeedResource[] feeds = new HttpFeedResource[res.length];
        feeds = (HttpFeedResource[])(Arrays.asList(res).toArray(feeds));
        return feeds;
    }

    /** Refreshes a feed content.
     *
     * @param feed the feed being updated.
     * @param content new feed content
     * @param subject the subject performing refresh action.
     */
    public void refreshFeed(HttpFeedResource feed, String content, Subject subject)
    {
        if(content != null)
        {
            // update the http feed content
            feed.setContents(content);
            // the update was successfull
            feed.setFailedUpdates(0);
        }
        else
        {
            // increase the number of failed updates
            feed.setFailedUpdates(1+feed.getFailedUpdates());
        }
        
        // update last update date
        feed.setLastUpdate(new Date());
        
        // save feed changes
        feed.update();
    }

    /**
     * Retrieves some content via HTTP.
     *
     * @param url URL which points to the content
     * @return body of a content or <code>null</code>
     */
    public String getContent(String url)
    {
        // 1.  Create an instance of HttpClient .
        // 2. Create an instance of one of the methods (GetMethod in this case).
        //    The URL to connect to is passed in to the the method constructor.
        // 3. Tell HttpClient to execute the message.
        // 4. Read the response.
        // 5. Release the connection.
        // 6. Deal with the response.
        
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(url);
        
        int statusCode = -1;
        int attempt = 0;
        // We will retry up to configured number of times
        while(statusCode == -1 && attempt < downloadAttempts)
        {
            try
            {
                // increase number of attempts
                attempt++;
                statusCode = client.executeMethod(method);
            }
            catch(IOException e)
            {
                log.error("failed to get the content for url="+url, e);
            }
            catch(Exception e)
            {
                log.error("failed to get the content for url="+url
                          +" something bad hapened", e);
            }
        }
        
        String responseBody = null;
        // check if the data was properly retrieved
        if(statusCode == 200)
        {
            // get the response body with encoding specified in the response headers
            try
            {
                responseBody = method.getResponseBodyAsString();
            }
            catch(IOException e)
            {
                log.error("problem fetching response body", e);
            }
        }
        
        // release the connection.
        method.releaseConnection();
        
        return responseBody;
    }
}
