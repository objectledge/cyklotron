package net.cyklotron.cms.modules.components.link;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.link.LinkService;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;


/**
 * The discussion list screen class.
 */
public class ProposeLink
    extends SkinableCMSComponent
{
	public static final String COMPONENT_NAME = "cms:component:link,ProposeLink";

	private LinkService linkService;

	public ProposeLink()
	{
		linkService = (LinkService)broker.getService(LinkService.SERVICE_NAME);
	}

	public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
			throws ProcessingException
	{
		prepareState(data,context);
	}

    public void prepareDefault(RunData data, Context context)
        throws ProcessingException
    {
    	try
    	{
        	templatingContext.put("link_root", linkService.getLinkRoot(getSite(context)));
    	}
    	catch(Exception e)
    	{
    		componentError(context, "Exception occurred", e);
    	}
    }

    public void prepareResult(RunData data, Context context)
        throws ProcessingException
    {
		templatingContext.put("stan", "blebleble");
    }

    public String getState(RunData data)
        throws ProcessingException
    {
        String state = parameters.get("state","");
		if(state.equals("propose_link_result"))
		{
	    	return "Result";
        }
		return "Default";
    }
}
