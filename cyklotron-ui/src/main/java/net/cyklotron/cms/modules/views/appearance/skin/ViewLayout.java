package net.cyklotron.cms.modules.views.appearance.skin;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Template;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.builders.EnclosingView;
import org.objectledge.web.mvc.tools.PageTool;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.style.StyleService;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ViewLayout.java,v 1.6 2005-05-24 03:49:48 pablo Exp $
 */
public class ViewLayout extends BaseAppearanceScreen
{
    
    public ViewLayout(org.objectledge.context.Context context, Logger logger,
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
        String layout = parameters.get("layout");
        SiteResource site = getSite();
        try
        {
			templatingContext.put("layout_preview", Boolean.TRUE);
			PageTool pageTool = (PageTool)templatingContext.get("pageTool");
            pageTool.addStyleLink("style/cms-component-wrapper.css");
            getCmsData().setSkinName(skin);
            Template layoutTemplate = skinService.getLayoutTemplate(coralSession, site, skin, layout);
			String content = layoutTemplate.merge(templatingContext);
			templatingContext.put("cmsLayoutPlaceholder", content);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to load template", e);        
        }
    }
	
	/**
	 * {@inheritDoc}
	 */
    public EnclosingView getEnclosingView(String thisViewName)
    {
        return new EnclosingView("CmsPage");
    }

}

