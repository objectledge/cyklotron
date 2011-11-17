package net.cyklotron.cms.modules.views.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import net.cyklotron.cms.category.CategoryResourceImpl;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.documents.keywords.KeywordResource;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;

/**
 * Screen showing available categories, presented as tree, allowing to choose them for a resource.
 * This screen is not protected because everyone should be able to see which categories are
 * assigned to a resource.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: Categorize.java,v 1.6 2005-08-10 05:31:12 rafal Exp $
 */
public class ChooseCategories extends CategoryList
{
    
    public ChooseCategories(org.objectledge.context.Context context, Logger logger,
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
 
        Resource resource = getResource(coralSession, parameters);
        templatingContext.put("resource", resource);
        
        String choose_category_selection_name = "NEW_CHOOSE_SELECTION_RESOURCE";
        if(resource != null && !(resource instanceof KeywordResource))
        {
            choose_category_selection_name = resource.getIdString();
        }
        
        // prepare category tool
        CategoryInfoTool categoryTool = new CategoryInfoTool(context, integrationService, categoryService);
        templatingContext.put("category_tool", categoryTool);
        boolean resetState = parameters.getBoolean("reset-state", true);
        
        ResourceSelectionState categorizationState = ResourceSelectionState.getState(context,
            CategoryConstants.CATEGORY_SELECTION_STATE + ":" + choose_category_selection_name );
        
        if(resetState)
        {
            CoralEntitySelectionState.removeState(context, categorizationState);
            categorizationState = ResourceSelectionState.getState(context,
                CategoryConstants.CATEGORY_SELECTION_STATE + ":" + choose_category_selection_name );
        }

        Set expandedCategoriesIds = new HashSet();
        if(categorizationState.isNew())
        {
            
            categorizationState.setPrefix("category");
            CategoryResource[] categories = getCategories(coralSession, resource, false);
            Map initialState = new HashMap();
            for(int i = 0; i < categories.length; i++)
            {
                initialState.put(categories[i], "selected");
            }
            categorizationState.init(initialState);

            categories = getCategories(coralSession, resource, true);
            for(int i = 0; i < categories.length; i++)
            {
                expandedCategoriesIds.add(categories[i].getIdObject());
            }
        }
        
        // modify category ids state
        categorizationState.update(parameters);
        templatingContext.put("category_selection_state", categorizationState);
        
        prepareTableTools(coralSession, templatingContext, i18nContext, expandedCategoriesIds, true);
    }


    protected CategoryResource[] getCategories(CoralSession coralSession, Resource resource,
        boolean includeImplied)
        throws ProcessingException
    {

        if(resource instanceof KeywordResource)
        {
            List<CategoryResource> categories = new ArrayList<CategoryResource>();
            categories = ((KeywordResource)resource).getCategories();
            if(includeImplied)
            {
                for(int i = 0; i < categories.size(); i++)
                {
                    CategoryResource[] implied = categoryService.getImpliedCategories(
                        categories.get(i), false);
                    for(int j = 0; j < implied.length; j++)
                    {
                        categories.add(implied[j]);
                    }
                }
            }
            CategoryResource[] result = new CategoryResource[categories.size()];
            categories.toArray(result);
            return result;
        }
        else
        {
            return categoryService.getCategories(coralSession, resource, includeImplied);
        }
    }
    
    protected CategoryResource[] getCategories(CoralSession coralSession, Parameters parameters,
        boolean includeImplied)
        throws ProcessingException
    {
        List<CategoryResource> categories = new ArrayList<CategoryResource>();
        CategoryResource category = null;
        String[] ids = parameters.get("ids", "").split(" ");
        try
        {
            for(int i = 0; i < ids.length; i++)
            {
                category = CategoryResourceImpl.getCategoryResource(coralSession, new Long(ids[i]));
                if(category != null)
                {
                    categories.add(category);
                    if(includeImplied)
                    {
                        CategoryResource[] implied = categoryService.getImpliedCategories(category,
                            false);
                        for(int j = 0; j < implied.length; j++)
                        {
                            categories.add(implied[j]);
                        }
                    }
                }
            }
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("resource does not exist", e);
        }
        catch(NumberFormatException e)
        {
            throw new ProcessingException("resource does not exist", e);
        }
        CategoryResource[] result = new CategoryResource[categories.size()];
        categories.toArray(result);
        return result;
    }
    
    
    protected Resource getResource(CoralSession coralSession, Parameters parameters)
        throws ProcessingException
    {
        long res_id = parameters.getLong("res_id", -1);
        Resource resource = null;
        if(res_id != -1)
        {
            try
            {
                resource = coralSession.getStore().getResource(res_id);
            }
            catch(EntityDoesNotExistException e)
            {
                throw new ProcessingException("resource does not exist", e);
            }
        }
        return resource;
    }
}
