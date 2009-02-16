package net.cyklotron.cms.modules.actions.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import pl.caltha.forms.Form;
import pl.caltha.forms.FormsService;
import pl.caltha.forms.Instance;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdateDocumentFormInstance.java,v 1.2 2005-01-25 03:22:23 pablo Exp $
 */
public class UpdateDocumentFormInstance extends BaseDocumentAction
{
    /** Document edit form. */
    protected Form form = null;

    
    
    public UpdateDocumentFormInstance(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FormsService formsService,
        DocumentService documentService)
    {
        super(logger, structureService, cmsDataFactory, styleService, formsService, documentService);
        form = documentService.getDocumentEditForm();
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        // process the form instance
        Instance instance = getInstance(httpContext);
        try
        {
            form.process(instance, parameters);
        }
        catch(Exception e)
        {
            throw new ProcessingException("Document edit form processing failed", e);
        }
    }
}
