package net.cyklotron.cms.modules.actions.category.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.datatypes.ResourceList;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryPoolResourceData;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 * An action for index pool modification.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryPoolUpdate.java,v 1.8 2007-10-24 23:27:23 rafal Exp $
 */
public class CategoryQueryPoolUpdate
	extends BaseCategoryQueryAction
{
    private final CoralSessionFactory coralSessionFactory;

    public CategoryQueryPoolUpdate(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, CategoryQueryService categoryQueryService,
        CategoryService categoryService, SiteService siteService, 
        CoralSessionFactory coralSessionFactory)
    {
        super(logger, structureService, cmsDataFactory, categoryQueryService, categoryService,
                        siteService);
        
        this.coralSessionFactory = coralSessionFactory;
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();

        CategoryQueryPoolResource pool = getPool(coralSession, parameters);

        CategoryQueryPoolResourceData poolData = CategoryQueryPoolResourceData.getData(httpContext, pool);
        poolData.update(parameters);
       
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
			try
            {
                coralSession.getStore().setName(pool, poolData.getName());
            }
            catch(InvalidResourceNameException e)
            {
                templatingContext.put("result", "invalid_name");
                return;                
            }
		}
       
        pool.setDescription(poolData.getDescription());

        // set pool indexes
        Set newQueries = poolData.getQueriesSelectionState().getEntities(coralSession, "selected")
            .keySet();
        ResourceList queries = pool.getQueries();
        
        List toAdd = new ArrayList(newQueries);
        toAdd.removeAll(queries);
        // sort newly added items by name
        I18nContext i18nContext = context.getAttribute(I18nContext.class);
        Collections.sort(toAdd, new NameComparator(i18nContext.getLocale()));
        
        Set toRemove = new HashSet(queries);
        toRemove.removeAll(newQueries);

        queries.removeAll(toRemove);
        queries.addAll(toAdd);
        pool.setQueries(queries);

        pool.update();
        
		CategoryQueryPoolResourceData.removeData(httpContext, pool);
        mvcContext.setView("category.query.CategoryQueryPoolList");
        templatingContext.put("result","updated_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return checkPermission(context, coralSession, "cms.category.query.pool.modify");
    }
}
