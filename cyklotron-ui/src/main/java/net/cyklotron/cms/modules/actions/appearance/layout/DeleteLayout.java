package net.cyklotron.cms.modules.actions.appearance.layout;

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
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.ComponentSocketResource;
import net.cyklotron.cms.style.LayoutResource;
import net.cyklotron.cms.style.LayoutResourceImpl;
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeleteLayout.java,v 1.2 2005-01-24 10:27:10 pablo Exp $
 */
public class DeleteLayout
    extends BaseAppearanceAction
{
    
    
    public DeleteLayout(Logger logger, StructureService structureService,
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
        long layoutId = parameters.getLong("layout_id", -1);
        if (layoutId == -1)
        {
            throw new ProcessingException("layout id could not be found");
        }
        try
        {
            LayoutResource layout = LayoutResourceImpl.getLayoutResource(coralSession,layoutId);
            // delete all sockets
            ComponentSocketResource[] sockets =
                styleService.getSockets(coralSession, layout);
            for(int i=0; i<sockets.length; i++)
            {
                styleService.deleteSocket(coralSession, layout, sockets[i].getName());
            }
            styleService.deleteLayout(coralSession, layout);
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            log.error("failed to delete layout", e);
            templatingContext.put("trace", new StackTrace(e));
            return;
        }
        parameters.remove("layout_id");
        templatingContext.put("result","deleted_successfully");
    }
}
