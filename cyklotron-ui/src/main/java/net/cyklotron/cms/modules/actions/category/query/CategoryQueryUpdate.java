package net.cyklotron.cms.modules.actions.category.query;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryResourceData;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 * Category query update action.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryUpdate.java,v 1.3 2005-01-25 03:22:19 pablo Exp $
 */
public class CategoryQueryUpdate
    extends BaseCategoryQueryAddUpdate
{
    
    
    public CategoryQueryUpdate(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, CategoryQueryService categoryQueryService,
        CategoryService categoryService, SiteService siteService)
    {
        super(logger, structureService, cmsDataFactory, categoryQueryService, categoryService,
                        siteService);
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();

        CategoryQueryResource query = getQuery(coralSession, parameters);
		CategoryQueryResourceData queryData = CategoryQueryResourceData.getData(httpContext, query);
        queryData.update(parameters);
        
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

			updateQuery(query, queryData, coralSession);
		}
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace", new StackTrace(e));
            logger.error("problem saving a category query for site '"+site.getName()+"'", e);
            return;
        }

		CategoryQueryResourceData.removeData(httpContext, query);
        mvcContext.setView("category,query,CategoryQueryList");
        templatingContext.put("result","saved_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.category.query.modify");
    }
}
