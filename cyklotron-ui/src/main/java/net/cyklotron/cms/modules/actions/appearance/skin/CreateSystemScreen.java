package net.cyklotron.cms.modules.actions.appearance.skin;

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
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.LayoutResource;
import net.cyklotron.cms.style.StyleException;
import net.cyklotron.cms.style.StyleService;

/**
 * 
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id$
 */
public class CreateSystemScreen extends BaseAppearanceAction
{
    protected FileUpload fileUpload;
    
    public CreateSystemScreen(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FileSystem fileSystem,
        SkinService skinService, IntegrationService integrationService,
        FileUpload fileUpload)
    {
        super(logger, structureService, cmsDataFactory, styleService, fileSystem, skinService,
                        integrationService);
        this.fileUpload = fileUpload;
    }
    /* overriden */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        String screen = parameters.get("screen");
        String skin = parameters.get("skin");
        boolean useFile = parameters.get("source").
            equals("file");
        UploadContainer file;
        try
        {
            file = fileUpload.getContainer("file");
        }
        catch(UploadLimitExceededException e)
        {
            templatingContext.put("result", "file_size_exceeded");
            return;
        }
        SiteResource site = getSite(context);
        try
        {
            String contents = null;
            if(file == null)
            {
                if(useFile == true)
                {
                    templatingContext.put("result","file_not_selected");
                }
                else
                {
                    contents = "";
                }
            }
            else
            {
                contents = file.getString();
            }
                
            if(contents != null)
            {
                skinService.createSystemScreenTemplate(coralSession, site, skin, screen, 
                    contents, coralSession.getUserSubject());
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
        if(templatingContext.containsKey("result"))
        {
            mvcContext.setView("appearance.skin.CreateSystemScreen");
        }
        else
        {
            templatingContext.put("result","file_created");
        }
    }
}
