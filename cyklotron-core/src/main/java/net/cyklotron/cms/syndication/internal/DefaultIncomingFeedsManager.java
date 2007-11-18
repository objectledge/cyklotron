package net.cyklotron.cms.syndication.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.objectledge.ComponentInitializationError;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.UnsupportedCharactersInFilePathException;

import cpdetector.io.ASCIIDetector;
import cpdetector.io.CodepageDetectorProxy;
import cpdetector.io.JChardetFacade;
import cpdetector.io.ParsingDetector;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.syndication.CannotCreateFeedsRootException;
import net.cyklotron.cms.syndication.CannotCreateSyndicationRootException;
import net.cyklotron.cms.syndication.CannotReadTemplateException;
import net.cyklotron.cms.syndication.EmptyFeedNameException;
import net.cyklotron.cms.syndication.EmptyUrlException;
import net.cyklotron.cms.syndication.FeedAlreadyExistsException;
import net.cyklotron.cms.syndication.FeedCreationException;
import net.cyklotron.cms.syndication.IncomingFeedContentProcessor;
import net.cyklotron.cms.syndication.IncomingFeedResource;
import net.cyklotron.cms.syndication.IncomingFeedResourceImpl;
import net.cyklotron.cms.syndication.IncomingFeedsManager;
import net.cyklotron.cms.syndication.SyndicationException;
import net.cyklotron.cms.syndication.SyndicationService;
import net.cyklotron.cms.syndication.TooManyFeedsRootsException;
import net.cyklotron.cms.syndication.TooManySyndicationRootsException;
import net.cyklotron.cms.syndication.UnsupportedTemplateTypeException;
import net.cyklotron.cms.util.URI.MalformedURIException;

/**
 * Implementation of IncomingFeedsManager.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DefaultIncomingFeedsManager.java,v 1.2 2007-11-18 21:23:21 rafal Exp $
 */
