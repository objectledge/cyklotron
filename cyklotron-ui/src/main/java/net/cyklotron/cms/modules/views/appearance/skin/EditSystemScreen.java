package net.cyklotron.cms.modules.views.appearance.skin;

import java.io.StringReader;
import java.io.StringWriter;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.MergingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

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
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id$
 */
public class EditSystemScreen extends BaseAppearanceScreen
{
    
    public EditSystemScreen(org.objectledge.context.Context context, Logger logger,
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
        String screen = parameters.get("screen");
        templatingContext.put("skin", skin);
        templatingContext.put("screen", screen);
        SiteResource site = getSite();
        try
        {
            String contents =
                skinService.getSystemScreenTemplateContents(site, skin, screen);
            templatingContext.put("contents", contents);
            if(!templatingContext.containsKey("result"))
            {
                TemplatingContext blankContext = templating.createContext();
                StringReader in = new StringReader(contents);
                StringWriter out = new StringWriter();
                try
                {
                    templating.merge(
                        blankContext,
                        in,
                        out,
                        "<system screen template>");
                }
                catch (MergingException e)
                {
                    templatingContext.put("result", "template_parse_error");
                    templatingContext.put(
                        "parse_trace", new StackTrace(e));
                }
            }
        }
        catch (Exception e)
        {
            throw new ProcessingException("failed to load file contents", e);
        }
    }
}
