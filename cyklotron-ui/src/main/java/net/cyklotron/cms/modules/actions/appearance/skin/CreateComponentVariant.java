package net.cyklotron.cms.modules.actions.appearance.skin;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.ComponentResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.ComponentVariantResource;
import net.cyklotron.cms.skins.SkinException;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CreateComponentVariant.java,v 1.4 2005-03-09 09:58:31 pablo Exp $
 */
public class CreateComponentVariant extends BaseAppearanceAction
{

    public CreateComponentVariant(Logger logger, StructureService structureService,
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
        String compName = parameters.get("compName");
        ApplicationResource appRes = integrationService.getApplication(coralSession, appName);
        ComponentResource compRes = integrationService.getComponent(coralSession, appRes, 
            compName);
        boolean defaultVariant = parameters.get("variant","default").equals("default");
        String name = defaultVariant ? "Default" :
            parameters.get("name");
        String description = parameters.get("description");
        try
        {
            if(skinService.hasComponentVariant(coralSession, site, skin, 
                compRes.getApplicationName(), compRes.getComponentName(), name))
            {
                templatingContext.put("result","variant_exists");
            }
            else
            {
                ComponentVariantResource variant = skinService.
                    createComponentVariant(coralSession, site, skin, 
                    compRes.getApplicationName(), compRes.getComponentName(), 
                    name, coralSession.getUserSubject());
                variant.setDescription(description);
                variant.update();
            }
        }
        catch(SkinException e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
        if(templatingContext.containsKey("result"))
        {
            mvcContext.setView("appearance.skin.CreateComponentVariant");
        }
        else
        {
            templatingContext.put("result","added_successfully");
        }
    }
}
