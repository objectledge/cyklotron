package net.cyklotron.cms.modules.views.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;

public class PrintDocumentConf
	extends BaseCMSScreen
{


    public PrintDocumentConf(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        // TODO Auto-generated constructor stub
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
		CmsData cmsData = getCmsData();
		try
		{
			// get config
			Parameters conf = prepareComponentConfig(parameters, templatingContext);
			Resource parent = cmsData.getHomePage().getParent();
			String path = conf.get("printNodePath",null);
			if(path != null)
			{
				Resource[] nodes = coralSession.getStore().getResourceByPath(parent, path);
				if(nodes.length > 1)
				{
					// ???
					throw new ProcessingException("too many print nodes with the same path");
				}
				templatingContext.put("print_node", nodes[0]);
			}
		}
		catch(Exception e)
		{
			throw new ProcessingException("cannot find configured print node", e);
		}
    }
}
