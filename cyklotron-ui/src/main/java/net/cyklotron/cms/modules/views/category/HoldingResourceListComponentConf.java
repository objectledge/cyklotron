package net.cyklotron.cms.modules.views.category;

import java.util.Set;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.category.components.BaseResourceListConfiguration;
import net.cyklotron.cms.category.components.HoldingResourceListConfiguration;
import net.labeo.services.resource.Resource;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Configuration screen for HoldingResourceList component.
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: HoldingResourceListComponentConf.java,v 1.1 2005-01-24 04:34:27 pablo Exp $
 */
public class HoldingResourceListComponentConf extends BaseResourceListComponentConf
{
	/** Table service used to display resource lists. */
	protected TableService tableService;

	public HoldingResourceListComponentConf()
	{
		tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
	}	
	
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
    	// prepares the config
		super.prepare(data, context);
		// configuration is already inited
		HoldingResourceListConfiguration config = (HoldingResourceListConfiguration)getConfig(data);

		CmsData cmsData = cmsDataFactory.getCmsData(context);

		net.cyklotron.cms.category.components.HoldingResourceList resList =
			new net.cyklotron.cms.category.components.HoldingResourceList(
				coralSession, categoryQueryService);

		// get resources based on category query
		Resource[] resources = null;
		String query = resList.getQuery(data, config);
        Set idSet = resList.getIdSet(data, config);
        try
        {
            if(idSet != null)
            {
                resources = categoryQueryService.forwardQuery(query, idSet);
            }
            else
            {
                resources = categoryQueryService.forwardQuery(query);
            }
        }
		catch(Exception e)
		{
			cmsData.getComponent().error("Cannot execute category query", e);
			return;
		}

		// setup table tool
		TableState state = tableService.getGlobalState(data, resList.getTableStateName(data));
		TableTool tool = resList.getTableTool(data, config, state, resources);
		templatingContext.put("table", tool);
    }

    /* (non-Javadoc)
     * @see net.cyklotron.cms.modules.screens.category.BaseResourceListComponentConf#getConfig(net.labeo.webcore.RunData)
     */
    protected BaseResourceListConfiguration getConfig(RunData data) throws ProcessingException
    {
		return HoldingResourceListConfiguration.getConfig(data);
    }
}
