package net.cyklotron.cms.modules.actions.category.query;

import java.util.ArrayList;

import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.labeo.services.resource.EntityInUseException;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryPoolDelete.java,v 1.2 2005-01-24 10:27:21 pablo Exp $
 */
public class CategoryQueryPoolDelete
	extends BaseCategoryQueryAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
		Subject subject = coralSession.getUserSubject();
        Context context = data.getContext();

        CategoryQueryPoolResource pool = getPool(data);
        // remove query references
		pool.setQueries(new ArrayList());
		pool.update(subject);

        try
        {
            coralSession.getStore().deleteResource(pool);
        }
        catch(EntityInUseException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace", new StackTrace(e));
            log.error("problem deleting the category query pool '"+pool.getIdString()+"'", e);
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.category.query.pool.delete");
    }
}
