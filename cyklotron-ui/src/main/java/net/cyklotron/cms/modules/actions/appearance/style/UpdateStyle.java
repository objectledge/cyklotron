package net.cyklotron.cms.modules.actions.appearance.style;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;
import net.cyklotron.cms.style.LayoutResource;
import net.cyklotron.cms.style.LayoutResourceImpl;
import net.cyklotron.cms.style.LevelResource;
import net.cyklotron.cms.style.StyleResource;
import net.cyklotron.cms.style.StyleResourceImpl;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdateStyle.java,v 1.1 2005-01-24 04:34:43 pablo Exp $
 */
public class UpdateStyle
    extends BaseAppearanceAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();
        String name = parameters.get("name","");
        String description = parameters.get("description","");
        if(name.equals(""))
        {
            templatingContext.put("result","navi_name_empty");
            return;
        }
        long styleId = parameters.getLong("style_id", -1);
        if (styleId == -1)
        {
            throw new ProcessingException("style id could not be found");
        }
        StyleResource style = null;
        try
        {
            style = StyleResourceImpl.getStyleResource(coralSession,styleId);
            Resource parent = style.getParent();
            if(parent instanceof StyleResource)
            {
                styleService.updateStyle(style, name, description, (StyleResource)parent, subject);
            }
            else
            {
                styleService.updateStyle(style, name, description, null, subject);
            }

            int origLevelCount = parameters.getInt("orig_level_count");
            int levelCount = parameters.getInt("level_count");
            if(levelCount < origLevelCount)
            {
                // levels have been deleted
                levelCount = origLevelCount;
            }
            for(int i = 0; i<levelCount; i++)
            {
                LevelResource level = styleService.getLevel(style,i);
                long layoutId = parameters.getLong("level_"+i, 0);
                if(layoutId == -1)
                {
                    if(level == null)
                    {
                        styleService.addLevel(style, null, i, "level_"+i, subject);
                    }
                    else
                    {
                        if(level.getLayout() != null)
                        {
                            level.setLayout(null);
                            level.update(subject);
                        }
                    }
                }
                if(layoutId == 0)
                {
                    if(level != null)
                    {
                        styleService.deleteLevel(level,subject);
                    }
                }
                if(layoutId > 0)
                {
                    LayoutResource layout = LayoutResourceImpl.
                        getLayoutResource(coralSession,layoutId);
                    if(level == null)
                    {
                        styleService.addLevel(style, layout, i, "level_"+i, subject);
                    }
                    else
                    {
                        level.setLayout(layout);
                        level.update(subject);
                    }
                }
            }
        }
        catch(Exception e)
        {
            if(e instanceof ProcessingException)
            {
                throw (ProcessingException)e;
            }
            templatingContext.put("result","exception");
            log.error("failed to update style",e);
            templatingContext.put("trace", StringUtils.stackTrace(e));
            return;
        }
        templatingContext.put("result","updated_successfully");
    }
}
