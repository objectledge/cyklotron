package net.cyklotron.cms.modules.views.link;

import java.util.ArrayList;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.ResourceListTableModel;
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

import net.cyklotron.cms.CmsConstants;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.link.LinkRootResource;
import net.cyklotron.cms.link.LinkRootResourceImpl;
import net.cyklotron.cms.link.LinkService;
import net.cyklotron.cms.link.PoolResource;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 */
public class PoolList
    extends BaseLinkScreen
{

    public PoolList(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, LinkService linkService,
        StructureService structureService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, linkService,
                        structureService);
        
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        if(parameters.getBoolean("reset",false))
        {
            httpContext.removeSessionAttribute(CmsConstants.FROM_COMPONENT);
            httpContext.removeSessionAttribute(CmsConstants.COMPONENT_INSTANCE);
            httpContext.removeSessionAttribute(CmsConstants.COMPONENT_NODE);
        }
        else
        {
            Boolean fromComponent = (Boolean)httpContext.getSessionAttribute(CmsConstants.FROM_COMPONENT);
            if(fromComponent != null && fromComponent.booleanValue())
            {
                templatingContext.put("from_component",fromComponent);
            }
        }

        int lsid = parameters.getInt("lsid", -1);
        if(lsid == -1)
        {
            throw new ProcessingException("Link root id not found");
        }
        try
        {
            LinkRootResource linksRoot = LinkRootResourceImpl.getLinkRootResource(coralSession, lsid);
            templatingContext.put("linksRoot",linksRoot);
            Resource[] resources = coralSession.getStore().getResource(linksRoot);
            List pools = new ArrayList();
            for(int i = 0; i < resources.length; i++)
            {
                if(resources[i] instanceof PoolResource)
                {
                    pools.add(resources[i]);
                }
            }
            templatingContext.put("pools",pools);

            TableState state = tableStateManager.getState(context, "cms:screens:link,PoolList");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
            }
            TableModel model = new ResourceListTableModel(pools, i18nContext.getLocale());
            templatingContext.put("table", new TableTool(state, null, model));
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("failed to lookup resource", e);
        }
        catch(TableException e)
        {
            throw new ProcessingException("failed to initialize column data", e);
        }

    }
}
