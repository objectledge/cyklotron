package net.cyklotron.cms.modules.views.appearance.skin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
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
import net.cyklotron.cms.style.ComponentSocketResource;
import net.cyklotron.cms.style.LayoutResource;
import net.cyklotron.cms.style.StyleException;
import net.cyklotron.cms.style.StyleService;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ValidateLayout.java,v 1.4 2005-03-08 10:57:43 pablo Exp $
 */
public class ValidateLayout extends BaseAppearanceScreen
{
    
    public ValidateLayout(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StyleService styleService, SkinService skinService,
        IntegrationService integrationService, Templating templating)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, styleService,
                        skinService, integrationService, templating);
        
    }
    /* overriden */
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        String skin = parameters.get("skin");
        String layout = parameters.get("layout");
        templatingContext.put("skin", skin);
        templatingContext.put("layout", layout);
        SiteResource site = getSite();
        try
        {
            String contents = skinService.getLayoutTemplateContents(site, skin, layout);
            String[] templateSockets = null;
            try
            {
                templateSockets = styleService.findSockets(contents);
            }
            catch(StyleException e)
            {
                templatingContext.put("result", "template_saved_parse_error");
                templatingContext.put("parse_trace", new StackTrace(e));
            }
            if(templateSockets != null)
            {
                LayoutResource layoutRes = styleService.getLayout(coralSession, site, layout);
                ComponentSocketResource[] layoutSockets = styleService.getSockets(coralSession, layoutRes); 
                
                List sockets = new ArrayList(templateSockets.length > 
                    layoutSockets.length ? templateSockets.length :
                    layoutSockets.length);
                for (int i = 0; i < templateSockets.length; i++)
                {
                    add(sockets, templateSockets[i], "template");
                }
                for (int i = 0; i < layoutSockets.length; i++)
                {
                    add(sockets, layoutSockets[i].getName(), "layout");
                }
                templatingContext.put("sockets", sockets);
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to load file contents", e);
        }
    }
    
    private void add(List sockets, String name, String location)
    {
        int i=0;
        for(i=0; i<sockets.size(); i++)
        {
            Map item = (Map)sockets.get(i);
            String itemName = (String)item.get("name");
            if(itemName.equals(name))
            {
                item.put(location, Boolean.TRUE);
                return;
            }
            if(itemName.compareTo(name) > 0)
            {
                break;
            }
        }
        Map item = new HashMap();
        item.put("name", name);
        item.put(location, Boolean.TRUE);
        sockets.add(i, item);
    }
}
