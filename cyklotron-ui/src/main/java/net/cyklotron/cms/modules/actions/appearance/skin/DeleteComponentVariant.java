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
import net.cyklotron.cms.integration.ComponentStateResource;
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
 * @version $Id: DeleteComponentVariant.java,v 1.3 2005-03-08 10:50:47 pablo Exp $
 */
public class DeleteComponentVariant extends BaseAppearanceAction
{
    
    
    public DeleteComponentVariant(Logger logger, StructureService structureService,
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
        String app = parameters.get("appName");
        String component = parameters.get("compName");
        String variant =
            parameters.get("variant","Default");
        ApplicationResource appRes = integrationService.getApplication(coralSession, app);
        ComponentResource compRes =
            integrationService.getComponent(coralSession, appRes, component);
        try
        {
            ComponentStateResource[] states =
                integrationService.getComponentStates(coralSession, compRes);
            if (states.length > 0)
            {
                for (int i = 0; i < states.length; i++)
                {
                    if(skinService.hasComponentTemplate(coralSession, 
                        site,
                        skin,
                        compRes.getApplicationName(),
                        compRes.getComponentName(),
                        variant,
                        states[i].getName()))
                    {
                        skinService.deleteComponentTemplate(coralSession, 
                            site,
                            skin,
                            compRes.getApplicationName(),
                            compRes.getComponentName(),
                            variant,
                            states[i].getName());
                    }
                }
            }
            else
            {
                if(skinService.hasComponentTemplate(coralSession, 
                    site,
                    skin,
                    compRes.getApplicationName(),
                    compRes.getComponentName(),
                    variant,
                    "Default"))
                {
                    skinService.deleteComponentTemplate(coralSession, 
                        site,
                        skin,
                        compRes.getApplicationName(),
                        compRes.getComponentName(),
                        variant,
                        "Default");
                }
            }
            skinService.deleteComponentVariant(coralSession, 
                site,
                skin,
                compRes.getApplicationName(),
                compRes.getComponentName(),
                variant);
            }
        catch (Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
        if (templatingContext.containsKey("result"))
        {
            mvcContext.setView("appearance,skin,DeleteComponentVariant");
        }
        else
        {
            templatingContext.put("result", "deleted_successfully");
        }
    }
}
