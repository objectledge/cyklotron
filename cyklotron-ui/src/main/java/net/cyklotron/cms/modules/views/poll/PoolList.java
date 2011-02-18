package net.cyklotron.cms.modules.views.poll;

import java.util.ArrayList;
import java.util.HashMap;
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

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.poll.PollException;
import net.cyklotron.cms.poll.PollService;
import net.cyklotron.cms.poll.PollsResource;
import net.cyklotron.cms.poll.PoolResource;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 *
 */
public class PoolList
    extends BasePollScreen
{


    public PoolList(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, PollService pollService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, pollService);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        if(parameters.getBoolean("reset",false))
        {
            httpContext.removeSessionAttribute(FROM_COMPONENT);
            httpContext.removeSessionAttribute(COMPONENT_INSTANCE);
            httpContext.removeSessionAttribute(COMPONENT_NODE);
        }
        else
        {
            Boolean fromComponent = (Boolean)httpContext.getSessionAttribute(FROM_COMPONENT);
            if(fromComponent != null && fromComponent.booleanValue())
            {
                templatingContext.put("from_component",fromComponent);
            }
        }

        int psid = parameters.getInt("psid", -1);
        if(psid == -1)
        {
            throw new ProcessingException("Polls root id not found");
        }
        try
        {
            PollsResource poolsParent = pollService.getPollsParent(coralSession, psid, pollService.POOLS_ROOT_NAME);
            
            templatingContext.put("pollsRoot", poolsParent);
            Resource[] resources = coralSession.getStore().getResource(poolsParent);
            List pools = new ArrayList();
            HashMap hasPolls = new HashMap();
            for(int i = 0; i < resources.length; i++)
            {
                if(resources[i] instanceof PoolResource)
                {
                    pools.add(resources[i]);
                    hasPolls.put(resources[i], new Boolean(pollService.getRelation(coralSession).get(resources[i]).length > 0));
                }
            }
            templatingContext.put("pools",pools);
            templatingContext.put("has_polls",hasPolls);
            TableState state = tableStateManager.getState(context, "cms:screens:poll,PoolList");
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
        catch(PollException e)
        {
            throw new ProcessingException("failed to lookup pools root resource", e);
        }
    }
}
