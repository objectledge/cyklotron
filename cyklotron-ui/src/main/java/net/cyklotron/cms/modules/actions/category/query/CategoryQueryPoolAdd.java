package net.cyklotron.cms.modules.actions.category.query;

import java.util.ArrayList;
import java.util.List;

import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryPoolResourceData;
import net.cyklotron.cms.category.query.CategoryQueryPoolResourceImpl;
import net.cyklotron.cms.site.SiteResource;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Category query pool adding action.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryPoolAdd.java,v 1.1 2005-01-24 04:34:35 pablo Exp $
 */
public class CategoryQueryPoolAdd
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

		CategoryQueryPoolResourceData poolData = CategoryQueryPoolResourceData.getData(data, null);
        poolData.update(data);
        
        if(poolData.getName().equals(""))
        {
            templatingContext.put("result", "name_empty");
            return;
        }
        
        SiteResource site = getSite(context);
        try
        {
			Resource root = categoryQueryService.getCategoryQueryPoolRoot(site);

            if(coralSession.getStore().getResource(root, poolData.getName()).length > 0)
            {
                templatingContext.put("result","cannot_add_pools_with_the_same_name");
                return;
            }
            
			CategoryQueryPoolResource pool = CategoryQueryPoolResourceImpl
                .createCategoryQueryPoolResource(coralSession, poolData.getName(), root, subject);
            
            pool.setDescription(poolData.getDescription());
            // set pool queries
            List newQueries = new ArrayList(poolData.getQueriesSelectionState()
                .getResources(coralSession, "selected").keySet());
            pool.setQueries(newQueries);
            
            pool.update(subject);
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
            log.error("problem adding a category query pool for site '"+site.getName()+"'", e);
            return;
        }

		CategoryQueryPoolResourceData.removeData(data, null);
        try
        {
            data.setView("category,query,CategoryQueryPoolList");
        }
        catch(NotFoundException e)
        {
            throw new ProcessingException("cannot redirect to pool list", e);
        }
        templatingContext.put("result","added_successfully");
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.category.query.pool.add");
    }
}
