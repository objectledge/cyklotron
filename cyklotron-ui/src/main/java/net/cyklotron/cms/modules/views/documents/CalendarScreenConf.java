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

/**
 * A screen for configuring calendar screen.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: CalendarScreenConf.java,v 1.6 2008-10-30 17:46:03 rafal Exp $
 */
public class CalendarScreenConf extends BaseCMSScreen
{
    public CalendarScreenConf(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        
    }
    
    @Override
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
		Parameters screenConfig = getScreenConfig();
		long root1 = screenConfig.getLong("category_id_1",-1);
		long root2 = screenConfig.getLong("category_id_2",-1);
        long queryPool = screenConfig.getLong("query_pool_id", -1);
		long index = screenConfig.getLong("index_id",-1);
        String sourceType = screenConfig.get("sourceConfigType", "sourceCategories");
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
            if(queryPool != -1)
            {
                templatingContext.put("query_pool", coralSession.getStore().getResource(queryPool));
            }
			if(index != -1)
			{
				templatingContext.put("index", coralSession.getStore().getResource(index));
			}
            templatingContext.put("source_type", sourceType);
		}
		catch(Exception e)
		{
			throw new ProcessingException("Exception occurred",e);
		}
    }
}
