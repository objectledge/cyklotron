package net.cyklotron.cms.modules.views.documents;

import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentService;
import net.cyklotron.cms.modules.views.structure.BaseStructureScreen;
import net.cyklotron.cms.structure.NavigationNodeResource;

/** Base class for document editing screens.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: BaseDocumentScreen.java,v 1.2 2005-01-24 10:26:54 pablo Exp $
 */
public class BaseDocumentScreen extends BaseStructureScreen implements Secure
{
    /** Document service. */
    protected  DocumentService documentService;

    public BaseDocumentScreen()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).
                getFacility(DocumentService.LOGGING_FACILITY);
        documentService = (DocumentService)broker.getService(DocumentService.SERVICE_NAME);
    }

    public DocumentNodeResource getDocument(RunData data)
        throws ProcessingException
    {
        NavigationNodeResource node = getNode();
        if(node instanceof DocumentNodeResource)
        {
            return (DocumentNodeResource)node;
        }
        else
        {
            throw new ProcessingException("Current node is not a document node");
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return checkModifyPermission(data);
    }
}

