package net.cyklotron.cms.modules.components.documents;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentService;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.labeo.Labeo;
import net.labeo.services.ServiceBroker;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * DocumentView component displays document contents.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DocumentView.java,v 1.1 2005-01-24 04:35:16 pablo Exp $
 */
public class DocumentView
    extends SkinableCMSComponent
{
    public DocumentView()
    {
        ServiceBroker broker = Labeo.getBroker();
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(DocumentService.LOGGING_FACILITY);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        CmsData cmsData = getCmsData();
        NavigationNodeResource node = cmsData.getNode();
        if(node != null)
        {
            if(node instanceof DocumentNodeResource)
            {
                templatingContext.put("document_tool", ((DocumentNodeResource)node).getDocumentTool(data));
            }
            else
            {
                componentError(context, "Cannot display a non document node");
            }
        }
        else
        {
            componentError(context, "No navigation node selected");            
        }
    }
}
