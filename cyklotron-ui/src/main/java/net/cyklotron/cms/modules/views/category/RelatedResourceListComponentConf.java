package net.cyklotron.cms.modules.views.category;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.category.CategoryInfoTool;
import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.components.RelatedResourceListConfiguration;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: RelatedResourceListComponentConf.java,v 1.3 2005-01-25 11:23:54 pablo Exp $
 */
public class RelatedResourceListComponentConf extends CategoryList
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        // get config
        Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
		RelatedResourceListConfiguration config = RelatedResourceListConfiguration.getConfig(data);

        // TODO: use ResourceSelectionState.getExpandedIds(resServ, rootId)
        Set expandedCategoriesIds = new HashSet();
        if(config.isNew() || parameters.get("from_component_wrapper").asBoolean(false))
        {
            // initialise config
            config.init(componentConfig, coralSession);
            
            // prepare expanded categories - includes inherited ones
            Map initialState = config.getCategorySelectionState().getResources(coralSession);
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
        	config.update(data);
        }
        templatingContext.put("list_conf", config);
        
        // prepare category tree or trees
        prepareTableTools(data, expandedCategoriesIds);
        
        // prepare resource type list
        templatingContext.put("category_tool", new CategoryInfoTool(data));
    }
    
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
