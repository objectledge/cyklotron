package net.cyklotron.cms.modules.views.link;

import java.util.ArrayList;

import net.labeo.services.resource.Resource;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.link.BaseLinkResource;
import net.cyklotron.cms.link.LinkRootResource;
import net.cyklotron.cms.link.LinkRootResourceImpl;
import net.cyklotron.cms.util.CmsResourceListTableModel;
import net.cyklotron.services.workflow.StatefulResource;

/**
 *
 */
public class LinkList
    extends BaseLinkScreen
{
	TableService tableService = null;

	public LinkList()
	{
	    tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
	}
	
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        int lsid = parameters.getInt("lsid", -1);
        if(lsid == -1)
        {
            throw new ProcessingException("Links root id not found");
        }
        try
        {
            LinkRootResource linksRoot = LinkRootResourceImpl.getLinkRootResource(coralSession, lsid);
            templatingContext.put("linksRoot",linksRoot);
            Resource[] links = coralSession.getStore().getResource(linksRoot);
            ArrayList active = new ArrayList();
            ArrayList added = new ArrayList();
            for(int i = 0; i < links.length; i++)
            {
                if(links[i] instanceof BaseLinkResource)
                {
                    Resource state = ((StatefulResource)links[i]).getState(); 
                    if(state == null || state.getName().equals("new"))
                    {
                        added.add(links[i]);
                    }
                    else
                    {
                        active.add(links[i]);
                    }
                }
            }
			TableState stateAdded = tableService.getLocalState(data, "cms:screens:link,LinkList:added");
			if(stateAdded.isNew())
			{
				stateAdded.setViewType(TableConstants.VIEW_AS_LIST);
				stateAdded.setPageSize(10);
			}
			templatingContext.put("table_added", new TableTool(stateAdded, new CmsResourceListTableModel(added, i18nContext.getLocale()()), null));
			
			TableState state = tableService.getLocalState(data, "cms:screens:link,LinkList:active");
			if(state.isNew())
			{
				state.setTreeView(false);
				state.setPageSize(10);
			}
			templatingContext.put("table_active", new TableTool(state, new CmsResourceListTableModel(active, i18nContext.getLocale()()), null));
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("LinkException: ",e);
            return;
        }
    }
}
