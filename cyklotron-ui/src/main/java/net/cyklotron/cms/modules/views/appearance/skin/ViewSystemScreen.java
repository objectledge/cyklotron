package net.cyklotron.cms.modules.views.appearance.skin;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.style.StyleService;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Template;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.builders.BuildException;
import org.objectledge.web.mvc.builders.EnclosingView;

/**
 * 
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id$
 */
public class ViewSystemScreen extends BaseAppearanceScreen
{
    
    public ViewSystemScreen(org.objectledge.context.Context context, Logger logger,
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
        /*
        String skin = parameters.get("skin");
        String screen = parameters.get("screen");
        SiteResource site = getSite();
        try
        {
			templatingContext.put("layout_preview", Boolean.TRUE);
            getCmsData().setSkinName(skin);
            Template screenTemplate = skinService.getSystemScreenTemplate(coralSession, site, skin, screen);
			String content = screenTemplate.merge(templatingContext);
			templatingContext.put("embeddedPlaceholder", content);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to load template", e);        
        }
        */
    }
	
    
    /**
     * {@inheritDoc}
     */
    public String build(Template template, String embeddedBuildResults)
        throws BuildException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        String skin = parameters.get("skin");
        String screen = parameters.get("screen");
        Template screenTemplate = null;
        try
        {
            SiteResource site = getSite();
            screenTemplate = skinService.getSystemScreenTemplate(coralSession, site, skin, screen);
        }
        catch(Exception e)
        {
            throw new BuildException("Failed to get system screen template", e);
        }
        return super.build(screenTemplate, embeddedBuildResults); 
    }

    
    
	/**
	 * {@inheritDoc}
	 */
    public EnclosingView getEnclosingView(String thisViewName)
    {
        return new EnclosingView("Page");
    }

}

