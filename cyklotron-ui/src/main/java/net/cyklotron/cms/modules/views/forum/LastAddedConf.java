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
 * @version $Id: LastAddedConf.java,v 1.2 2005-01-25 11:23:58 pablo Exp $
 */
public class LastAddedConf extends BaseCMSScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
    	Parameters conf = prepareComponentConfig(parameters, templatingContext);
    	templatingContext.put("forum_node", conf.get("forum_node","no_selection"));	
	}
}
