package net.cyklotron.cms.modules.actions.appearance.style;

import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;
import net.cyklotron.cms.style.StyleResource;
import net.cyklotron.cms.style.StyleResourceImpl;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeleteStyle.java,v 1.1 2005-01-24 04:34:43 pablo Exp $
 */
public class DeleteStyle
    extends BaseAppearanceAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        long styleId = parameters.getLong("style_id", -1);
        if (styleId == -1)
        {
            throw new ProcessingException("style id could not be found");
        }
        try
        {
            StyleResource style = StyleResourceImpl.getStyleResource(coralSession,styleId);
            styleService.deleteStyle(style, coralSession.getUserSubject());
            parameters.remove("style_id");
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            log.error("failed to delete style", e);
            templatingContext.put("trace", StringUtils.stackTrace(e));
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }
}
