package net.cyklotron.cms.modules.views.documents;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentService;
import net.cyklotron.cms.modules.views.BaseSkinableScreen;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureUtil;
import net.labeo.Labeo;
import net.labeo.services.ServiceBroker;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Print Document screen displays document for printing.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: PrintDocument.java,v 1.2 2005-01-24 10:26:54 pablo Exp $
 */
public class PrintDocument
    extends BaseSkinableScreen
{
    public PrintDocument()
    {
        ServiceBroker broker = Labeo.getBroker();
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(DocumentService.LOGGING_FACILITY);
    }

    public void prepareDefault(RunData data, Context context)
        throws ProcessingException
    {
        CmsData cmsData = getCmsData();
        long printDocId = parameters.getLong("print_doc_id", -1L);
        if(printDocId == -1L)
        {
            screenError(cmsData.getNode(), context, "no 'print_doc_id' parameter defined");
            return;
        }

        try
        {
            NavigationNodeResource printDoc = StructureUtil.getNode(coralSession, printDocId);
            if(printDoc instanceof DocumentNodeResource)
            {
                templatingContext.put("document_tool", ((DocumentNodeResource)printDoc).getDocumentTool(data));
            }
            else
            {
                screenError(cmsData.getNode(), context, "cannot display a non document node");
            }
        }
        catch(ProcessingException e)
        {
            screenError(cmsData.getNode(), context, "cannot find document resource for printing");
        }
    }
    
    /**
     * Because this screen shows document from other part of the site depending on a URL parameter
     * it needs a special security check in order to avoid secure document data interception by
     * using hand written URLs.
     * This method check for visibility of a printing node itself and for visiblity of a document
     * viewed in print mode.
     */
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        if(super.checkAccess(data))
        {
            long printDocId = parameters.getLong("print_doc_id", -1L);
            if( printDocId != -1L )
            {
                CmsData cmsData = cmsDataFactory.getCmsData(context);
                NavigationNodeResource printDoc = StructureUtil.getNode(coralSession, printDocId);
                return printDoc.canView(cmsData, cmsData.getUserData().getSubject());
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }
    }
}

