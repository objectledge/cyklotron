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
import net.cyklotron.cms.style.StyleService;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CreateFile.java,v 1.6 2005-05-30 08:51:26 rafal Exp $
 */
public class CreateFile extends BaseAppearanceAction
{
    protected FileUpload fileUpload;
    
    public CreateFile(Logger logger, StructureService structureService,
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
        String path = parameters.get("path");
        String name = parameters.get("name");
        String skin = parameters.get("skin");
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
        if(name.length() == 0)
        {
            name = file.getFileName();
        }
        path = path.replace(',', '/') + name;
        SiteResource site = getSite(context);
        try
        {
            if(skinService.contentItemExists(site, skin, path))
            {   
                templatingContext.put("result","file_or_directory_exists");
            }
            else
            {
                if(file == null)
                {
                    templatingContext.put("result","file_not_selected");
                }
                else
                {
                    skinService.createContentFile(site, skin, path, file.getInputStream());
                }
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
        if(templatingContext.containsKey("result"))
        {
            mvcContext.setView("appearance.skin.CreateFile");
        }
        else
        {
            templatingContext.put("result","file_created");
        }
    }
}
