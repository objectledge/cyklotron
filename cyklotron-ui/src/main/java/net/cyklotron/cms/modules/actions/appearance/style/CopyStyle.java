package net.cyklotron.cms.modules.actions.appearance.style;

import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;

import org.objectledge.pipeline.ProcessingException;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: CopyStyle.java,v 1.1 2005-01-24 04:34:43 pablo Exp $
 */
public class CopyStyle
    extends BaseAppearanceAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        long id = parameters.getLong("style_id");
        httpContext.setSessionAttribute(CLIPBOARD_STYLE_MODE,"copy");
        httpContext.setSessionAttribute(CLIPBOARD_STYLE_KEY,new Long(id));
    }
}

