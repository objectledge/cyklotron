package net.cyklotron.cms.modules.actions.appearance.style;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.LayoutResource;
import net.cyklotron.cms.style.LayoutResourceImpl;
import net.cyklotron.cms.style.LevelResource;
import net.cyklotron.cms.style.StyleResource;
import net.cyklotron.cms.style.StyleResourceImpl;
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdateStyle.java,v 1.2 2005-01-24 10:27:39 pablo Exp $
 */
public class UpdateStyle
    extends BaseAppearanceAction
{
    
    public UpdateStyle(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FileSystem fileSystem,
        SkinService skinService, IntegrationService integrationService)
    {
        super(logger, structureService, cmsDataFactory, styleService, fileSystem, skinService,
                        integrationService);
        // TODO Auto-generated constructor stub
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
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
                styleService.updateStyle(coralSession, style, name, description, (StyleResource)parent);            }
            else
            {
                styleService.updateStyle(coralSession, style, name, description, null);
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
                LevelResource level = styleService.getLevel(coralSession, style,i);
                long layoutId = parameters.getLong("level_"+i, 0);
                if(layoutId == -1)
                {
                    if(level == null)
                    {
                        styleService.addLevel(coralSession, style, null, i, "level_"+i);
                    }
                    else
                    {
                        if(level.getLayout() != null)
                        {
                            level.setLayout(null);
                            level.update();
                        }
                    }
                }
                if(layoutId == 0)
                {
                    if(level != null)
                    {
                        styleService.deleteLevel(coralSession, level);
                    }
                }
                if(layoutId > 0)
                {
                    LayoutResource layout = LayoutResourceImpl.
                        getLayoutResource(coralSession,layoutId);
                    if(level == null)
                    {
                        styleService.addLevel(coralSession, style, layout, i, "level_"+i);
                    }
                    else
                    {
                        level.setLayout(layout);
                        level.update();
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
            templatingContext.put("trace", new StackTrace(e));
            return;
        }
        templatingContext.put("result","updated_successfully");
    }
}
