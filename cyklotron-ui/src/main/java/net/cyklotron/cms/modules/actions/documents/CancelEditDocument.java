package net.cyklotron.cms.modules.actions.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import pl.caltha.forms.FormsService;
import pl.caltha.forms.Instance;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: CancelEditDocument.java,v 1.3 2005-03-08 10:51:57 pablo Exp $
 */
public class CancelEditDocument extends BaseDocumentAction
{
    
    public CancelEditDocument(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FormsService formsService,
        DocumentService documentService)
    {
        super(logger, structureService, cmsDataFactory, styleService, formsService, documentService);
        
    }
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        // prepare needed variables
        Instance instance = getInstance(httpContext);

        // kill da instance
        formService.removeInstance(httpContext, instance);
        
        // return to original screen
        restoreView(httpContext, mvcContext, parameters);
    }
}
