package net.cyklotron.cms.modules.actions.appearance.layout;

import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;
import net.cyklotron.cms.style.ComponentSocketResource;
import net.cyklotron.cms.style.LayoutResource;
import net.cyklotron.cms.style.LayoutResourceImpl;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeleteLayout.java,v 1.1 2005-01-24 04:34:02 pablo Exp $
 */
public class DeleteLayout
    extends BaseAppearanceAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
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
                styleService.getSockets(layout);
            for(int i=0; i<sockets.length; i++)
            {
                styleService.deleteSocket(layout, sockets[i].getName(), coralSession.getUserSubject());
            }
            styleService.deleteLayout(layout, coralSession.getUserSubject());
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            log.error("failed to delete layout", e);
            templatingContext.put("trace", StringUtils.stackTrace(e));
            return;
        }
        parameters.remove("layout_id");
        templatingContext.put("result","deleted_successfully");
    }
}
