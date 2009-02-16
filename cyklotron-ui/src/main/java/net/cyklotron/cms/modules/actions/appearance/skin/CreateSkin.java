package net.cyklotron.cms.modules.actions.appearance.skin;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
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
import net.cyklotron.cms.skins.SkinResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CreateSkin.java,v 1.3 2005-03-09 09:58:31 pablo Exp $
 */
public class CreateSkin extends BaseAppearanceAction
{
    public CreateSkin(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FileSystem fileSystem,
        SkinService skinService, IntegrationService integrationService)
    {
        super(logger, structureService, cmsDataFactory, styleService, fileSystem, skinService,
                        integrationService);
    }
    
    /* overriden */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        String name = parameters.get("name");
        boolean copy = parameters.get("source","empty").
            equals("copy");
        String sourceSkin = parameters.get("source_skin");
        SiteResource site = getSite(context);
        Subject subject = coralSession.getUserSubject();
        try
        {
            if(name.length() == 0)
            {
                templatingContext.put("result", "empty_name");
            }
            else
            {
                if(skinService.hasSkin(coralSession, site, name))
                {
                    templatingContext.put("result","skin_exists");
                }
                else
                {
                    if(copy)
                    {
                        SkinResource source = skinService.getSkin(coralSession, site, sourceSkin);
                        skinService.createSkin(coralSession, site, name, source);      
                    }
                    else
                    {
                        skinService.createSkin(coralSession, site, name, null);
                    }
                }
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
        if(templatingContext.containsKey("result"))
        {
            mvcContext.setView("appearance.skin.CreateSkin");
        }
        else
        {
            templatingContext.put("result","added_successfully");
        }
    }
}
