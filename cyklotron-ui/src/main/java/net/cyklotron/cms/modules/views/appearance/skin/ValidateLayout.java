package net.cyklotron.cms.modules.views.appearance.skin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.style.ComponentSocketResource;
import net.cyklotron.cms.style.LayoutResource;
import net.cyklotron.cms.style.StyleException;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: ValidateLayout.java,v 1.2 2005-01-25 11:23:41 pablo Exp $
 */
public class ValidateLayout extends BaseAppearanceScreen
{
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
                templatingContext.put("parse_trace", StringUtils.stackTrace(e.getRootCause()));
            }
            if(templateSockets != null)
            {
                LayoutResource layoutRes = styleService.getLayout(site, layout);
                ComponentSocketResource[] layoutSockets = styleService.getSockets(layoutRes); 
                
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
