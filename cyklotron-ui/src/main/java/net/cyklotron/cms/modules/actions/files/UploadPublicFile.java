package net.cyklotron.cms.modules.actions.files;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.upload.FileUpload;
import org.objectledge.upload.UploadContainer;
import org.objectledge.upload.UploadLimitExceededException;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.structure.StructureService;

/**
 * Accepts uploaded file and adds it to the Files repository, and optionally binds it with the
 * currently selected resource through the Related application.
 */
public class UploadPublicFile
    extends UploadFile
{

    private final UserManager userManager;

    private final FileUpload uploadService;

    public UploadPublicFile(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, FilesService filesService, FileUpload fileUpload,
        UserManager userManager, RelatedService relatedService)
    {
        super(logger, structureService, cmsDataFactory, filesService, fileUpload);
        this.userManager = userManager;
        this.uploadService = fileUpload;
    }

    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            String instanceName = parameters.get("ci", "");
            Parameters comp = cmsDataFactory.getCmsData(context).getComponent(instanceName).getConfiguration();
            AuthenticationContext authContext = context.getAttribute(AuthenticationContext.class);
            Long uploadMaxSize = comp.getLong("upload_max_size", -1L);
            String allowedFormats = comp.get("upload_allowed_formats", "").toLowerCase();
            UploadContainer uploadedFile = uploadService.getContainer("item1");
            List<String> allowedFormatList = Arrays.asList(allowedFormats.replaceAll("\\s+", ";")
                .split(";"));

            if(uploadedFile != null)
            {
                String fileName = uploadedFile.getFileName().replaceAll("[^A-Za-z0-9_.-]", "_")
                    .replaceAll("_+", "_");
                String fileExt = fileName.substring(fileName.lastIndexOf('.') + 1).trim()
                    .toLowerCase();
                if(!allowedFormatList.contains(fileExt))
                {
                    templatingContext.put("result", "file_type_not_allowed");
                    return;
                }
                if(uploadedFile.getSize() > uploadMaxSize * 1024)
                {
                    templatingContext.put("result", "file_size_exceeded");
                    return;
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String login = userManager.getLogin(authContext.getUserPrincipal().getName());
                String date = simpleDateFormat.format(cmsDataFactory.getCmsData(context).getDate());
                parameters.set("item_name", login + "_" + date + "_" + fileName);
                parameters.set("dir_id", comp.getLong("dir", -1L));

                super.execute(context, parameters, mvcContext, templatingContext, httpContext,
                    coralSession);
            }
            else
            {
                templatingContext.put("result", "not_uploaded_correctly");
            }

        }
        catch(UploadLimitExceededException e)
        {
            templatingContext.put("result", "file_size_exceeded");
            return;
        }
        catch(Exception e)
        {
            logger.error("UnknownUserException: ", e);
            templatingContext.put("result", "exception");
            templatingContext.put("trace", e.getMessage());
            return;
        }
    }

}
