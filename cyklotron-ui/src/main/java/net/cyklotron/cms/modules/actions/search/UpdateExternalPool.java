package net.cyklotron.cms.modules.actions.search;

import net.cyklotron.cms.search.ExternalPoolResource;
import net.cyklotron.cms.search.ExternalPoolResourceData;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * An action for index pool modification.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdateExternalPool.java,v 1.2 2005-01-24 10:27:13 pablo Exp $
 */
public class UpdateExternalPool extends BaseSearchAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();

        ExternalPoolResource pool = getExternalPool(data);

        ExternalPoolResourceData poolData = ExternalPoolResourceData.getData(data, pool);
        poolData.update(data);
        ExternalPoolResourceData.removeData(data, pool);
        
        pool.setDescription(poolData.getDescription());
        pool.setUrlTemplate(poolData.getUrlTemplate());

        pool.update(subject);
        
        try
        {
            mvcContext.setView("search,PoolList");
        }
        catch(NotFoundException e)
        {
            throw new ProcessingException("cannot redirect to pool list", e);
        }
        templatingContext.put("result","updated_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.search.external.pool.modify");
    }
}
