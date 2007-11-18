package net.cyklotron.cms.modules.views.syndication;

import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Template;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.builders.BuildException;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.syndication.OutgoingFeedResource;
import net.cyklotron.cms.syndication.SyndicationService;

/**
 * View an outgoing feed.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: OutgoingFeedView.java,v 1.4 2007-11-18 21:24:50 rafal Exp $
 */
public class OutgoingFeedView extends BaseSyndicationScreen
{
    public OutgoingFeedView(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        SyndicationService syndicationService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        syndicationService);
    }

    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession) throws ProcessingException
    {
        throw new UnsupportedOperationException("Implemented the calling method 'build'");
    }

    
    @Override
    public String build(Template template, String embeddedBuildResults) 
    throws BuildException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        HttpContext httpContext = HttpContext.getHttpContext(context);

        OutgoingFeedResource feed;
        try
        {
            feed = getOutgoingFeed(coralSession, parameters);
        }
        catch(ProcessingException e)
        {
            throw new BuildException("Cannot get the feed", e);
        }
        boolean makeRefresh = (feed.getLastUpdate() == null) || (feed.getContents() == null);
        if(!makeRefresh)
        {
            Calendar cal = Calendar.getInstance();
            Date now = cal.getTime();
            // calculate next update time
            cal.setTime(feed.getLastUpdate());
            cal.add(Calendar.MINUTE, feed.getInterval());
            Date nextUpdate = cal.getTime();
            
            makeRefresh = nextUpdate.before(now);
        }
        if(makeRefresh)
        {
            synchronized(feed)
            {
                try
                {
                    syndicationService.getOutgoingFeedsManager().refreshFeed(context, coralSession, feed);
                }
                catch(Exception e)
                {
                    throw new BuildException("Could not refresh feed '" +feed+ "'", e);
                }
            }
        }
        
        if(feed.getContents() != null && feed.getLastUpdate() != null)
        {
            httpContext.setContentType("text/xml");
            httpContext.getResponse().addDateHeader("Last-Modified",feed.getLastUpdate().getTime());
            try
            {
                httpContext.setEncoding("UTF-8");
                Writer writer = httpContext.getPrintWriter();
                writer.write(feed.getContents());
                writer.flush();
                writer.close();
            }
            catch(IOException e)
            {
                throw new BuildException("Could not get the output stream", e);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiresAuthenticatedUser(Context context) throws Exception
    {
        return false;
    }

    public boolean checkAccessRights(Context context)
    throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("syndication"))
        {
            logger.debug("Application 'syndication' not enabled in site");
            return false;
        }
        CoralSession coralSession = getCoralSession(context); 
        Subject subject = coralSession.getUserSubject();
        Parameters parameters = RequestParameters.getRequestParameters(context);
        OutgoingFeedResource feed = getOutgoingFeed(coralSession, parameters);
        SiteResource site = cmsData.getSite();
        return feed.getPublic() || subject.hasRole(site.getTeamMember());
    }
}
