package net.cyklotron.cms.modules.views.test;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryInfoTool;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.category.BaseCategoryScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;

/**
 * Screen for batch category creation.
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id: CreateCategories.java,v 1.5 2005-03-08 11:09:32 pablo Exp $
 */
public class CreateCategories extends BaseCategoryScreen
{
    
    public CreateCategories(org.objectledge.context.Context context, Logger logger,
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
        if(parameters.isDefined("cat_id"))
        {
            CategoryResource category = getCategory(coralSession, parameters);
            templatingContext.put("category", category);
        }
        templatingContext.put("category_tool", new CategoryInfoTool(context, integrationService, categoryService));
    }
}
