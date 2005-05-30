package net.cyklotron.cms.modules.actions.appearance.skin;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.MergingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.upload.FileUpload;
import org.objectledge.upload.UploadContainer;
import org.objectledge.upload.UploadLimitExceededException;
import org.objectledge.utils.StackTrace;
import org.objectledge.utils.StringUtils;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.ComponentResource;
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
 * @version $Id: CreateComponentTemplate.java,v 1.5 2005-05-30 08:17:09 rafal Exp $
 */
public class CreateComponentTemplate extends BaseAppearanceAction
{
    protected FileUpload fileUpload;
    
    protected Templating templating;
    
    public CreateComponentTemplate(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FileSystem fileSystem,
        SkinService skinService, IntegrationService integrationService, FileUpload fileUpload,
        Templating templating)
    {
        super(logger, structureService, cmsDataFactory, styleService, fileSystem, skinService,
                        integrationService);
        this.fileUpload = fileUpload;
        this.templating = templating;
    }

    /* overriden */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        String skin = parameters.get("skin");
        String app = parameters.get("appName");
        String component = parameters.get("compName");
        String variant = parameters.get("variant","Default");
        String state = parameters.get("state","Default");
        ApplicationResource appRes = integrationService.getApplication(coralSession, app);
        ComponentResource compRes = integrationService.getComponent(coralSession, appRes, 
            component);        
        String source = parameters.get("source","app");
        UploadContainer file;
        try
        {
            file = fileUpload.getContainer("file");
        }
        catch(UploadLimitExceededException e)
        {
            // TODO Inform the user abour a problem in file upload
            throw e;
        }
        SiteResource site = getSite(context);
        try
        {
            String contents = null;
            if(source.equals("file"))
            {
                if(file == null)
                {
                    templatingContext.put("result","file_not_selected");
                }
                else
                {
                    contents = file.getString();
                }
            }
            else if(source.equals("app"))
            {
                Locale locale = StringUtils.getLocale(parameters.
                    get("locale"));
                contents = skinService.getComponentTemplateContents(compRes.getApplicationName(), 
                    compRes.getComponentName(), state, locale);
            }
            else if(source.equals("def_variant"))
            {
                contents = skinService.getComponentTemplateContents(site, skin, 
                    compRes.getApplicationName(), compRes.getComponentName(), 
                    "Default", state);
            }
            else
            {
                contents = "";
            }                

            skinService.createComponentTemplate(coralSession, site, skin,
                compRes.getApplicationName(), compRes.getComponentName(), 
                variant, state, contents);

            TemplatingContext blankContext = templating.createContext();
            StringReader in = new StringReader(contents);
            StringWriter out = new StringWriter();
            try
            {
                templating.merge(blankContext, in, out, "<component template>");
            }
            catch(MergingException e)
            {
                templatingContext.put("result", "template_saved_parse_error");
                templatingContext.put("parse_trace", new StackTrace(e));
                mvcContext.setView("appearance.skin.EditComponentTemplate");
                return;                
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
        if(templatingContext.containsKey("result"))
        {
            mvcContext.setView("appearance.skin.CreateComponentTemplate");
        }
        else
        {
            templatingContext.put("result","added_successfully");
        }
   }
}
