package net.cyklotron.cms.modules.views.forum;

import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.BaseCMSScreen;

/**
 * A screen for configuring last added posts.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: LastAddedConf.java,v 1.1 2005-01-24 04:34:44 pablo Exp $
 */
public class LastAddedConf extends BaseCMSScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
    	Parameters conf = prepareComponentConfig(parameters, templatingContext);
    	templatingContext.put("forum_node", conf.get("forum_node","no_selection"));	
	}
}
