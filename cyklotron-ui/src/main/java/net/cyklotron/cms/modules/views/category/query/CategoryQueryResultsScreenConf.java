package net.cyklotron.cms.modules.views.category.query;

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
import net.cyklotron.cms.category.query.CategoryQueryResultsConfiguration;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 * Category Query Results screen configuration screen.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryResultsScreenConf.java,v 1.5 2005-03-08 11:02:24 pablo Exp $ 
 */
public class CategoryQueryResultsScreenConf
extends BaseCMSScreen
{
    
    public CategoryQueryResultsScreenConf(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        
    }
	public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
		throws ProcessingException
	{
		CategoryQueryResultsConfiguration config =
			new CategoryQueryResultsConfiguration(prepareScreenConfig(), null);
		templatingContext.put("conf", config);
	}
    
	public boolean checkAccessRights(Context context)
		throws ProcessingException
	{
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        CmsData cmsData = getCmsData();
		if(cmsData.getNode() != null)
		{
			return cmsData.getNode().canModify(context, coralSession.getUserSubject());
		}
		else
		{
			return checkAdministrator(coralSession);
		}
	}
}