public class DefaultIncomingFeedsManager
extends BaseFeedsManager
implements IncomingFeedsManager
{
    private static final String BASE_TEMPLATES_DIR = "incoming-feeds-templates";
    private static final String TEMPLATES_APP = "cms";
    private static final String TEMPLATES_DIR =
        "/templates/"+TEMPLATES_APP+"/"+BASE_TEMPLATES_DIR;
    
    public DefaultIncomingFeedsManager(SyndicationService syndicationService, FileSystem fileSystem)
    {
        super(syndicationService, fileSystem);
        if(!fileSystem.exists(TEMPLATES_DIR))
        {
            try
            {
                fileSystem.mkdirs(TEMPLATES_DIR);
            }
            catch(IOException e)
            {
                throw new ComponentInitializationError("cannot create templates dir", e);
            }
            catch(UnsupportedCharactersInFilePathException e)
            {
                throw new ComponentInitializationError(
                    "templates dir path has unsupported characters", e);
            }
        }
    }

    public IncomingFeedResource createFeed(CoralSession coralSession, String name, String url,
        int interval, String template, SiteResource site)
    throws EmptyFeedNameException, InvalidResourceNameException, FeedCreationException,
        FeedAlreadyExistsException, EmptyUrlException, MalformedURIException 
    {
        Resource parent = prepareCreateFeed(coralSession, name, site);

        checkUrl(url);
        
        template = fixTemplate(template);
        
        IncomingFeedResource feed = IncomingFeedResourceImpl.createIncomingFeedResource(
            coralSession, name, parent);
        
        feed.setUrl(url);
        feed.setInterval(interval);
        feed.setTransformationTemplate(template);
        feed.setFailedUpdates(-1);
        feed.update();
        
        return feed;
    }

    public void updateFeed(CoralSession coralSession, IncomingFeedResource feed, String name, String url,
        int interval, String template)
    throws EmptyFeedNameException,
        FeedAlreadyExistsException, EmptyUrlException, MalformedURIException, InvalidResourceNameException
    {
        prepareRenameFeedResource(coralSession, feed, name);
        checkUrl(url);

        template = fixTemplate(template);

        // rename
        if(!feed.getName().equals(name))
        {
            coralSession.getStore().setName(feed, name);
        }
        
        boolean update = false;
        if(feed.getUrl() == null || !feed.getUrl().equals(url))
        {
            feed.setUrl(url);
            feed.setFailedUpdates(-1);
            feed.setContents(null);
            update = true;
        }
        if(feed.getInterval() != interval)
        {
            feed.setInterval(interval);
            update = true;
        }
        if(feed.getTransformationTemplate() == null
            || !feed.getTransformationTemplate().equals(template))
        {
            feed.setTransformationTemplate(template);
            feed.setContents(null);
            update = true;
        }
        
        // and update
        if(update)
        {
            feed.update();
        }
    }

    public void deleteFeed(CoralSession coralSession, IncomingFeedResource feed)
    throws EntityInUseException
    {
        deleteFeedResource(coralSession, feed);
    }

    public IncomingFeedResource[] getFeeds(CoralSession coralSession, SiteResource site)
    throws SyndicationException
    {
        Resource parent = getFeedsParent(coralSession, site);
        Resource[] res = coralSession.getStore().getResource(parent);
        IncomingFeedResource[] feeds = new IncomingFeedResource[res.length];
        System.arraycopy(res, 0, feeds, 0, res.length);
        return feeds;
    }

    public synchronized Resource getFeedsParent(CoralSession coralSession, SiteResource site)
    throws TooManySyndicationRootsException, CannotCreateSyndicationRootException,
        TooManyFeedsRootsException, CannotCreateFeedsRootException
    {
        return getFeedsParent(site, INCOMING_FEEDS_ROOT, coralSession);
    }

    public void refreshFeed(CoralSession coralSession, IncomingFeedResource feed)
    throws Exception
    {
        String url = feed.getUrl();

        // 1. download the feed
        String contents = null;
        try
        {
            contents = downloadContent(url);
        }
        catch(MalformedURLException e)
        {
            refreshError(e, feed);
        }
        catch(HttpException e)
        {
            refreshError(e, feed);
        }
        catch(IOException e)
        {
            refreshError(e, feed);
        }

        // 2. check the type of the template
        // 3. if template is XSL
        // 3.1. try to transform by parsing XML, on success go to ....
        // 4. else if template velocity
        // 4.1. try to parse using feed parser
        // 4.2. generate by using velocity template
        // 2. check the type of the template
        IncomingFeedContentProcessor processor = null;
        try
        {
            processor = getFeedProcessor(feed, contents);
        }
        catch(UnsupportedTemplateTypeException e)
        {
            refreshError(e, feed);
        }
        catch(CannotReadTemplateException e)
        {
            refreshError(e, feed);
        } 

        // 5. save results
        String content = null;
        try
        {
            content = processor.process();
        }
        catch(Exception e)
        {
            refreshError(e, feed);
        }
        
        feed.setContents(content);
        
        feed.setLastUpdate(new Date());
        feed.setFailedUpdates(0);
        feed.setUpdateErrorKey(null);
        feed.update();
    }

    private IncomingFeedContentProcessor getFeedProcessor(IncomingFeedResource feed, String contents)
        throws UnsupportedTemplateTypeException, CannotReadTemplateException
    {
        String templateName = feed.getTransformationTemplate();
        if(templateName == null)
        {
            // pass as is
            return new PassThroughIncomingFeedContentProcessor(contents);
        }
        
        String templatePath = TEMPLATES_DIR+"/"+templateName;
        InputStream templateIS = fileSystem.getInputStream(templatePath);
        if(templateIS == null)
        {
            throw new CannotReadTemplateException(templateName);
        }
        
        /*if(templateName.endsWith(".vt"))
        {
            // velocity template
            return new VelocityIncomingFeedContentProcessor(contents, templatePath);
        }*/
        if(templateName.endsWith(".xsl"))
        {
            // velocity template
            return new XSLIncomingFeedContentProcessor(contents, templateIS);
        }
        throw new UnsupportedTemplateTypeException(templateName);
    }

    private void refreshError(Exception e, IncomingFeedResource feed)
    throws Exception
    {
        feed.setUpdateErrorKey(e.getClass().getSimpleName());
        int failedUpdates = feed.getFailedUpdates();
        failedUpdates = failedUpdates <= 0 ? 1 : failedUpdates + 1;
        feed.setFailedUpdates(failedUpdates);
        feed.update();
        
        throw e;
    }

    public String downloadContent(String url)
    throws MalformedURLException, HttpException, IOException
    {
        //create a singular HttpClient object
        HttpClient client = new HttpClient();

        //establish a connection within 10 seconds
        client.getHttpConnectionManager().getParams().setConnectionTimeout(10000);

        HttpMethodRetryHandler retryHandler = new HttpMethodRetryHandler() {
            public boolean retryMethod(
                final HttpMethod method, 
                final IOException exception, 
                int executionCount) {
                if (executionCount >= 5) {
                    // Do not retry if over max retry count
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {
                    // Retry if the server dropped connection on us
                    return true;
                }
                if (!method.isRequestSent()) {
                    // Retry if the request has not been sent fully or
                    // if it's OK to retry methods that have been sent
                    return true;
                }
                // otherwise do not retry
                return false;
            }
        };
        
        //create a method object
        HttpMethod method = new GetMethod(url);
        method.setFollowRedirects(true);
        //} catch (MalformedURLException murle) {
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, retryHandler);

        //execute the method
        String responseBody = null;
        try
        {
            client.executeMethod(method);
            
            CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
            detector.add(new ParsingDetector(false));
            detector.add(JChardetFacade.getInstance());
            detector.add(ASCIIDetector.getInstance());
            
            byte[] body = method.getResponseBody();
            InputStream is = new ByteArrayInputStream(body); 
            Charset charSet = detector.detectCodepage(is, body.length > 65536 ? 65536 : body.length);
            
            responseBody = new String(body, charSet.name());
        }
        finally
        {
            //clean up the connection resources
            method.releaseConnection();
        }

        return responseBody;
    }

    public List getTransformationTemplates() throws IOException
    {
        String[] fileTemplates = fileSystem.list(TEMPLATES_DIR);
        Arrays.sort(fileTemplates);
        List templates = new ArrayList(fileTemplates.length+1);
        templates.add(SyndicationService.NO_TEMPLATE_SELECTED_STRING);
        templates.addAll(Arrays.asList(fileTemplates));
        return templates;
    }
}
