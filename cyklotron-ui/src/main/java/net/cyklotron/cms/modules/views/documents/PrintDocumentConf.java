package net.cyklotron.cms.modules.views.documents;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.modules.views.BaseCMSScreen;

import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

public class PrintDocumentConf
	extends BaseCMSScreen
{
	protected Logger log;
    
	public PrintDocumentConf()
	{
		log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("documents");
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
		CmsData cmsData = getCmsData();
		try
		{
			// get config
			Parameters conf = prepareComponentConfig(parameters, templatingContext);
			Resource parent = cmsData.getHomePage().getParent();
			String path = conf.get("printNodePath",null);
			if(path != null)
			{
				Resource[] nodes = coralSession.getStore().getResourceByPath(parent, path);
				if(nodes.length > 1)
				{
					// ???
					throw new ProcessingException("too many print nodes with the same path");
				}
				templatingContext.put("print_node", nodes[0]);
			}
		}
		catch(Exception e)
		{
			throw new ProcessingException("cannot find configured print node", e);
		}
    }
}
