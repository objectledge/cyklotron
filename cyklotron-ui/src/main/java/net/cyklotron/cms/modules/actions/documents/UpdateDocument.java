package net.cyklotron.cms.modules.actions.documents;

import pl.caltha.forms.Form;
import pl.caltha.forms.Instance;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.documents.DocumentException;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.structure.StructureException;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: UpdateDocument.java,v 1.2 2005-01-24 10:27:36 pablo Exp $
 */
public class UpdateDocument extends BaseDocumentAction
{
    /** Document edit form. */
    protected Form form = null;

    public UpdateDocument()
    {
        form = documentService.getDocumentEditForm();
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        // get processed node
        DocumentNodeResource doc = getDocument(data);

        // prepare needed variables
        Context context = data.getContext();
        Instance instance = getInstance(data);
        Subject subject = coralSession.getUserSubject();

        // process the form instance
        try
        {
            form.process(instance, data);
        }
        catch(Exception e)
        {
            throw new ProcessingException("Document edit form processing failed", e);
        }

        // if user requested a document save -> update the resource
        if(instance.isSubmitted())
        {
            updateDocument(doc , instance, context, subject);

            // kill da instance
            formService.removeInstance(data, instance);

            // return to original screen
            restoreView(data);
        }
    }

    protected boolean updateDocument(DocumentNodeResource doc, Instance instance,
                                     Context context, Subject subject)
    {
        try
        {
            documentService.copyToDocumentNode(doc, instance.getDocument());
        }
        catch(DocumentException e)
        {
            templatingContext.put("result","exception");
            log.error("DocumentException: ",e);
            templatingContext.put("trace", new StackTrace(e));
            return false;
        }

        String name = doc.getName();
        try
        {
            structureService.updateNode(doc, name, subject);
        }
        catch(StructureException e)
        {
            templatingContext.put("result","exception");
            log.error("StructureException: ",e);
            templatingContext.put("trace", new StackTrace(e));
            return false;
        }

        templatingContext.put("result","updated_successfully");
        return true;
    }
}
