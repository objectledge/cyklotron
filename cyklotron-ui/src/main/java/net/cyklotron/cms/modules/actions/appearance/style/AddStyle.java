package net.cyklotron.cms.modules.actions.appearance.style;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
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
import net.cyklotron.cms.style.StyleResource;
import net.cyklotron.cms.style.StyleResourceImpl;
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: AddStyle.java,v 1.3 2005-03-08 10:51:03 pablo Exp $
 */
public class AddStyle
    extends BaseAppearanceAction
{
    
    public AddStyle(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FileSystem fileSystem,
        SkinService skinService, IntegrationService integrationService)
    {
        super(logger, structureService, cmsDataFactory, styleService, fileSystem, skinService,
                        integrationService);
        
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        String name = parameters.get("name","");
        String description = parameters.get("description","");
        if(name.equals(""))
        {
            templatingContext.put("result", "required_field_missing");
            mvcContext.setView("appearance,AddStyle");
            return;
        }
        long parentId = parameters.getLong("style_id", -1);
        if (parentId == -1)
        {
            throw new ProcessingException("style id could not be found");
        }
        StyleResource parent = null;

        SiteResource site = getSite(context);
        try
        {
            parent = StyleResourceImpl.getStyleResource(coralSession,parentId);
        }
        catch (EntityDoesNotExistException e)
        {
            throw new ProcessingException("Style resource doesn't exist",e);
        }
        try
        {
            StyleResource style = styleService.
                addStyle(coralSession, name, description, site, parent);
            parameters.set("style_id", style.getIdString());
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            log.error("failed to add style", e);
            templatingContext.put("trace", new StackTrace(e));
            return;
        }
        templatingContext.put("result","added_successfully");
    }
}
