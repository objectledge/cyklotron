package net.cyklotron.cms.modules.components.link;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.link.LinkService;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.skins.SkinService;


/**
 * The discussion list screen class.
 */
public class ProposeLink
    extends SkinableCMSComponent
{
    public static final String COMPONENT_NAME = "cms:component:link,ProposeLink";
    private LinkService linkService;
    
    public ProposeLink(org.objectledge.context.Context context, Logger logger,
        Templating templating, CmsDataFactory cmsDataFactory, SkinService skinService,
        MVCFinder mvcFinder, LinkService linkService)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder);
        this.linkService = linkService;
	}

	public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
			throws ProcessingException
	{
		prepareState(context);
	}

    public void prepareDefault(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
    	try
    	{
        	templatingContext.put("link_root", linkService.getLinkRoot(coralSession, getSite(context)));
    	}
    	catch(Exception e)
    	{
    		componentError(context, "Exception occurred", e);
    	}
    }

    public void prepareResult(Context context)
        throws ProcessingException
    {
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
		templatingContext.put("stan", "---");
    }

    public String getState(Context context)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);        
        String state = parameters.get("state","");
		if(state.equals("propose_link_result"))
		{
	    	return "Result";
        }
		return "Default";
    }
}
