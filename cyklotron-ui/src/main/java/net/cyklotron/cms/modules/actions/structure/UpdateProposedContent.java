package net.cyklotron.cms.modules.actions.structure;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.upload.FileUpload;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.structure.internal.ProposedDocumentData;
import net.cyklotron.cms.style.StyleService;

public class UpdateProposedContent
    extends BaseStructureAction
{
    private final FileUpload fileUpload;

    public UpdateProposedContent(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FileUpload fileUpload)
    {
        super(logger, structureService, cmsDataFactory, styleService);
        this.fileUpload = fileUpload;
    }

    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long docId = parameters.getLong("doc_id");
            DocumentNodeResource node = DocumentNodeResourceImpl.getDocumentNodeResource(coralSession, docId);

            CmsData cmsData = cmsDataFactory.getCmsData(context);
            Parameters screenConfig = cmsData.getEmbeddedScreenConfig();
            ProposedDocumentData data = new ProposedDocumentData(screenConfig);
            data.fromParameters(parameters, coralSession);
            
            boolean valid = true;
            // check required parameters
            if(!data.isValid())
            {
                valid = false;
                templatingContext.put("result", data.getValidationFailure());
            }
            
            // file upload - checking
            if(valid && !data.isFileUploadValid(coralSession, fileUpload))
            {
                valid = false;
                templatingContext.put("result", data.getValidationFailure());
            }
            
            if(valid)
            {
                data.toProposal(node);
            }
        }
        catch(Exception e)
        {
            logger.error("excception", e);
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
    }
}
