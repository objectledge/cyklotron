package net.cyklotron.cms.modules.actions.structure;

import org.objectledge.pipeline.ProcessingException;

/**
 * Copy action.
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Copy.java,v 1.1 2005-01-24 04:33:55 pablo Exp $
 */
public class Copy
    extends BaseCopyPasteAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        long id = parameters.getLong("node_id");
        httpContext.setSessionAttribute(CLIPBOARD_MODE,"copy");
        httpContext.setSessionAttribute(CLIPBOARD_KEY,new Long(id));
    }
}
