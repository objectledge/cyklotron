package net.cyklotron.cms.modules.actions.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import pl.caltha.forms.Form;
import pl.caltha.forms.FormsService;
import pl.caltha.forms.Instance;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentException;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentService;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: UpdateDocument.java,v 1.3 2005-01-25 03:22:23 pablo Exp $
 */
public class UpdateDocument extends BaseDocumentAction
{

    /** Document edit form. */
    protected Form form = null;
    
    public UpdateDocument(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FormsService formsService,
        DocumentService documentService)
    {
        super(logger, structureService, cmsDataFactory, styleService, formsService, documentService);
        form = documentService.getDocumentEditForm();
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        // get processed node
        DocumentNodeResource doc = getDocument(context);

        Instance instance = getInstance(httpContext);
        Subject subject = coralSession.getUserSubject();

        // process the form instance
        try
        {
            form.process(instance, parameters);
        }
        catch(Exception e)
        {
            throw new ProcessingException("Document edit form processing failed", e);
        }

        // if user requested a document save -> update the resource
        if(instance.isSubmitted())
        {
            updateDocument(doc , instance, context, subject, coralSession);

            // kill da instance
            formService.removeInstance(httpContext, instance);

            // return to original screen
            restoreView(httpContext, mvcContext, parameters);
        }
    }

    protected boolean updateDocument(DocumentNodeResource doc, Instance instance,
                                     Context context, Subject subject, CoralSession coralSession)
    {
        TemplatingContext templatingContext = (TemplatingContext)
            context.getAttribute(TemplatingContext.class);
        try
        {
            documentService.copyToDocumentNode(doc, instance.getDocument());
        }
        catch(DocumentException e)
        {
            templatingContext.put("result","exception");
            logger.error("DocumentException: ",e);
            templatingContext.put("trace", new StackTrace(e));
            return false;
        }

        String name = doc.getName();
        try
        {
            structureService.updateNode(coralSession, doc, name, subject);
        }
        catch(StructureException e)
        {
            templatingContext.put("result","exception");
            logger.error("StructureException: ",e);
            templatingContext.put("trace", new StackTrace(e));
            return false;
        }

        templatingContext.put("result","updated_successfully");
        return true;
    }
}
