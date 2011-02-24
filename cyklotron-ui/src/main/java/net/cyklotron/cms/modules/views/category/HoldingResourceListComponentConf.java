package net.cyklotron.cms.modules.views.category;

import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.category.components.BaseResourceListConfiguration;
import net.cyklotron.cms.category.components.HoldingResourceList;
import net.cyklotron.cms.category.components.HoldingResourceListConfiguration;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 * Configuration screen for HoldingResourceList component.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HoldingResourceListComponentConf.java,v 1.5 2005-05-17 06:22:17 zwierzem Exp $
 */
public class HoldingResourceListComponentConf extends BaseResourceListComponentConf
{    
    private final StructureService structureService;

    public HoldingResourceListComponentConf(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, CategoryService categoryService,
        SiteService siteService, IntegrationService integrationService,
        CategoryQueryService categoryQueryService, StructureService structureService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        categoryService, siteService, integrationService, categoryQueryService);
        this.structureService = structureService;
        
    }
	
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
    	// prepares the config
		super.process(parameters, mvcContext, templatingContext, httpContext, i18nContext, coralSession);
		// configuration is already inited
		HoldingResourceListConfiguration config = (HoldingResourceListConfiguration)getConfig();

		CmsData cmsData = cmsDataFactory.getCmsData(context);

		HoldingResourceList resList =
			new HoldingResourceList(context,integrationService,cmsDataFactory,
				categoryQueryService, siteService, structureService);

		// get resources based on category query
		Resource[] resources = null;
		String query = resList.getQuery(coralSession,  config);
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
			cmsData.getComponent().error("Cannot execute category query", e);
			return;
		}

		// setup table tool
		TableState state = tableStateManager.getState(context, resList.getTableStateName());
		TableTool tool = resList.getTableTool(coralSession, context, config, state, resources);
		templatingContext.put("table", tool);
    }

    protected BaseResourceListConfiguration getConfig() throws ProcessingException
    {
		return HoldingResourceListConfiguration.getConfig(context);
    }
}
