package net.cyklotron.cms.modules.actions.category.query;

import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryResourceData;
import net.cyklotron.cms.site.SiteResource;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Category query update action.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryUpdate.java,v 1.2 2005-01-24 10:27:21 pablo Exp $
 */
public class CategoryQueryUpdate
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

        CategoryQueryResource query = getQuery(data);
		CategoryQueryResourceData queryData = CategoryQueryResourceData.getData(data, query);
        queryData.update(data);
        
        if(queryData.getName().equals(""))
        {
            templatingContext.put("result", "name_empty");
            return;
        }
        
        SiteResource site = getSite(context);
        try
        {
            if(!queryData.getName().equals(query.getName()))
            {
                Resource parent = query.getParent(); 
                if(coralSession.getStore().getResource(parent, queryData.getName()).length > 0)
                {
                    templatingContext.put("result","duplicate_query_name");
                    return;
                }
                coralSession.getStore().setName(query, queryData.getName());
            }

			updateQuery(query, queryData, subject);
		}
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace", new StackTrace(e));
            log.error("problem saving a category query for site '"+site.getName()+"'", e);
            return;
        }

		CategoryQueryResourceData.removeData(data, query);
        try
        {
            mvcContext.setView("category,query,CategoryQueryList");
        }
        catch(NotFoundException e)
        {
            throw new ProcessingException("cannot redirect to query list", e);
        }
        templatingContext.put("result","saved_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.category.query.modify");
    }
}
