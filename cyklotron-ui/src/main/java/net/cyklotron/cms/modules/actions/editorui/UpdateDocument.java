package net.cyklotron.cms.modules.actions.editorui;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.relation.Relation;
import org.objectledge.coral.relation.RelationModification;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.forms.Form;
import org.objectledge.forms.FormsService;
import org.objectledge.forms.Instance;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;


import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.documents.DocumentException;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentService;
import net.cyklotron.cms.modules.actions.documents.BaseDocumentAction;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: UpdateDocument.java,v 1.5 2005-06-13 11:08:27 rafal Exp $
 */
public class UpdateDocument extends BaseDocumentAction
{

    /** Document edit form. */
    protected Form form = null;
    
    protected CategoryService categoryService;
    
    public UpdateDocument(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FormsService formsService,
        DocumentService documentService, CategoryService categoryService)
    {
        super(logger, structureService, cmsDataFactory, styleService, formsService, documentService);
        form = documentService.getDocumentEditForm();
        this.categoryService = categoryService;
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
            setUnclassified(coralSession, categoryService, doc);

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
            structureService.updateNode(coralSession, doc, name, true, subject);
        }
        catch(StructureException e)
        {
            templatingContext.put("result","exception");
            logger.error("StructureException: ",e);
            templatingContext.put("trace", new StackTrace(e));
            return false;
        }
        catch(InvalidResourceNameException e)
        {
            // same name is re-set
        	throw new RuntimeException("unexpected exception", e);
        }

        templatingContext.put("result","updated_successfully");
        return true;
    }
    
    protected void setUnclassified(CoralSession coralSession,CategoryService categoryService, DocumentNodeResource node)
    { 
        // remove positiveCategory if document has been modified and site uses showUnclassified 
        // this hit is done only for editorui users !!! 
        
        boolean showUnclassified = structureService.isShowUnclassifiedNodes();
        Resource positiveCategory = structureService.getPositiveCategory();
        
        if(showUnclassified)
        {
            showUnclassified = structureService.showUnclassifiedInSite(node.getSite());
        }
        if(showUnclassified && positiveCategory != null)
        {
            Relation relation = categoryService.getResourcesRelation(coralSession);
            RelationModification modification = new RelationModification();
            
            modification.remove(positiveCategory, node);
            coralSession.getRelationManager().updateRelation(relation, modification);
        }
    }
}
