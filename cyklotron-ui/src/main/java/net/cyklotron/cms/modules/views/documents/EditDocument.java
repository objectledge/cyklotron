package net.cyklotron.cms.modules.views.documents;

import pl.caltha.forms.Form;
import pl.caltha.forms.FormsException;
import pl.caltha.forms.FormsService;
import pl.caltha.forms.Instance;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.documents.DocumentException;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentService;

/**
 * The screen assember for form-tool test app.
 */
public class EditDocument extends BaseDocumentScreen
{
    /** Form-tool service. */
    protected  FormsService formService;
    /** Document edit form. */
    protected Form form = null;

    
    public EditDocument()
    {
        formService = (FormsService)broker.getService(FormsService.SERVICE_NAME);
        form = documentService.getDocumentEditForm();
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        // get processed node
        DocumentNodeResource doc = getDocument(data);

        // prepare needed variables
        Instance instance = getInstance(data);
        Subject subject = coralSession.getUserSubject();

		// kill da instance
		if (parameters.get("from_list").asBoolean(false))
		{
			formService.removeInstance(data, instance);
            instance = getInstance(data);
		}

        // initialise instance with document data if this is the first hit
        if(!instance.isDirty())
        {
            if(!prepareInstance(doc, instance, context, subject))
            {
                // operation failed
                throw new ProcessingException("Could not prepare the document with id="+doc.getIdString()
                            +" for editing");
            }

            try
            {
                form.process(instance, data);
            }
            catch(Exception e)
            {
                throw new ProcessingException("Document edit form processing failed", e);
            }
        }

        templatingContext.put("doc-edit-instance", instance);

        // WARN: ugly hacking
        // save view
        if(parameters.get("return_view").isDefined())
        {
            String returnView = parameters.get("return_view",null);
            httpContext.setSessionAttribute("document_edit_return_view", returnView);
        }
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

    protected boolean prepareInstance(DocumentNodeResource doc, Instance instance,
                                        Context context, Subject subject)
    {
        try
        {
            documentService.copyFromDocumentNode(doc, instance.getDocument());
            // the insatnce is changed so we set it dirty
            instance.setDirty(true);
        }
        catch(DocumentException e)
        {
            templatingContext.put("result","exception");
            log.error("DocumentException: ",e);
            templatingContext.put("trace", StringUtils.stackTrace(e));
            return false;
        }
        return true;
    }
}
