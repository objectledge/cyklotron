package net.cyklotron.cms.modules.views.category.query;

import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.query.CategoryQueryException;
import net.cyklotron.cms.category.query.CategoryQueryResource;
import net.cyklotron.cms.category.query.CategoryQueryResultsConfiguration;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.category.query.CategoryQueryUtil;
import net.cyklotron.cms.category.query.screens.CategoryQueryResultsResourceList;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.BaseSkinableScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * Category Query Resutls screen.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryResults.java,v 1.4 2005-03-23 09:14:07 pablo Exp $ 
 */
public class CategoryQueryResults 
    extends BaseSkinableScreen
{
	/** category query service */
	protected CategoryQueryService categoryQueryService;

    protected SiteService siteService;
    
    protected IntegrationService integrationService;
    
    public CategoryQueryResults(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        StructureService structureService, StyleService styleService, SkinService skinService,
        MVCFinder mvcFinder, TableStateManager tableStateManager,
        CategoryQueryService categoryQueryService, SiteService siteService,
        IntegrationService integrationService)
    {
        super(context, logger, preferencesService, cmsDataFactory, structureService, styleService,
                        skinService, mvcFinder, tableStateManager);
        this.categoryQueryService = categoryQueryService;
        this.siteService = siteService;
        this.integrationService = integrationService;
	}
	
	public void prepareDefault(Context context)
		throws ProcessingException
	{
		CmsData cmsData = cmsDataFactory.getCmsData(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
		// get query object
		CategoryQueryResource categoryQuery;        
		try
		{
			if(parameters.isDefined(CategoryQueryUtil.QUERY_PARAM))
			{
                categoryQuery = CategoryQueryUtil.getQuery(coralSession, parameters);
			}
			else
			{
                categoryQuery = categoryQueryService.getDefaultQuery(coralSession, cmsData.getSite());
			}
            
            if(categoryQuery == null)
            {
                screenError(cmsData.getNode(), context, "default category query not configured");
                return;
            }
		}
		catch (CategoryQueryException e1)
		{
			screenError(cmsData.getNode(), context, "cannot get catgory query root for site "+
                cmsData.getSite().getName());
			return;
		}    
		templatingContext.put("category_query", categoryQuery);    

		// get config
		CategoryQueryResultsConfiguration config = 
			new CategoryQueryResultsConfiguration(getConfiguration(coralSession), categoryQuery);

        CategoryQueryResultsResourceList resList = new CategoryQueryResultsResourceList(context, integrationService,
            cmsDataFactory, categoryQueryService, siteService,
                categoryQuery, config);

        // get resources based on category query
        Resource[] resources = null;
        String query = resList.getQuery(coralSession, config);
        Set idSet = resList.getIdSet(coralSession, config);
        try
        {
            if(idSet != null)
            {
                resources = categoryQueryService.forwardQuery(coralSession, query, idSet);
            }
            else
            {
                resources = categoryQueryService.forwardQuery(coralSession, query);
            }
        }
        catch(Exception e)
        {
            screenError(cmsData.getNode(), context, "Cannot execute category query");
            return;
        }

        // setup table tool
        TableState state = tableStateManager.getState(context, resList.getTableStateName());
        TableTool tool = resList.getTableTool(coralSession, context, config, state, resources);
        templatingContext.put("table", tool);
    }
}
