package net.cyklotron.cms.modules.views.link;

import java.util.ArrayList;

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
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.link.BaseLinkResource;
import net.cyklotron.cms.link.LinkRootResource;
import net.cyklotron.cms.link.LinkRootResourceImpl;
import net.cyklotron.cms.link.LinkService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.util.CmsResourceListTableModel;
import net.cyklotron.cms.workflow.StatefulResource;

/**
 *
 */
public class LinkList
    extends BaseLinkScreen
{
    protected IntegrationService integrationService;
	
    public LinkList(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, LinkService linkService,
        StructureService structureService, IntegrationService integrationService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, linkService,
                        structureService);
        this.integrationService = integrationService;
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
			TableState stateAdded = tableStateManager.getState(context, "cms:screens:link,LinkList:added");
			if(stateAdded.isNew())
			{
				stateAdded.setTreeView(false);
				stateAdded.setPageSize(10);
			}
			templatingContext.put("table_added", new TableTool(stateAdded, null, new CmsResourceListTableModel(context, integrationService,added, i18nContext.getLocale())));
			
			TableState state = tableStateManager.getState(context, "cms:screens:link,LinkList:active");
			if(state.isNew())
			{
				state.setTreeView(false);
				state.setPageSize(10);
			}
			templatingContext.put("table_active", new TableTool(state, null, new CmsResourceListTableModel(context, integrationService, active, i18nContext.getLocale())));
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("LinkException: ",e);
            return;
        }
    }
}
