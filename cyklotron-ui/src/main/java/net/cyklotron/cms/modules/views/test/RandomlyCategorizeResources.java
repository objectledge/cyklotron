package net.cyklotron.cms.modules.views.test;

import java.util.HashSet;

import org.jcontainer.dna.Logger;
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
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.category.CategoryList;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;

/**
 * Screen showing available categories, presented as tree, allowing to choose them for a 
 * random resource categorization.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: RandomlyCategorizeResources.java,v 1.4 2005-02-09 22:22:43 rafal Exp $
 */
public class RandomlyCategorizeResources extends CategoryList
{
    
    public RandomlyCategorizeResources(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, CategoryService categoryService,
        SiteService siteService, IntegrationService integrationService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        categoryService, siteService, integrationService);
        // TODO Auto-generated constructor stub
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        // prepare category tool
        CategoryInfoTool categoryTool = new CategoryInfoTool(context, integrationService,categoryService);
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

        if(categorizationState.isNew())
        {
            categorizationState.setPrefix("category");
        }
        // modify category ids state
        categorizationState.update(parameters);
        //
        templatingContext.put("category_selection_state", categorizationState);

        // prepare category tree or trees
        prepareTableTools(coralSession, templatingContext, i18nContext, new HashSet());

        // get resource selection state
        ResourceSelectionState resSelState =
            ResourceSelectionState.getState(context, RES_SELECTION_STATE);
        if(parameters.getBoolean("reset-state",false))
        {
            CoralEntitySelectionState.removeState(context, resSelState);
            resSelState = ResourceSelectionState.getState(context, RES_SELECTION_STATE);
        }

        // modify res ids state
        resSelState.update(parameters);
        //
        templatingContext.put("resource_selection_state", resSelState);
        
        prepareTableTool(coralSession, templatingContext, i18nContext, cmsDataFactory.getCmsData(context).getSite().getIdString(), "res_table");
    }

    protected String getTableStateBaseName()
    {
        return "cms:screens:test,RandomlyCategorizeResources";
    }
    
    public static final String RES_SELECTION_STATE = "test res sel state ugaxdasd";
}
