package net.cyklotron.cms.modules.actions.category;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: Paste.java,v 1.1 2005-01-24 04:33:58 pablo Exp $
 */
public class Paste
    extends BaseCopyPasteAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
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

