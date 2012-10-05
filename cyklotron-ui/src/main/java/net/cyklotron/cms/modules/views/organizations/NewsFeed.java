package net.cyklotron.cms.modules.views.organizations;

import java.io.Writer;
import java.util.Date;

import org.objectledge.context.Context;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Template;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.builders.AbstractBuilder;
import org.objectledge.web.mvc.builders.BuildException;

import net.cyklotron.cms.organizations.OrganizationRegistryService;

/**
 * View organization's news outgoing feed.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Download.java,v 1.6 2006-01-02 11:42:17 rafal Exp $
 */

public class NewsFeed
    extends AbstractBuilder
{
    private OrganizationRegistryService organizationRegistry;

    public NewsFeed(Context context, OrganizationRegistryService ngoDatabaseService)
    {
        super(context);
        this.organizationRegistry = ngoDatabaseService;
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
            writer.write(organizationRegistry.getOrganizationNewsFeed(parameters));
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
