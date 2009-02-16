package net.cyklotron.cms.modules.actions.appearance.style;

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
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: PasteStyle.java,v 1.3 2005-03-08 10:51:03 pablo Exp $
 */
public class PasteStyle
    extends BaseAppearanceAction
{
    
    
    public PasteStyle(Logger logger, StructureService structureService,
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
        // Logger log = ((LoggingService)data.getBroker().getService(LoggingService.SERVICE_NAME)).getFacility("cms.appearance");
        long targetId = parameters.getLong("style_id", -1);
        if(targetId == -1)
        {
            templatingContext.put("result","parameter_not_found");
            return;
        }
        Long nodeId = (Long)httpContext.getSessionAttribute(CLIPBOARD_STYLE_KEY);
        String mode = (String)httpContext.getSessionAttribute(CLIPBOARD_STYLE_MODE);
        if(nodeId == null || mode == null)
        {
            templatingContext.put("result","clipboard_empty");
            return;
        }
        if(mode.equals("cut"))
        {
            /*
            try
            {
                ls.moveStyle(nodeId.longValue(), targetId, subject);
            }
            catch(StructureException e)
            {
                throw new ProcessingException("StructureException occured",e);
            }
            */
        }
        else
        {
            // TO DO ....if anybody decide to use it
            throw new UnsupportedOperationException("not implemented yet");
        }
    }
}

