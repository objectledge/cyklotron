package net.cyklotron.cms.modules.views.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
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
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;

/**
 * Screen showing available categories, presented as tree, allowing to choose them for a resource.
 * This screen is not protected because everyone should be able to see which categories are assigned
 * to a resource.
 * 
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: Categorize.java,v 1.6 2005-08-10 05:31:12 rafal Exp $
 */
public class ChooseCategories
    extends CategoryList
{

    public ChooseCategories(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, CategoryService categoryService,
        SiteService siteService, IntegrationService integrationService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        categoryService, siteService, integrationService);

    }

    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        // prepare category tool
        CategoryInfoTool categoryTool = new CategoryInfoTool(context, integrationService,
            categoryService);
        templatingContext.put("category_tool", categoryTool);
        boolean resetState = parameters.getBoolean("reset-state", false);

        ResourceSelectionState categorizationState = ResourceSelectionState.getState(context,
            CategoryConstants.CATEGORY_SELECTION_STATE + ":" + "CHOOSE_SELECTION_RESOURCE");
        if(resetState)
        {
            CoralEntitySelectionState.removeState(context, categorizationState);
            categorizationState = ResourceSelectionState.getState(context,
            CategoryConstants.CATEGORY_SELECTION_STATE + ":" + "CHOOSE_SELECTION_RESOURCE");
        }

        Set expandedCategoriesIds = new HashSet();
        if( categorizationState.isNew() )
        {
            categorizationState.setPrefix("category");
            CategoryResource[] categories = getCategories(coralSession, parameters, false);
            Map initialState = new HashMap();
            for(int i = 0; i < categories.length; i++)
            {
                initialState.put(categories[i], "selected");
            }
            categorizationState.init(initialState);

            categories = getCategories(coralSession, parameters, true);
            for(int i = 0; i < categories.length; i++)
            {
                expandedCategoriesIds.add(categories[i].getIdObject());
            }

        }

        categorizationState.update(parameters);
        templatingContext.put("category_selection_state", categorizationState);

        prepareTableTools(coralSession, templatingContext, i18nContext, expandedCategoriesIds, resetState );
    }

    protected CategoryResource[] getCategories(CoralSession coralSession, Parameters parameters,
        boolean includeImplied)
        throws ProcessingException
    {
        List<CategoryResource> categories = new ArrayList<CategoryResource>();
        String ids = parameters.get("selected", "");

        if(!ids.isEmpty())
        {
            StringTokenizer st = new StringTokenizer(ids, " ");
            try
            {
                CategoryResource category = null;
                while(st.hasMoreTokens())
                {
                    category = CategoryResourceImpl.getCategoryResource(coralSession,
                        Long.parseLong(st.nextToken()));
                    if(category != null)
                    {
                        categories.add(category);
                        if(includeImplied)
                        {
                            CategoryResource[] implied = categoryService.getImpliedCategories(
                                category, false);
                            for(int i = 0; i < implied.length; i++)
                            {
                                categories.add(implied[i]);
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
        }
        CategoryResource[] result = new CategoryResource[categories.size()];
        categories.toArray(result);
        return result;
    }
}