package net.cyklotron.cms.modules.views.category;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryInfoTool;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.components.RelatedResourceListConfiguration;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;

/**
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: RelatedResourceListComponentConf.java,v 1.4 2005-01-26 05:23:29 pablo Exp $
 */
public class RelatedResourceListComponentConf extends CategoryList
{
    protected CategoryQueryService categoryQueryService;
    
    public RelatedResourceListComponentConf(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, CategoryService categoryService,
        SiteService siteService, IntegrationService integrationService,
        CategoryQueryService categoryQueryService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        categoryService, siteService, integrationService);
        this.categoryQueryService = categoryQueryService;
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        // get config
        Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
		RelatedResourceListConfiguration config = RelatedResourceListConfiguration.getConfig(context);

        // TODO: use ResourceSelectionState.getExpandedIds(resServ, rootId)
        Set expandedCategoriesIds = new HashSet();
        if(config.isNew() || parameters.getBoolean("from_component_wrapper",false))
        {
            // initialise config
            config.init(coralSession, componentConfig, categoryQueryService);
            
            // prepare expanded categories - includes inherited ones
            Map initialState = config.getCategorySelectionState().getEntities(coralSession);
            for(Iterator i=initialState.keySet().iterator(); i.hasNext();)
            {
                CategoryResource category = (CategoryResource)(i.next());
                CategoryResource[] cats = categoryService.getImpliedCategories(category, true);
                for(int j=0; j<cats.length; j++)
                {
                    expandedCategoriesIds.add(cats[j].getIdObject());
                }
            }
        }
        else
        {
            // modify config state
        	config.update(cmsDataFactory.getCmsData(context), parameters);
        }
        templatingContext.put("list_conf", config);
        
        // prepare category tree or trees
        prepareTableTools(coralSession, templatingContext, i18nContext, expandedCategoriesIds);
        
        // prepare resource type list
        templatingContext.put("category_tool", new CategoryInfoTool(context, integrationService, categoryService));
    }
    
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        CmsData cmsData = getCmsData();
        if(cmsData.getNode() != null)
        {
            return cmsData.getNode().canModify(context,coralSession.getUserSubject());
        }
        else
        {
            // check permissions necessary to configure global components
            return checkAdministrator(coralSession);
        }
    }
}
