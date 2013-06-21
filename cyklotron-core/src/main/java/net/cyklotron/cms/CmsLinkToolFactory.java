/*
 * Created on 2005-01-27
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.cyklotron.cms;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConfigurator;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.tools.LinkToolFactoryImpl;

import net.cyklotron.cms.rewrite.UrlRewriteRegistry;
import net.cyklotron.cms.site.SiteService;

/**
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Style - Code Templates
 * 
 * @author pablo
 */
public class CmsLinkToolFactory
    extends LinkToolFactoryImpl
{
    private CmsDataFactory cmsDataFactory;

    private final UrlRewriteRegistry urlRewriteRegistry;

    private final SiteService siteSevice;

    private final CoralSessionFactory coralSessionFactory;

    public CmsLinkToolFactory(Configuration config, Context context,
        WebConfigurator webConfigurator, CmsDataFactory cmsDataFactory,
        UrlRewriteRegistry urlRewriteRegistry, SiteService siteSevice,
        CoralSessionFactory coralSessionFactory)
        throws ConfigurationException
    {
        super(config, context, webConfigurator);
        this.cmsDataFactory = cmsDataFactory;
        this.urlRewriteRegistry = urlRewriteRegistry;
        this.siteSevice = siteSevice;
        this.coralSessionFactory = coralSessionFactory;
    }

    /**
     * {@inheritDoc}
     */
    public Object getTool()
    {
        HttpContext httpContext = HttpContext.getHttpContext(context);
        MVCContext mvcContext = MVCContext.getMVCContext(context);
        RequestParameters requestParameters = RequestParameters.getRequestParameters(context);
        return new CmsLinkTool(httpContext, context, mvcContext, requestParameters,
            linkToolConfiguration, cmsDataFactory, urlRewriteRegistry, siteSevice,
            coralSessionFactory);
    }

}
