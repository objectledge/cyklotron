package net.cyklotron.cms.modules.actions.fixes;

import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.labeo.services.resource.AttributeDefinition;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceClass;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Transforms the category query siteFilter attribute to acceptedSites attribute.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryCopySiteFilterToAcceptedSites.java,v 1.1 2005-01-24 04:34:29 pablo Exp $
 */
public class CategoryQueryCopySiteFilterToAcceptedSites
extends BaseCMSAction
{
	/** site service */
	private SiteService siteService;
	private CategoryQueryService categoryQueryService; 

    public CategoryQueryCopySiteFilterToAcceptedSites()
    {
		categoryQueryService = (CategoryQueryService)broker.getService(CategoryQueryService.SERVICE_NAME);
		siteService = (SiteService)broker.getService(SiteService.SERVICE_NAME);
    }

	/* (non-Javadoc)
	 * @see net.labeo.webcore.Action#execute(net.labeo.webcore.RunData)
	 */
	public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException
	{
		Context context = data.getContext();
		Subject subject = coralSession.getUserSubject();
		try
		{
			ResourceClass categoryQueryResClass =
				coralSession.getSchema().getResourceClass(CategoryQueryResource.CLASS_NAME);
			AttributeDefinition siteFilterAttrDef = categoryQueryResClass.getAttribute("siteFilter");

			SiteResource[] sites = siteService.getSites();
			for(int i = 0; i < sites.length; i++)
			{
				String currentSiteName = sites[i].getName();
				Resource parent = categoryQueryService.getCategoryQueryRoot(sites[i]);
				Resource[] queries = coralSession.getStore().getResource(parent);
				
				for(int j = 0; j < queries.length; j++)
				{
					CategoryQueryResource query = (CategoryQueryResource)queries[j];
					if(query.getAcceptedSites() == null)
					{
						boolean filter = query.isDefined(siteFilterAttrDef) ?
							((Boolean)(query.get(siteFilterAttrDef))).booleanValue():
							false;
						if(filter)
						{
							query.setAcceptedSites(currentSiteName);
							query.update(subject);
						}
					}
				} 
			}
		}
		catch(Exception e)
		{
			templatingContext.put("result", "exception");
			templatingContext.put("trace", StringUtils.stackTrace(e));
		}
	}
}
