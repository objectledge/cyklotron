package net.cyklotron.cms.modules.actions.category;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: Paste.java,v 1.3 2005-03-08 10:51:31 pablo Exp $
 */
public class Paste
    extends BaseCopyPasteAction
{
    
    
    public Paste(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory,
        CategoryService categoryService, IntegrationService integrationService)
    {
        super(logger, structureService, cmsDataFactory, categoryService, integrationService);
        
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        // CoralSession rs = (CoralSession)data.getBroker().getService(CoralSession.SERVICE_NAME);
        // Logger log = ((LoggingService)data.getBroker().getService(LoggingService.SERVICE_NAME)).getFacility("cms.structure");
        long id = parameters.getLong("cat_id", -1);
        if(id == -1)
        {
            templatingContext.put("result","parameter_not_found");
            return;
        }
        Long nodeId = (Long)httpContext.getSessionAttribute(CLIPBOARD_KEY);
        String mode = (String)httpContext.getSessionAttribute(CLIPBOARD_MODE);
        if(nodeId == null || mode == null)
        {
            templatingContext.put("result","clipboard_empty");
            return;
        }
        // TODO ....
//        try
//        {
//             Resource parent = rs.getStore().getResource(id);
//             Resource node = rs.getStore().getResource(nodeId.longValue());
//             Subject subject = coralSession.getUserSubject();
//        }
//        catch(EntityDoesNotExistException e)
//        {
//            log.error("Exception ",e);
//        }
        throw new UnsupportedOperationException("not implemented yet");        
    }
}

