package net.cyklotron.cms.modules.actions.search;

import java.util.ArrayList;
import java.util.List;

import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.search.PoolResource;
import net.cyklotron.cms.search.PoolResourceData;

/**
 * An action for index pool modification.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdatePool.java,v 1.2 2005-01-24 10:27:13 pablo Exp $
 */
public class UpdatePool extends BaseSearchAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();

        PoolResource pool = getPool(data);

        PoolResourceData poolData = PoolResourceData.getData(data, pool);
        poolData.update(data);
        PoolResourceData.removeData(data, pool);
        
        pool.setDescription(poolData.getDescription());

        // set pool indexes
        List newIndexes = new ArrayList(poolData.getIndexesSelectionState()
            .getResources(coralSession, "selected").keySet());
        pool.setIndexes(newIndexes);

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
        return checkPermission(context, coralSession, "cms.search.pool.modify");
    }
}
