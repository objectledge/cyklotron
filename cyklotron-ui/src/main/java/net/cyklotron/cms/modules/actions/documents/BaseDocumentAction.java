package net.cyklotron.cms.modules.actions.documents;

import org.objectledge.pipeline.ProcessingException;

import pl.caltha.forms.FormsException;
import pl.caltha.forms.FormsService;
import pl.caltha.forms.Instance;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentService;
import net.cyklotron.cms.modules.actions.structure.BaseStructureAction;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: BaseDocumentAction.java,v 1.1 2005-01-24 04:34:39 pablo Exp $
 */
public abstract class BaseDocumentAction
    extends BaseStructureAction
{
    /** Form-tool service. */
    protected  FormsService formService;

    /** Document service. */
    protected  DocumentService documentService;

    public BaseDocumentAction()
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).
                getFacility(DocumentService.LOGGING_FACILITY);
        formService = (FormsService)broker.getService(FormsService.SERVICE_NAME);
        documentService = (DocumentService)broker.getService(DocumentService.SERVICE_NAME);
    }

    protected Instance getInstance(RunData data)
        throws ProcessingException
    {
        try
        {
            return formService.getInstance(DocumentService.FORM_NAME, data);
        }
        catch(FormsException e)
        {
            throw new ProcessingException("Cannot get a form instance", e);
        }
    }

    public DocumentNodeResource getDocument(RunData data)
        throws ProcessingException
    {
        NavigationNodeResource node = getNode(context);
        if(node instanceof DocumentNodeResource)
        {
            return (DocumentNodeResource)node;
        }
        else
        {
            throw new ProcessingException("Current node is not a document node");
        }
    }

    public void restoreView(RunData data)
        throws ProcessingException
    {
        String viewName = "structure,NaviInfo";
        // WARN: ugly hacking
        if(data.getLocalContext().hasAttribute("document_edit_return_view"))
        {
            viewName = (String)httpContext.getSessionAttribute("document_edit_return_view");
        }
        viewName = parameters.get("target_view",viewName);
        try
        {
            data.setView(viewName);
        }
        catch(NotFoundException e)
        {
            throw new ProcessingException("Cannot restore a view called '"+viewName+"'", e);
        }
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return getCmsData(context).getNode(context).canModify(coralSession.getUserSubject());
    }
}
