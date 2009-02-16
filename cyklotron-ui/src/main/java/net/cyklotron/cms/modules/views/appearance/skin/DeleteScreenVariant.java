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
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ScreenResource;
import net.cyklotron.cms.integration.ScreenStateResource;
import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.ScreenVariantResource;
import net.cyklotron.cms.skins.SkinException;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.style.StyleService;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DeleteScreenVariant.java,v 1.4 2005-03-08 10:57:43 pablo Exp $
 */
public class DeleteScreenVariant extends BaseAppearanceScreen
{
    
    public DeleteScreenVariant(org.objectledge.context.Context context, Logger logger,
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
        String screen = parameters.get("screenName");
        String variant = parameters.get("variant");
        templatingContext.put("skin", skin);
        templatingContext.put("appName", app);
        templatingContext.put("screenName", screen);        
        templatingContext.put("variant", variant);        
        ApplicationResource appRes = integrationService.getApplication(coralSession, app);
        ScreenResource screenRes =
            integrationService.getScreen(coralSession, appRes, screen);
        try
        {
            ScreenVariantResource variantRes =
                skinService.getScreenVariant(coralSession, 
                    site,
                    skin,
                    screenRes.getApplicationName(),
                    screenRes.getScreenName(),
                    variant);
            templatingContext.put("variant_description", variantRes.getDescription());
            
            ScreenStateResource[] states = integrationService.getScreenStates(coralSession, screenRes); 
            if(states.length > 0)
            {
                boolean stateTemplatesPresent = false;
                for (int i = 0; i < states.length; i++)
                {
                    if (skinService
                        .hasScreenTemplate(coralSession, 
                            site,
                            skin,
                            screenRes.getApplicationName(),
                            screenRes.getScreenName(),
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
                    .hasScreenTemplate(coralSession, 
                        site,
                        skin,
                        screenRes.getApplicationName(),
                        screenRes.getScreenName(),
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
