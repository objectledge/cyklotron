package net.cyklotron.cms.modules.views.appearance.skin;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.ComponentResource;
import net.cyklotron.cms.integration.ComponentStateResource;
import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.ComponentVariantResource;
import net.cyklotron.cms.skins.SkinException;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DeleteComponentVariant.java,v 1.2 2005-01-25 11:23:41 pablo Exp $
 */
public class DeleteComponentVariant extends BaseAppearanceScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        String skin = parameters.get("skin");
        String app = parameters.get("appName");
        SiteResource site = getSite();
        String component = parameters.get("compName");
        String variant = parameters.get("variant");
        templatingContext.put("skin", skin);
        templatingContext.put("appName", app);
        templatingContext.put("compName", component);        
        templatingContext.put("variant", variant);        
        ApplicationResource appRes = integrationService.getApplication(app);
        ComponentResource compRes =
            integrationService.getComponent(appRes, component);
        try
        {
            ComponentVariantResource variantRes =
                skinService.getComponentVariant(
                    site,
                    skin,
                    compRes.getApplicationName(),
                    compRes.getComponentName(),
                    variant);
            templatingContext.put("variant_description", variantRes.getDescription());
            
            ComponentStateResource[] states = integrationService.getComponentStates(compRes); 
            if(states.length > 0)
            {
                boolean stateTemplatesPresent = false;
                for (int i = 0; i < states.length; i++)
                {
                    if (skinService
                        .hasComponentTemplate(
                            site,
                            skin,
                            compRes.getApplicationName(),
                            compRes.getComponentName(),
                            variant,
                            states[i].getName()))
                    {
                        stateTemplatesPresent = true;
                    }
                }
                if(stateTemplatesPresent)
                {
                    templatingContext.put("warn_stateful", Boolean.TRUE);
                }
            }
            else
            {
                if(skinService
                    .hasComponentTemplate(
                        site,
                        skin,
                        compRes.getApplicationName(),
                        compRes.getComponentName(),
                        variant,
                        "Default"))
                {
                    templatingContext.put("warn_stateless", Boolean.TRUE);
                }
            }
        }
        catch (SkinException e)
        {
            throw new ProcessingException("failed to look up information");            
        }
    }
}
