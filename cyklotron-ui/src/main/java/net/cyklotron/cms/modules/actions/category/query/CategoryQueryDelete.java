package net.cyklotron.cms.modules.actions.category.query;

import net.labeo.services.resource.EntityInUseException;
import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.category.query.CategoryQueryException;
import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.site.SiteResource;

/**
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryDelete.java,v 1.1 2005-01-24 04:34:35 pablo Exp $
 */
public class CategoryQueryDelete
	extends BaseCategoryQueryAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();

        CategoryQueryResource query = getQuery(data);

        SiteResource site = getSite(context);
        Resource pools[];
        try
        {
            Resource poolRoot = categoryQueryService.getCategoryQueryPoolRoot(site);
            pools = coralSession.getStore().getResource(poolRoot);
        }
        catch (CategoryQueryException e)
        {
            throw new ProcessingException("failed to lookup query pool root", e);
        }
        for(int i=0; i<pools.length; i++)
        {
            CategoryQueryPoolResource pool = (CategoryQueryPoolResource)pools[i];
            if(pool.getQueries().contains(query))
            {
                try
                {
                    data.setView("category,query,CategoryQueryInUse");
                    return;
                }
                catch(NotFoundException e)
                {
                    throw new ProcessingException("redirect failed", e);
                }
            }
        }

        try
        {
            coralSession.getStore().deleteResource(query);
        }
        catch(EntityInUseException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
            log.error("problem deleting the category query '"+query.getIdString()+"'", e);
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.category.query.delete");
    }
}
