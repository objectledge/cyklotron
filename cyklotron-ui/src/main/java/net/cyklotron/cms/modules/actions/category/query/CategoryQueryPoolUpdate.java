package net.cyklotron.cms.modules.actions.category.query;

import java.util.ArrayList;
import java.util.List;

import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryPoolResourceData;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * An action for index pool modification.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryPoolUpdate.java,v 1.1 2005-01-24 04:34:35 pablo Exp $
 */
public class CategoryQueryPoolUpdate
	extends BaseCategoryQueryAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();

        CategoryQueryPoolResource pool = getPool(data);

        CategoryQueryPoolResourceData poolData = CategoryQueryPoolResourceData.getData(data, pool);
        poolData.update(data);
       
		if(poolData.getName().equals(""))
		{
			templatingContext.put("result", "name_empty");
			return;
		}

		if(!poolData.getName().equals(pool.getName()))
		{
			Resource parent = pool.getParent(); 
			if(coralSession.getStore().getResource(parent, poolData.getName()).length > 0)
			{
				templatingContext.put("result","cannot_have_the_same_name_as_other");
				return;
			}
			coralSession.getStore().setName(pool, poolData.getName());
		}
       
        pool.setDescription(poolData.getDescription());

        // set pool indexes
        List newQueries = new ArrayList(poolData.getQueriesSelectionState()
            .getResources(coralSession, "selected").keySet());
        pool.setQueries(newQueries);

        pool.update(subject);
        
		CategoryQueryPoolResourceData.removeData(data, pool);
        try
        {
            data.setView("category,query,CategoryQueryPoolList");
        }
        catch(NotFoundException e)
        {
            throw new ProcessingException("cannot redirect to pool list", e);
        }
        templatingContext.put("result","updated_successfully");
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.category.query.pool.modify");
    }
}
