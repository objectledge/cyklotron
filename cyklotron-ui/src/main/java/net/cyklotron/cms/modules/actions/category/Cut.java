package net.cyklotron.cms.modules.actions.category;

import org.objectledge.pipeline.ProcessingException;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: Cut.java,v 1.1 2005-01-24 04:33:58 pablo Exp $
 */
public class Cut
    extends BaseCopyPasteAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        long id = parameters.getLong("cat_id");
        httpContext.setSessionAttribute(CLIPBOARD_MODE,"cut");
        httpContext.setSessionAttribute(CLIPBOARD_KEY,new Long(id));
    }
}
