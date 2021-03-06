package net.cyklotron.cms.modules.views.appearance.skin;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.ComponentResource;
import net.cyklotron.cms.integration.ComponentStateResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.ComponentVariantResource;
import net.cyklotron.cms.skins.SkinException;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.style.StyleService;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DeleteComponentVariant.java,v 1.4 2005-03-08 10:57:43 pablo Exp $
 */
public class DeleteComponentVariant extends BaseAppearanceScreen
{
    
    public DeleteComponentVariant(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StyleService styleService, SkinService skinService,
        IntegrationService integrationService, Templating templating)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, styleService,
                        skinService, integrationService, templating);
        
    }
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
        ApplicationResource appRes = integrationService.getApplication(coralSession, app);
        ComponentResource compRes =
            integrationService.getComponent(coralSession, appRes, component);
        try
        {
            ComponentVariantResource variantRes =
                skinService.getComponentVariant(coralSession, 
                    site,
                    skin,
                    compRes.getApplicationName(),
                    compRes.getComponentName(),
                    variant);
            templatingContext.put("variant_description", variantRes.getDescription());
            
            ComponentStateResource[] states = integrationService.getComponentStates(coralSession, compRes); 
            if(states.length > 0)
            {
                boolean stateTemplatesPresent = false;
                for (int i = 0; i < states.length; i++)
                {
                    if (skinService
                        .hasComponentTemplate(coralSession, 
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
                    .hasComponentTemplate(coralSession, 
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
