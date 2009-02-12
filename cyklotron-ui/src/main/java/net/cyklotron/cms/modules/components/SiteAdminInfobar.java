package net.cyklotron.cms.modules.components;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;

public class SiteAdminInfobar
    extends BaseCMSComponent
{
    public SiteAdminInfobar(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory)
    {
        super(context, logger, templating, cmsDataFactory);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        templatingContext.put("authenticationContext", AuthenticationContext.getAuthenticationContext(context));
    }
}
