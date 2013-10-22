package net.cyklotron.cms.modules.actions.structure;

import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.activemq.util.ByteArrayInputStream;
import org.apache.tika.io.IOUtils;
import org.imgscalr.Scalr;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.upload.FileUpload;
import org.objectledge.upload.UploadContainer;

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

    protected byte[] resizeIfNecessary(UploadContainer uploadContainer, int maxSize)
        throws IOException, IllegalArgumentException, ImagingOpException, ProcessingException
    {
        byte[] srcBytes = IOUtils.toByteArray(uploadContainer.getInputStream());
        if(maxSize > 0)
        {
            final ByteArrayInputStream is = new ByteArrayInputStream(srcBytes);
            String contentType = filesService.detectMimeType(is, uploadContainer.getFileName());
            if(contentType.startsWith("image/"))
            {
                is.reset();
                BufferedImage srcImage = ImageIO.read(is);
                BufferedImage targetImage = null;
                try
                {
                    if(srcImage.getWidth() > maxSize || srcImage.getHeight() > maxSize)
                    {
                        targetImage = Scalr.resize(srcImage, Scalr.Method.AUTOMATIC,
                            Scalr.Mode.AUTOMATIC, maxSize, maxSize);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(targetImage, "jpeg", baos);
                        return baos.toByteArray();
                    }
                }
                finally
                {
                    srcImage.flush();
                    if(targetImage != null)
                    {
                        targetImage.flush();
                    }
                }
            }
        }
        return srcBytes;
    }

    protected FileResource createAttachment(ProposedDocumentData data, int i,
        DirectoryResource attachmentDirectory, int maxSize, CoralSession coralSession)
        throws IllegalArgumentException, ImagingOpException, IOException, ProcessingException,
        FilesException
    {
        UploadContainer container = data.getAttachmentContainer(i, uploadService);
        if(container != null)
        {
            String description = data.getAttachmentDescription(i);
            byte[] contents = resizeIfNecessary(container, maxSize);
            final ByteArrayInputStream contentsIs = new ByteArrayInputStream(contents);
            String contentType = filesService.detectMimeType(contentsIs, container.getFileName());
            contentsIs.reset();
            FileResource file = filesService.createFile(coralSession,
                ProposedDocumentData.getAttachmentName(container.getFileName()), contentsIs,
                contentType, null, attachmentDirectory);
            file.setDescription(description);
            file.update();
            return file;
        }
        return null;
    }
}
