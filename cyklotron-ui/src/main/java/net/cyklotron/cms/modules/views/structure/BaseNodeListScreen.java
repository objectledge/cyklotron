package net.cyklotron.cms.modules.views.structure;

import java.util.ArrayList;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.structure.table.NavigationTableModel;
import net.cyklotron.cms.style.StyleService;
import net.cyklotron.cms.util.ProtectedViewFilter;

/**
 * Base class for node list screens.
 */
public abstract class BaseNodeListScreen
extends BaseStructureScreen
{
    public BaseNodeListScreen(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StructureService structureService,
        StyleService styleService, SiteService siteService, RelatedService relatedService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        structureService, styleService, siteService, relatedService);
        
    }

    public void prepareTableState(Context context,
            NavigationNodeResource rootNode, NavigationNodeResource selectedNode)
        throws ProcessingException
    {
        Parameters parameters = RequestParameters.getRequestParameters(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        HttpContext httpContext = HttpContext.getHttpContext(context);
        I18nContext i18nContext = I18nContext.getI18nContext(context);
        TemplatingContext templatingContext = TemplatingContext.getTemplatingContext(context);
        TableState state = tableStateManager.getState(context, getStateName());
        if(state.isNew())
        {
            state.setRootId(rootNode.getIdString());
            state.setExpanded(rootNode.getIdString());
            state.setTreeView(true);
            state.setShowRoot(true);
            state.setSortColumnName("sequence");
        }

        if(selectedNode != null)
        {
            String selectedNodeId = selectedNode.getIdString();
            state.setExpanded(selectedNodeId);
            //state.setSelected(selectedNodeId);

            List expandedList = selectedNode.getParentNavigationNodes(true);
            for(int i = 0; i < expandedList.size(); i++)
            {
                state.setExpanded(((NavigationNodeResource)expandedList.get(i)).getIdString());
            }
        }

        
        TableModel model = new NavigationTableModel(coralSession, i18nContext.getLocale());
        try
        {
            ArrayList filters = new ArrayList();
            filters.add(new ProtectedViewFilter(context, coralSession.getUserSubject()));
            TableTool helper = new TableTool(state, filters, model);
            templatingContext.put("table", helper);
        }
        catch(TableException e)
        {
            throw new ProcessingException("Cannot create TableTool", e);
        }
    }

    protected abstract String getStateName()
        throws ProcessingException;
}
