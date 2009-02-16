package net.cyklotron.cms.modules.views.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;

public class CalendarEventsConf
	extends BaseCMSScreen
{


    public CalendarEventsConf(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        // get config
        Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
        templatingContext.put("config", componentConfig);
		long root1 = componentConfig.getLong("category_id_1",-1);
		long root2 = componentConfig.getLong("category_id_2",-1);
		long index = componentConfig.getLong("index_id",-1);
		try
		{
			if(root1 != -1)
			{
				templatingContext.put("category_1", coralSession.getStore().getResource(root1));
			}		               
			if(root2 != -1)
			{
				templatingContext.put("category_2", coralSession.getStore().getResource(root2));
			}
			if(index != -1)
			{
				templatingContext.put("index", coralSession.getStore().getResource(index));
			}
		}
		catch(Exception e)
		{
			throw new ProcessingException("Exception occurred",e);
		}
    }
}
