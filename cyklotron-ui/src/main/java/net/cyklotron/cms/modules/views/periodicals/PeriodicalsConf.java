/*
 * Created on Nov 6, 2003
 */
package net.cyklotron.cms.modules.views.periodicals;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalsException;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.ARLTableModel;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * @author <a href="dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: PeriodicalsConf.java,v 1.1 2005-01-24 04:34:37 pablo Exp $
 */
public class PeriodicalsConf 
    extends BaseCMSScreen
{
	protected PeriodicalsService periodicalsService;  
	protected TableService tableService;  
	
	public PeriodicalsConf()
	{
		periodicalsService = (PeriodicalsService)broker.getService(PeriodicalsService.SERVICE_NAME);
		tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
	}
	
	public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
		throws ProcessingException
	{
		CmsData cmsData = cmsDataFactory.getCmsData(context);
		
		Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
		String componentClazz = componentConfig.get("class",null);
		templatingContext.put("component_type", componentClazz);
		
		// setup header
		templatingContext.put("header", componentConfig.get("header",null));
		
		Resource periodicalsRoot = null;
		if(componentClazz.equals("periodicals,Periodicals"))
		{
			// setup periodicals root
			try
            {
                periodicalsRoot = periodicalsService.getPeriodicalsRoot(cmsData.getSite());
            }
            catch (PeriodicalsException e)
            {
                throw new ProcessingException("cannot get periodicals root", e);
            }
		}
		else //if(componentClazz.equals("periodicals,EmailPeriodicals"))
		{
			try
			{
				// setup email-periodicals root
				periodicalsRoot = periodicalsService.getEmailPeriodicalsRoot(cmsData.getSite());
			}
			catch (PeriodicalsException e)
			{
				throw new ProcessingException("cannot get email-periodicals root", e);
			}
		}
		
		// setup default periodical
		String periodicalName = componentConfig.get("periodical",null);
		if(periodicalName != null)
		{
			Resource[] res = coralSession.getStore().getResource(periodicalsRoot, periodicalName);
			if(res.length > 0)
			{
				templatingContext.put("selected_periodical", (PeriodicalResource)res[0]);
			}
		}

		// setup table tool
		try
		{
			TableState state = tableService.getLocalState(data,
				"cms:periodicals:component:"+componentClazz+cmsData.getSite().getName());
			if(state.isNew())
			{
				state.setRootId(periodicalsRoot.getIdString());
				state.setShowRoot(false);
				state.setMaxVisibleDepth(2);

				state.setSortColumnName("name");
				state.setViewType(TableConstants.VIEW_AS_LIST);
			}
        
			TableModel model = new ARLTableModel(i18nContext.getLocale()());
			templatingContext.put("table", new TableTool(state, model, null));
		}
		catch(TableException e)
		{
			throw new ProcessingException("table tool setup failed", e);
		}
	}
}
