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
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinException;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.style.StyleService;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CreateComponentVariant.java,v 1.4 2005-03-08 10:57:43 pablo Exp $
 */
public class CreateComponentVariant extends BaseAppearanceScreen
{
    
    
    public CreateComponentVariant(org.objectledge.context.Context context, Logger logger,
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
        try
        {
            String skin = parameters.get("skin");
            String app = parameters.get("appName");
            SiteResource site = getSite();
            String component = parameters.get("compName");
            templatingContext.put("skin", skin);
            templatingContext.put("appName", app);
            templatingContext.put("compName", component);
            ApplicationResource appRes = integrationService.getApplication(coralSession, app);
            ComponentResource compRes = integrationService.getComponent(coralSession, appRes, 
                component);
            if(skinService.hasComponentVariant(coralSession, site, skin, 
                compRes.getApplicationName(), compRes.getComponentName(), 
                "Default"))
            {
                templatingContext.put("default_exits", Boolean.TRUE);
            }
            else
            {
                templatingContext.put("default_exits", Boolean.FALSE);                
            }
        }
        catch (SkinException e)
        {
            throw new ProcessingException("failed to check variant info");
        }        
    }
}
