package net.cyklotron.cms.modules.actions.fixes;

import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.SearchService;
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
 * Set undefined 'public' attribute in indexes to <code>true</code>.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: MakeSearchIndexesPublic.java,v 1.1 2005-01-24 04:34:29 pablo Exp $
 */
public class MakeSearchIndexesPublic
extends BaseCMSAction
{
	/** site service */
	private SiteService siteService;

	/** search service */
	private SearchService searchService;

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
		Context context = data.getContext();
		Subject subject = coralSession.getUserSubject();

		siteService = (SiteService)broker.getService(SiteService.SERVICE_NAME);
		searchService = (SearchService)broker.getService(SearchService.SERVICE_NAME);

		try
		{
			ResourceClass indexResClass =
				coralSession.getSchema().getResourceClass(IndexResource.CLASS_NAME);
			AttributeDefinition publicAttrDef = indexResClass.getAttribute("public");

			SiteResource[] sites = siteService.getSites();
			for(int i = 0; i < sites.length; i++)
			{
				Resource parent = searchService.getIndexesRoot(sites[i]);
				Resource[] indexes = coralSession.getStore().getResource(parent);
				
				for(int j = 0; j < indexes.length; j++)
                {
					IndexResource index = (IndexResource)indexes[j];
                    if(!index.isDefined(publicAttrDef))
                    {
                    	index.setPublic(true);
                    	index.update(subject);
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

	/* 
	 * (overriden)
	 */
	public boolean checkAccess(RunData data) throws ProcessingException
	{
		return checkAdministrator(context, coralSession);
	}
}
