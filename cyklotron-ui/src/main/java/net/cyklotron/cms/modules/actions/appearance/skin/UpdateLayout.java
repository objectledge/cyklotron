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
import net.cyklotron.cms.style.LayoutResource;
import net.cyklotron.cms.style.StyleException;
import net.cyklotron.cms.style.StyleService;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: UpdateLayout.java,v 1.3 2005-03-08 10:50:47 pablo Exp $
 */
public class UpdateLayout extends BaseAppearanceAction
{
    
    
    public UpdateLayout(Logger logger, StructureService structureService,
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
        String layout = parameters.get("layout");
        String skin = parameters.get("skin");
        String contents = parameters.get("contents");
        SiteResource site = getSite(context);
        try
        {
            skinService.setLayoutTemplateContents(getSite(context), skin, layout, contents);
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
                if(!styleService.matchSockets(coralSession, layoutRes, templateSockets))
                {
                    templatingContext.put("result", "template_saved_sockets_mismatch");
                    mvcContext.setView("appearance,skin,ValidateLayout");
                    return;
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
            mvcContext.setView("appearance,skin,EditLayout");
        }
        else
        {
            templatingContext.put("result","updated_successfully");
        }
    }
}
