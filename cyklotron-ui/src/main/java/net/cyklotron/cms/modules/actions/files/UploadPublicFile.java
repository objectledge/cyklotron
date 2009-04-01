package net.cyklotron.cms.modules.actions.files;

import java.text.SimpleDateFormat;
import org.jcontainer.dna.Logger;
import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.upload.FileUpload;
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

    private final SimpleDateFormat simpleDateFormat;

    public UploadPublicFile(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, FilesService filesService, FileUpload fileUpload,
        UserManager userManager, RelatedService relatedService)
    {
        super(logger, structureService, cmsDataFactory, filesService, fileUpload);
        this.simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        this.userManager = userManager;
    }

    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            AuthenticationContext authContext = context.getAttribute(AuthenticationContext.class);
            String login = userManager.getLogin(authContext.getUserPrincipal().getName());
            String date = simpleDateFormat.format(cmsDataFactory.getCmsData(context).getDate());
            parameters.set("item_prefix", login + "_" + date + "_");
            super.execute(context, parameters, mvcContext, templatingContext, httpContext,
                coralSession);
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
