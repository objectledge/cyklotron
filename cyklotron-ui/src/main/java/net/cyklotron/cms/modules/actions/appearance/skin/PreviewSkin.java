package net.cyklotron.cms.modules.actions.appearance.skin;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

public class PreviewSkin
    extends BaseAppearanceAction
{
    public PreviewSkin(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FileSystem fileSystem,
        SkinService skinService, IntegrationService integrationService)
    {
        super(logger, structureService, cmsDataFactory, styleService, fileSystem, skinService,
                        integrationService);
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            String selected = parameters.get("selected","");
            SiteResource site = getSite(context);
            String key = SkinService.PREVIEW_KEY_PREFIX + site.getName();
            if(selected.length() == 0)
            {
                httpContext.removeSessionAttribute(key);
            }
            else
            {
                httpContext.setSessionAttribute(key, selected);
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to setup skin preview", e);
        }
    }
}
