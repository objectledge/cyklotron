package net.cyklotron.cms.modules.actions.appearance.skin;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
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
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CreateDirectory.java,v 1.2 2005-01-24 10:27:07 pablo Exp $
 */
public class CreateDirectory extends BaseAppearanceAction
{
    
    public CreateDirectory(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FileSystem fileSystem,
        SkinService skinService, IntegrationService integrationService)
    {
        super(logger, structureService, cmsDataFactory, styleService, fileSystem, skinService,
                        integrationService);
        // TODO Auto-generated constructor stub
    }
    /* overriden */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        String path = parameters.get("path");
        String name = parameters.get("name");
        String skin = parameters.get("skin");
        path = path.replace(',', '/') + name;
        SiteResource site = getSite(context);
        if(name.length() == 0)
        {
            templatingContext.put("result","empty_name");
        }
        else
        {
            try
            {
                if(skinService.contentItemExists(site, skin, path))
                {   
                    templatingContext.put("result","file_or_directory_exists");
                }
                else
                {
                    skinService.createContentDirectory(site, skin, path);
                }
            }
            catch(Exception e)
            {
                templatingContext.put("result", "exception");
                templatingContext.put("trace", new StackTrace(e));
            }
        }
        if(templatingContext.containsKey("result"))
        {
            mvcContext.setView("appearance,skin,CreateDirectory");
        }
        else
        {
            templatingContext.put("result","directory_created");
        }
    }
}
