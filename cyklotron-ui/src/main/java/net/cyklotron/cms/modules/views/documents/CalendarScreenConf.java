package net.cyklotron.cms.modules.views.documents;

import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.BaseCMSScreen;

/**
 * A screen for configuring calendar screen.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: CalendarScreenConf.java,v 1.2 2005-01-25 11:23:45 pablo Exp $
 */
public class CalendarScreenConf extends BaseCMSScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
		Parameters screenConfig = prepareScreenConfig(data);
		long root1 = screenConfig.get("category_id_1").asLong(-1);
		long root2 = screenConfig.get("category_id_2").asLong(-1);
		long index = screenConfig.get("index_id").asLong(-1);
		try
		{
			if(root1 != -1)
			{
				templatingContext.put("category_1", coralSession.getStore().getResource(root1));
			}		               
			if(root2 != -1)
			{
				templatingContext.put("category_2", coralSession.getStore().getResource(root2));
			}
			if(index != -1)
			{
				templatingContext.put("index", coralSession.getStore().getResource(index));
			}
		}
		catch(Exception e)
		{
			throw new ProcessingException("Exception occurred",e);
		}
    }
}
