/*
 * Created on Nov 6, 2003
 */
package net.cyklotron.cms.modules.views.periodicals;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.CoralTableModel;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalsException;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 * @author <a href="dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: PeriodicalsConf.java,v 1.3 2005-01-26 09:00:25 pablo Exp $
 */
public class PeriodicalsConf 
    extends BaseCMSScreen
{
    protected PeriodicalsService periodicalsService;
    
    public PeriodicalsConf(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, PeriodicalsService periodicalsService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.periodicalsService = periodicalsService;
    }
	public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
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
                periodicalsRoot = periodicalsService.getPeriodicalsRoot(coralSession, cmsData.getSite());
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
				periodicalsRoot = periodicalsService.getEmailPeriodicalsRoot(coralSession, cmsData.getSite());
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
			TableState state = tableStateManager.getState(context,
				"cms:periodicals:component:"+componentClazz+cmsData.getSite().getName());
			if(state.isNew())
			{
				state.setRootId(periodicalsRoot.getIdString());
				state.setShowRoot(false);
				state.setMaxVisibleDepth(2);

				state.setSortColumnName("name");
				state.setTreeView(false);
			}
        
			TableModel model = new CoralTableModel(coralSession, i18nContext.getLocale());
			templatingContext.put("table", new TableTool(state, null, model));
		}
		catch(TableException e)
		{
			throw new ProcessingException("table tool setup failed", e);
		}
	}
}
