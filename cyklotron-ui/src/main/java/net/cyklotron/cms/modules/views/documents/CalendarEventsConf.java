package net.cyklotron.cms.modules.views.documents;

import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.BaseCMSScreen;

public class CalendarEventsConf
	extends BaseCMSScreen
{
	protected Logger log;
    
	public CalendarEventsConf()
	{
		log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("documents");
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        // get config
        Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
        templatingContext.put("config", componentConfig);
		long root1 = componentConfig.get("category_id_1").asLong(-1);
		long root2 = componentConfig.get("category_id_2").asLong(-1);
		long index = componentConfig.get("index_id").asLong(-1);
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
