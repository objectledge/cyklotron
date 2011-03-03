package net.cyklotron.cms.modules.views.category.query;

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
import net.cyklotron.cms.category.query.CategoryQueryResultsConfiguration;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;

/**
 * Category Query Results screen configuration screen.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryResultsScreenConf.java,v 1.8 2008-10-30 17:46:03 rafal Exp $ 
 */
public class CategoryQueryResultsScreenConf
extends BaseCMSScreen
{
    private CategoryQueryService categoryQueryService;
    
    public CategoryQueryResultsScreenConf(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        CategoryQueryService categoryQueryService, TableStateManager tableStateManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.categoryQueryService = categoryQueryService;
    }
    
	public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
		throws ProcessingException
	{
		CategoryQueryResultsConfiguration config =
			new CategoryQueryResultsConfiguration(getScreenConfig(), null);
		templatingContext.put("conf", config);
		
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
			return checkAdministrator(coralSession);
		}
	}
}
