package net.cyklotron.cms.modules.views.appearance.style;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.style.LayoutResource;
import net.cyklotron.cms.style.LevelResource;
import net.cyklotron.cms.style.StyleResource;
import net.cyklotron.cms.style.StyleResourceImpl;
import net.cyklotron.cms.style.StyleService;

/**
 *
 */
public class EditStyle
    extends BaseAppearanceScreen
{
    public EditStyle(org.objectledge.context.Context context, Logger logger,
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
        long styleId = parameters.getLong("style_id", -1);
        if(styleId == -1)
        {
            throw new ProcessingException("style id couldn't be found");
        }
        StyleResource style = null;
        SiteResource site = getSite();
        try
        {
            style = StyleResourceImpl.getStyleResource(coralSession,styleId);
            templatingContext.put("style", style);
            List layoutList = Arrays.asList(styleService.getLayouts(coralSession, site));
            Collections.sort(layoutList, new Comparator()
                {
                    public int compare(Object o1, Object o2)
                    {
                        LayoutResource l1 = (LayoutResource)o1;
                        LayoutResource l2 = (LayoutResource)o2;
                        return l1.getName().compareTo(l2.getName());
                    }
                }
            );
            templatingContext.put("layouts", layoutList);
        }
        catch (Exception e)
        {
            throw new ProcessingException("failed to load style data", e);
        }

        LevelResource[] levels = styleService.getLevels(coralSession, style);
        int max = getMaximumLevel(levels);
        SortedMap levelMap = new TreeMap();
        if(!parameters.isDefined("level_count"))
        {
            // it's first appearance of the screen, init map from
            // current settings for the style
            outer: for(int i=0; i<=max; i++)
            {
                String istr = Integer.toString(i);
                for(int j=0; j<levels.length; j++)
                {
                    LevelResource l = levels[j];
                    if(l.getName().equals(istr))
                    {
                        if(l.getLayout() != null)
                        {
                            levelMap.put(new Integer(i), l.getLayout().getIdObject());
                        }
                        else
                        {
                            levelMap.put(new Integer(i), new Long(-1));
                        }
                        continue outer;
                    }
                }
                levelMap.put(new Integer(i), new Long(0));
            }
            if(levelMap.isEmpty())
            {
                levelMap.put(new Integer(0), new Long(-1));
            }
            templatingContext.put("orig_level_count", new Integer(levelMap.size()));
        }
        else
        {
            // the screen has been shown before, take user's input into
            // account
            int levelCount = parameters.getInt("level_count");
            for(int i=0; i<levelCount; i++)
            {
                levelMap.put(new Integer(i), new Long(parameters.
                                                      getLong("level_"+i)));
            }
            templatingContext.put("orig_level_count", parameters.
                        get("orig_level_count"));
        }
        templatingContext.put("levels", levelMap);
        templatingContext.put("ZERO", new Long(0));
        templatingContext.put("MINUS_ONE", new Long(-1));

        // cliboard support
        Long clipId = (Long)httpContext.getSessionAttribute(CLIPBOARD_STYLE_KEY);
        if(clipId != null)
        {
            try
            {
                Resource resource = coralSession.getStore().getResource(clipId.longValue());
                templatingContext.put("clipboard","true");
                templatingContext.put("clipboard_style",resource);
                templatingContext.put("clipboard_mode",httpContext.getSessionAttribute(CLIPBOARD_STYLE_MODE));
            }
            catch (EntityDoesNotExistException e)
            {
                logger.error("Resource Exception :",e);
                throw new ProcessingException("resource doesn't exist",e);
            }
        }
        else
        {
            templatingContext.put("clipboard","false");
        }
    }

    private int getMaximumLevel(LevelResource[] levels)
    {
        int level = MINIMAL_LEVEL;
        for(int i = 0; i < levels.length; i++)
        {
            int l = Integer.parseInt(levels[i].getName());
            if(l > level)
            {
                level = l;
            }
        }
        return level;
    }
}
