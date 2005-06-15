package net.cyklotron.cms.modules.views.category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.components.BaseResourceListConfiguration;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * Configuration screen for ResourceList component.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseResourceListComponentConf.java,v 1.5 2005-06-15 12:51:03 zwierzem Exp $
 */
public abstract class BaseResourceListComponentConf extends BaseCategoryScreen
{
	protected CategoryQueryService categoryQueryService;

    
    public BaseResourceListComponentConf(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, CategoryService categoryService,
        SiteService siteService, IntegrationService integrationService,
        CategoryQueryService categoryQueryService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        categoryService, siteService, integrationService);
        this.categoryQueryService =categoryQueryService;
	}
	
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        // get config
        Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
		BaseResourceListConfiguration config = getConfig();
        
        if(config.isNew() || parameters.getBoolean("from_component_wrapper",false))
        {
            // initialise config
            config.init(componentConfig);
        }
        else
        {
            // modify config state
            CmsData cmsData = cmsDataFactory.getCmsData(context);
        	config.update(cmsData, parameters);
        }
        templatingContext.put("list_conf", config);

		// queries list        
		try
		 {
			 SiteResource site = getSite();
			 Resource root = categoryQueryService.getCategoryQueryRoot(coralSession, site);
			 Resource[] queries = coralSession.getStore().getResource(root);
             Arrays.sort(queries, new NameComparator(i18nContext.getLocale()));
			 List temp = new ArrayList(queries.length); 
			 for(int i = 0; i < queries.length; i++)
			 {
				 Resource query = queries[i];
				 List item = new ArrayList();
				 item.add(query.getName());
				 item.add(query.getName());
				 temp.add(item); 
			 }
			 templatingContext.put("queries", temp);
		 }
		 catch(Exception e)
		 {
			 throw new ProcessingException("failed to retrieve information", e);
		 }
    }
    
	protected abstract BaseResourceListConfiguration getConfig()
	throws ProcessingException;
       
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        CmsData cmsData = getCmsData();
        if(cmsData.getNode() != null)
        {
            return cmsData.getNode().canModify(coralSession, coralSession.getUserSubject());
        }
        else
        {
            // check permissions necessary to configure global components
            return checkAdministrator(coralSession);
        }
    }
}
