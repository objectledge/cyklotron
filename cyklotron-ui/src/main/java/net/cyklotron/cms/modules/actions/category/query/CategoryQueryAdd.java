package net.cyklotron.cms.modules.actions.category.query;

import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryResourceData;
import net.cyklotron.cms.category.query.CategoryQueryResourceImpl;
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
 * @version $Id: CategoryQueryAdd.java,v 1.1 2005-01-24 04:34:35 pablo Exp $
 */
public class CategoryQueryAdd
    extends BaseCategoryQueryAddUpdate
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();

		CategoryQueryResourceData queryData = CategoryQueryResourceData.getData(data, null);
        queryData.update(data);
        
        if(queryData.getName().equals(""))
        {
            templatingContext.put("result", "name_empty");
            return;
        }
        
        SiteResource site = getSite(context);
        try
        {
			Resource root = categoryQueryService.getCategoryQueryRoot(site);

            if(coralSession.getStore().getResource(root, queryData.getName()).length > 0)
            {
                templatingContext.put("result","duplicate_query_name");
                return;
            }
            
			CategoryQueryResource query = CategoryQueryResourceImpl
                .createCategoryQueryResource(coralSession, queryData.getName(), root, subject);
            
			updateQuery(query, queryData, subject);
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
            log.error("problem adding a category query for site '"+site.getName()+"'", e);
            return;
        }

		CategoryQueryResourceData.removeData(data, null);
        try
        {
            data.setView("category,query,CategoryQueryList");
        }
        catch(NotFoundException e)
        {
            throw new ProcessingException("cannot redirect to query list", e);
        }
        templatingContext.put("result","added_successfully");
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.category.query.add");
    }
}
