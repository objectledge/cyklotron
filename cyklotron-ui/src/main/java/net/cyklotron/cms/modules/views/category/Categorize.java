package net.cyklotron.cms.modules.views.category;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.util.CoralEntitySelectionState;
import org.objectledge.coral.util.ResourceSelectionState;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryConstants;
import net.cyklotron.cms.category.CategoryInfoTool;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;

/**
 * Screen showing available categories, presented as tree, allowing to choose them for a resource.
 * This screen is not protected because everyone should be able to see which categories are
 * assigned to a resource.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: Categorize.java,v 1.5 2005-03-08 11:02:11 pablo Exp $
 */
public class Categorize extends CategoryList
{
    
    public Categorize(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, CategoryService categoryService,
        SiteService siteService, IntegrationService integrationService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        categoryService, siteService, integrationService);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        // prepare categorized resource
        Resource resource = getResource(coralSession, parameters);
        templatingContext.put("resource", resource);

        // prepare category tool
        CategoryInfoTool categoryTool = new CategoryInfoTool(context, integrationService, categoryService);
        templatingContext.put("category_tool", categoryTool);

        // get category selection state
        ResourceSelectionState categorizationState =
            ResourceSelectionState.getState(context, CategoryConstants.CATEGORY_SELECTION_STATE);
        if(parameters.getBoolean("reset-state",false))
        {
            CoralEntitySelectionState.removeState(context, categorizationState);
            categorizationState =
                ResourceSelectionState.getState(context, CategoryConstants.CATEGORY_SELECTION_STATE);
        }

        Set expandedCategoriesIds = new HashSet();
        if(categorizationState.isNew())
        {
            categorizationState.setPrefix("category");
    
            CategoryResource[] categories = categoryService.getCategories(coralSession, resource, false);
            Map initialState = new HashMap();
            for(int i=0; i<categories.length; i++)
            {
                initialState.put(categories[i], "selected");
            }
            categorizationState.init(initialState);

            // prepare expanded categories - includes inherited ones
            categories = categoryService.getCategories(coralSession, resource, true);
            for(int i=0; i<categories.length; i++)
            {
                expandedCategoriesIds.add(categories[i].getIdObject());
            }
        }
        // modify category ids state
        categorizationState.update(parameters);
        //
        templatingContext.put("category_selection_state", categorizationState);

        // prepare category tree or trees
        prepareTableTools(coralSession, templatingContext, i18nContext, expandedCategoriesIds);
    }

    protected String getTableStateBaseName()
    {
        return "cms:screens:category,Categorize";
    }

    protected Resource getResource(CoralSession coralSession, Parameters parameters)
        throws ProcessingException
    {
        long res_id = parameters.getLong("res_id", -1);
        Resource resource;
        if(res_id == -1)
        {
            throw new ProcessingException("Parameter res_id is not defined");
        }
        else
        {
            try
            {
                resource = coralSession.getStore().getResource(res_id);
                if(resource instanceof CategoryResource)
                {
                    throw new ProcessingException("Cannot categorize categories");
                }
            }
            catch(EntityDoesNotExistException e)
            {
                throw new ProcessingException("resource does not exist",e);
            }
        }
        return resource;
    }
}
