package net.cyklotron.cms.modules.components.documents;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.documents.DocumentService;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.labeo.Labeo;
import net.labeo.services.ServiceBroker;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Document print component displays a link to document printing page.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: PrintDocument.java,v 1.1 2005-01-24 04:35:16 pablo Exp $
 */
public class PrintDocument
    extends SkinableCMSComponent
{
    public PrintDocument()
    {
        ServiceBroker broker = Labeo.getBroker();
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(DocumentService.LOGGING_FACILITY);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        CmsData cmsData = getCmsData();
		try
		{
			Resource parent = cmsData.getHomePage().getParent();
			Parameters conf = cmsData.getComponent().getConfiguration();
			String path = conf.get("printNodePath",null);
			if(path == null)
			{
				cmsData.getComponent().error("print node not configured", null);
				return;
			}
			Resource[] nodes = coralSession.getStore().getResourceByPath(parent, path);
			if(nodes.length == 0)
			{
				cmsData.getComponent().error("cannot find configured print node", null);
				return;
			}
			if(nodes.length > 1)
			{
				cmsData.getComponent().error("too many print nodes with the same path", null);
				return;
			}
			templatingContext.put("print_node", nodes[0]);
		}
		catch(Exception e)
		{
			cmsData.getComponent().error("cannot find configured print node", e);
		}
    }
}
