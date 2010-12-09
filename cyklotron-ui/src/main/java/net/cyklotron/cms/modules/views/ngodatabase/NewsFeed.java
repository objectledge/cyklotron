package net.cyklotron.cms.modules.views.ngodatabase;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
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
import org.objectledge.web.mvc.builders.AbstractBuilder;
import org.objectledge.web.mvc.builders.BuildException;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.ngodatabase.NgoDatabaseService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.syndication.SyndicationService;
import net.cyklotron.cms.util.OfflineLinkRenderingService;

/**
 * View organization's news outgoing feed.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Download.java,v 1.6 2006-01-02 11:42:17 rafal Exp $
 */

public class NewsFeed
    extends AbstractBuilder
{
    private NgoDatabaseService ngoDatabaseService;

    public NewsFeed(Context context, NgoDatabaseService ngoDatabaseService)
    {
        super(context);
        this.ngoDatabaseService = ngoDatabaseService;
    }

    @Override
    public String build(Template template, String embeddedBuildResults)
        throws BuildException
    {
        try
        {           
            HttpContext httpContext = context.getAttribute(HttpContext.class);
            RequestParameters parameters = context.getAttribute(RequestParameters.class);
            
            httpContext.setContentType("text/xml");
            httpContext.getResponse().addDateHeader("Last-Modified", (new Date()).getTime());
            httpContext.setEncoding("UTF-8");
            Writer writer = httpContext.getPrintWriter();
            writer.write(ngoDatabaseService.getOrganizationNewsFeed(parameters));
            writer.close();
        }
        catch(Exception e)
        {
            throw new BuildException("news feed processing failed", e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean requiresAuthenticatedUser(Context context)
        throws Exception
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean requiresSecureChannel(Context context)
        throws Exception
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return true;
    }
}
