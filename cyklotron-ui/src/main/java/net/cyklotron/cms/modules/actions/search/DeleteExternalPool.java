package net.cyklotron.cms.modules.actions.search;

import net.cyklotron.cms.search.ExternalPoolResource;
import net.labeo.services.resource.EntityInUseException;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: DeleteExternalPool.java,v 1.2 2005-01-24 10:27:13 pablo Exp $
 */
public class DeleteExternalPool extends BaseSearchAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();

        ExternalPoolResource pool = getExternalPool(data);
        try
        {
            coralSession.getStore().deleteResource(pool);
        }
        catch(EntityInUseException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace", new StackTrace(e));
            log.error("problem deleting the external search pool '"+pool.getIdString()+"'", e);
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.search.external.pool.delete");
    }
}
