package net.cyklotron.cms.modules.actions.appearance.style;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: PasteStyle.java,v 1.1 2005-01-24 04:34:43 pablo Exp $
 */
public class PasteStyle
    extends BaseAppearanceAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
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

