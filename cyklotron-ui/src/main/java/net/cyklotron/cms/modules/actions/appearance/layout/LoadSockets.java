package net.cyklotron.cms.modules.actions.appearance.layout;

import java.util.Arrays;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.upload.FileUpload;
import org.objectledge.upload.UploadContainer;
import org.objectledge.upload.UploadLimitExceededException;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

public class LoadSockets
    extends BaseAppearanceAction
{
    protected FileUpload uploadService;
    
    public LoadSockets(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FileSystem fileSystem,
        SkinService skinService, IntegrationService integrationService,
        FileUpload fileUpload)
    {
        super(logger, structureService, cmsDataFactory, styleService, fileSystem, skinService,
                        integrationService);
        uploadService = fileUpload;
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        int socketCount = parameters.getInt("socket_count");
        for(int i=1; i<=socketCount; i++)
        {
            parameters.remove("socket_"+i);
        }

        UploadContainer item;
        try
        {
            item = uploadService.getContainer("item1");
        }
        catch(UploadLimitExceededException e)
        {
            // TODO Inform the user abour a problem in file upload
            throw e;
        }

        try
        {
            String[] sockets = styleService.findSockets(item.getString());

            Arrays.sort(sockets);
            for(int i=0; i<sockets.length; i++)
            {
                parameters.set("socket_"+(i+1), sockets[i]);
            }
            parameters.set("socket_count", sockets.length);
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
    }
}
