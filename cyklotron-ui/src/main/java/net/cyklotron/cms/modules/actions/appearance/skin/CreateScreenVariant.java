package net.cyklotron.cms.modules.actions.appearance.skin;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ScreenResource;
import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.ScreenVariantResource;
import net.cyklotron.cms.skins.SkinException;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CreateScreenVariant.java,v 1.5 2005-06-13 11:08:35 rafal Exp $
 */
public class CreateScreenVariant extends BaseAppearanceAction
{
    
    
    public CreateScreenVariant(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FileSystem fileSystem,
        SkinService skinService, IntegrationService integrationService)
    {
        super(logger, structureService, cmsDataFactory, styleService, fileSystem, skinService,
                        integrationService);
    }
    /* overriden */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        SiteResource site = getSite(context);
        String skin = parameters.get("skin");        
        String appName = parameters.get("appName");
        String screenName = parameters.get("screenName");
        ApplicationResource appRes = integrationService.getApplication(coralSession, appName);
        ScreenResource screenRes = integrationService.getScreen(coralSession, appRes, 
            screenName);
        boolean defaultVariant = parameters.get("variant","default").equals("default");
        String name = defaultVariant ? "Default" :
            parameters.get("name");
        String description = parameters.get("description");
        try
        {
            if(skinService.hasScreenVariant(coralSession, site, skin, 
                screenRes.getApplicationName(), screenRes.getScreenName(), name))
            {
                templatingContext.put("result","variant_exists");
            }
            else
            {
                try
                {
                    ScreenVariantResource variant = skinService
                        .createScreenVariant(coralSession, site, skin, screenRes
                            .getApplicationName(), screenRes.getScreenName(), name);
                    variant.setDescription(description);
                    variant.update();
                }
                catch(InvalidResourceNameException e)
                {
                    templatingContext.put("result", "invalid_name");
                }
            }
        }
        catch(SkinException e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
        if(templatingContext.containsKey("result"))
        {
            mvcContext.setView("appearance.skin.CreateScreenVariant");
        }
        else
        {
            templatingContext.put("result","added_successfully");
        }
    }
}
