package net.cyklotron.cms.modules.actions.documents;

import org.objectledge.pipeline.ProcessingException;

import pl.caltha.forms.Form;
import pl.caltha.forms.Instance;

/**
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdateDocumentFormInstance.java,v 1.1 2005-01-24 04:34:39 pablo Exp $
 */
public class UpdateDocumentFormInstance extends BaseDocumentAction
{
    /** Document edit form. */
    protected Form form = null;

    public UpdateDocumentFormInstance()
    {
        form = documentService.getDocumentEditForm();
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        // process the form instance
        Instance instance = getInstance(data);
        try
        {
            form.process(instance, data);
        }
        catch(Exception e)
        {
            throw new ProcessingException("Document edit form processing failed", e);
        }
    }
}
