package net.cyklotron.cms.modules.views.category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.category.components.BaseResourceListConfiguration;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.site.SiteResource;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.NameComparator;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Configuration screen for ResourceList component.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseResourceListComponentConf.java,v 1.3 2005-01-25 11:23:54 pablo Exp $
 */
public abstract class BaseResourceListComponentConf extends BaseCategoryScreen
{
	protected CategoryQueryService categoryQueryService;

	public BaseResourceListComponentConf()
	{
		categoryQueryService =
			(CategoryQueryService) broker.getService(CategoryQueryService.SERVICE_NAME);
	}
	
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        // get config
        ParameterscomponentConfig = prepareComponentConfig(parameters, templatingContext);
		BaseResourceListConfiguration config = getConfig(data);
        
        if(config.isNew() || parameters.get("from_component_wrapper").asBoolean(false))
        {
            // initialise config
            config.init(componentConfig, coralSession);
        }
        else
        {
            // modify config state
        	config.update(data);
        }
        templatingContext.put("list_conf", config);

		// queries list        
		try
		 {
			 SiteResource site = getSite();
			 Resource root = categoryQueryService.getCategoryQueryRoot(site);
			 Resource[] queries = coralSession.getStore().getResource(root);
             Arrays.sort(queries, new NameComparator(i18nContext.getLocale()()));
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
    
	protected abstract BaseResourceListConfiguration getConfig(RunData data)
	throws ProcessingException;
       
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CmsData cmsData = getCmsData();
        if(cmsData.getNode() != null)
        {
            return cmsData.getNode().canModify(coralSession.getUserSubject());
        }
        else
        {
            // check permissions necessary to configure global components
            return checkAdministrator(coralSession);
        }
    }
}
