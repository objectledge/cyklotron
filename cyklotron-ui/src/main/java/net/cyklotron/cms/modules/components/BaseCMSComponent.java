package net.cyklotron.cms.modules.components;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.modules.components.BaseCoralComponent;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * The base component class for CMS
 */
public abstract class BaseCMSComponent
    extends BaseCoralComponent
{
    /** logging facility */
    protected Logger logger;

    /** templating service */
    protected Templating templating;

    protected CmsDataFactory cmsDataFactory;
    
    public BaseCMSComponent(Context context, Logger logger, Templating templating, 
        CmsDataFactory cmsDataFactory)
    {
        super(context);
        this.logger = logger;
        this.templating = templating;
        this.cmsDataFactory = cmsDataFactory;
    }
    
    public final void process(Parameters parameters, TemplatingContext templatingContext, 
         MVCContext mvcContext,
         CoralSession coralSession)
        throws ProcessingException
    {
        HttpContext httpContext = HttpContext.getHttpContext(context);
        I18nContext i18nContext = I18nContext.getI18nContext(context);
        process(parameters, mvcContext, templatingContext, httpContext, i18nContext, coralSession);
    }
    
    public abstract void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException;


    public CmsData getCmsData()
    throws ProcessingException
    {
        return cmsDataFactory.getCmsData(context);
    }

    /** TODO: Remove after CmsData is widely used */
    public boolean isNodeDefined()
        throws ProcessingException
    {
        return getCmsData().isNodeDefined();
    }

    /** TODO: Remove after CmsData is widely used */
    public NavigationNodeResource getNode()
        throws ProcessingException
    {
        return getCmsData().getNode();
    }

    public SiteResource getSite(org.objectledge.context.Context context)
        throws ProcessingException
    {
        CmsData cmsData = getCmsData();
        if(cmsData.getSite() != null)
        { 
            return cmsData.getSite();
        }
        else
        {
            return cmsData.getGlobalComponentsDataSite();
        }
    }

    /** TODO: Remove after CmsData is widely used */
    public NavigationNodeResource getHomePage(org.objectledge.context.Context context)
        throws ProcessingException
    {
        return getCmsData().getHomePage();
    }

    // configuration support methods ///////////////////////////////////////////////////////////////

    protected Parameters getConfiguration()
    throws ProcessingException
    {
        return getCmsData().getComponent().getConfiguration();
    }

    protected void componentError(Context context, String message)
    throws ProcessingException
    {
        // TODO: params - (RunData data, String message)
        getCmsData().getComponent().error(message, null);
    }

    protected void componentError(Context context, String message,
                                  Throwable e)
    throws ProcessingException
    {
        // TODO: params - (RunData data, String message)
        getCmsData().getComponent().error(message, e);
    }
}
