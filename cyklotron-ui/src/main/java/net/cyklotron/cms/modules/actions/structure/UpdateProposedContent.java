package net.cyklotron.cms.modules.actions.structure;

import static net.cyklotron.cms.structure.internal.ProposedDocumentData.getAttachmentName;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.upload.FileUpload;
import org.objectledge.upload.UploadContainer;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.documents.DocumentNodeResourceImpl;
import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.structure.internal.ProposedDocumentData;
import net.cyklotron.cms.style.StyleService;

public class UpdateProposedContent
    extends BaseStructureAction
{
    private final FileUpload fileUpload;

    private final FilesService filesService;

    public UpdateProposedContent(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FileUpload fileUpload,
        FilesService filesService)
    {
        super(logger, structureService, cmsDataFactory, styleService);
        this.fileUpload = fileUpload;
        this.filesService = filesService;
    }

    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long docId = parameters.getLong("doc_id");
            DocumentNodeResource node = DocumentNodeResourceImpl.getDocumentNodeResource(
                coralSession, docId);

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
                DirectoryResource dir = data.getAttachmenDirectory(coralSession);
                for(int index = data.getAttachmentsCurrentCount(); index < data
                    .getAttachmentsMaxCount(); index++)
                {
                    String description = data.getAttachmentDescription(index);
                    UploadContainer container = data.getAttachmentContainer(index, fileUpload);
                    if(container != null)
                    {
                        FileResource file = filesService.createFile(coralSession,
                            getAttachmentName(container.getFileName()), container.getInputStream(),
                            container.getMimeType(), null, dir);
                        file.setDescription(description);
                        file.update();
                        data.addAttachment(file);
                    }
                }

                for(long id : parameters.getLongs("remove_attachment"))
                {
                    data.removeAttachment(id, coralSession);
                }

                data.toProposal(node);
                node.update();
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
