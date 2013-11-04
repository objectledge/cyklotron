package net.cyklotron.cms.modules.actions.structure;

import java.awt.image.ImagingOpException;
import java.io.IOException;

import org.apache.activemq.util.ByteArrayInputStream;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.upload.FileUpload;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.files.DirectoryResource;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.structure.internal.ProposedDocumentData;
import net.cyklotron.cms.style.StyleService;

public abstract class BaseProposeDocumentAction
    extends BaseStructureAction
{
    protected final FilesService filesService;

    protected final FileUpload uploadService;

    public BaseProposeDocumentAction(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FilesService filesService,
        FileUpload uploadService)
    {
        super(logger, structureService, cmsDataFactory, styleService);
        this.filesService = filesService;
        this.uploadService = uploadService;
    }

    protected FileResource createAttachment(ProposedDocumentData data, int i,
        DirectoryResource attachmentDirectory, CoralSession coralSession)
        throws IllegalArgumentException, ImagingOpException, IOException, ProcessingException,
        FilesException
    {
        String description = data.getAttachmentDescription(i);
        byte[] contents = data.getAttachmentContents(i);
        String contentType = data.getAttachmentType(i);
        FileResource file = filesService.createFile(coralSession, data.getAttachmentName(i),
            new ByteArrayInputStream(contents), contentType, null, attachmentDirectory);
        file.setDescription(description);
        file.update();
        return file;
    }
}
