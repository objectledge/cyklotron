package net.cyklotron.cms.modules.views.appearance.skin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.ScreenVariantResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.style.StyleService;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CreateScreenTemplate.java,v 1.4 2005-01-26 05:23:22 pablo Exp $
 */
public class CreateScreenTemplate extends BaseAppearanceScreen
{
    
    
    public CreateScreenTemplate(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StyleService styleService, SkinService skinService,
        IntegrationService integrationService, Templating templating)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, styleService,
                        skinService, integrationService, templating);
        // TODO Auto-generated constructor stub
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) 
        throws ProcessingException
    {
        try
        {
            String skin = parameters.get("skin");
            String app = parameters.get("appName");
            String screen = parameters.get("screenName");
            String variant = parameters.get("variant","Default");
            String state = parameters.get("state","Default");
            SiteResource site = getSite();
            templatingContext.put("skin", skin);
            templatingContext.put("appName", app);
            templatingContext.put("screenName", screen);
            templatingContext.put("variant", variant);
            if(parameters.isDefined("state"))
            {
                templatingContext.put("state", state);
            }
            ApplicationResource appRes = integrationService.getApplication(coralSession, app);
            ScreenResource screenRes = integrationService.getScreen(coralSession, appRes, 
                screen);
            ScreenVariantResource variantRes = skinService.
                getScreenVariant(coralSession, site, skin, screenRes.getApplicationName(), 
                screenRes.getScreenName(), variant);
            templatingContext.put("variant_description", variantRes.getDescription());
            List supported = skinService.getScreenTemplateLocales(screenRes.getApplicationName(), 
                screenRes.getScreenName(), state);
            Map locales = new HashMap();
            for(Iterator i = supported.iterator(); i.hasNext();)
            {
                Locale l = (Locale)i.next();
                locales.put(l, l.toString());
            }
            templatingContext.put("locales", locales);
            if(skinService.hasScreenTemplate(coralSession, site, skin, 
                screenRes.getApplicationName(), screenRes.getScreenName(), 
                "Default", state))
            {
                templatingContext.put("def_variant_present", Boolean.TRUE);
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retreive information", e);
        }
    }
}
