package net.cyklotron.cms.modules.actions.fixes;

import net.cyklotron.cms.category.query.CategoryQueryBuilder;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryResourceImpl;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.category.query.CategoryQueryUtil;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.labeo.Labeo;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.resource.util.ResourceSelectionState;
import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Converts the resource list configuration to category query resources.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: TransformResourceListConfigToCategoryQuery.java,v 1.1 2005-01-24 04:34:29 pablo Exp $
 */
public class TransformResourceListConfigToCategoryQuery extends BaseNavigationNodeFix
{
	CategoryQueryService categoryQueryService; 
    PreferencesService preferencesService;
    
    public TransformResourceListConfigToCategoryQuery()
    {
		preferencesService = (PreferencesService)Labeo.getBroker().
			getService(PreferencesService.SERVICE_NAME);
		categoryQueryService = (CategoryQueryService)Labeo.getBroker().
			getService(CategoryQueryService.SERVICE_NAME);
    }

	/* (non-Javadoc)
	 * @see net.labeo.webcore.Action#execute(net.labeo.webcore.RunData)
	 */
	public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException
	{
		try
		{
			Resource root = categoryQueryService.getCategoryQueryRoot(getSite(context));
			data.getContext().put("categoryqueryroot", root);
		}
		catch (Exception e)
		{
			throw new ProcessingException("error", e);
		}

		super.execute(data);
	}

    public boolean fixNode(RunData data, NavigationNodeResource node)
        throws ProcessingException
    {
        boolean update = false;

		Resource root = (Resource)(data.getContext().get("categoryqueryroot"));
		Subject subject = coralSession.getUserSubject();

        Parameters conf = preferencesService.getNodePreferences(node);
        // get component prefix
        Parameters components = conf.getSubset("component.");
        String[] instanceNames = components.getSubsetNames();
        // get all instances
        for(int i=0; i<instanceNames.length; i++)
        {
        	String instanceName = instanceNames[i];
            Parameters instance = components.getSubset(instanceName+".");
            String app = instance.get("app",null);
            String clazz = instance.get("class",null);

			// get instances which are resource lists
			if(app == null || clazz == null || !app.equals("cms") || !clazz.startsWith("category,")
			   || clazz.equals("category,RelatedResourceList"))
		    {
		    	continue;
		    }
            
            Parameters compConf = instance.getSubset("config.cms."+clazz.replace(',','.')+".");

            if(compConf.containsKey("simpleQuery") || 
               compConf.containsKey("optionalCategories") ||
			   compConf.containsKey("equiredCategories"))
            {

				String query = compConf.get("categoryQuery",null);
				String resourceClasses = compConf.get("resourceClasses","").replace(',', ' ');
				String siteNames = compConf.get("siteNames",null);
				String optionalCategories = compConf.get("optionalCategories",null);
				String requiredCategories= compConf.get("requiredCategories",null);
				boolean simpleQuery = compConf.get("simpleQuery").asBoolean(false); 
			
				if(simpleQuery)
				{
					// regenerate query
					ResourceSelectionState state = new ResourceSelectionState("def");
					if(optionalCategories != null)
					{
						state.init(coralSession, optionalCategories.replace(',', ' '), "optional");
					}
					if(requiredCategories != null)
					{
						state.init(coralSession, requiredCategories.replace(',', ' '), "required");
					}
				
					CategoryQueryBuilder catQuery = new CategoryQueryBuilder(coralSession, state, true);
				
					query = catQuery.getQuery();
					optionalCategories = CategoryQueryUtil.joinCategoryIdentifiers(
						catQuery.getOptionalIdentifiers());
					requiredCategories= CategoryQueryUtil.joinCategoryIdentifiers(	
						catQuery.getRequiredIdentifiers());
				}

            	String categoryQueryName = instanceName+" "+node.getName()+" "+node.getIdString();
				try
                {
                    CategoryQueryResource catQuery = CategoryQueryResourceImpl
                    	.createCategoryQueryResource(coralSession, categoryQueryName, root, subject);

					catQuery.setAcceptedResourceClasses(resourceClasses);
					if(siteNames == null)
					{
						catQuery.setAcceptedSites(node.getSite().getName());
					}
					else
					{
						catQuery.setAcceptedSites(siteNames);
					}
					catQuery.setQuery(query);
					catQuery.setSimpleQuery(simpleQuery);
					if(simpleQuery)
					{
						catQuery.setOptionalCategoryPaths(optionalCategories);
						catQuery.setRequiredCategoryPaths(requiredCategories);
					}
					catQuery.update(subject);

					compConf.add("categoryQueryName", categoryQueryName);
					compConf.remove("categoryQuery");
					compConf.remove("simpleCategoryQuery");
					compConf.remove("resourceClasses");
					compConf.remove("siteNames");
					compConf.remove("optionalCategories");
					compConf.remove("requiredCategories");
					compConf.remove("simpleQuery");

					update = true;
                }
                catch (ValueRequiredException e)
                {
                	throw new ProcessingException(
						"problem creating category query for node "+node.getPath(), e);
                } 
            }
        }
        
        return update;
    }
}
