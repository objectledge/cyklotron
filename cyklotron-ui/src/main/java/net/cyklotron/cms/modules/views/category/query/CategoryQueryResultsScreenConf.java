package net.cyklotron.cms.modules.views.category.query;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.category.query.CategoryQueryResultsConfiguration;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Category Query Results screen configuration screen.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryResultsScreenConf.java,v 1.3 2005-01-25 11:24:15 pablo Exp $ 
 */
public class CategoryQueryResultsScreenConf
extends BaseCMSScreen
{
	public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
		throws ProcessingException
	{
		CategoryQueryResultsConfiguration config =
			new CategoryQueryResultsConfiguration(prepareScreenConfig(data), null);
		templatingContext.put("conf", config);
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
			return checkAdministrator(coralSession);
		}
	}
}
