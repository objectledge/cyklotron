package net.cyklotron.cms.modules.views.structure;

import java.util.ArrayList;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.table.ResourceListTableModel;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableException;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;
import net.cyklotron.cms.util.ProtectedViewFilter;

public class ConfirmMassOperation
    extends BaseStructureScreen
{
    public ConfirmMassOperation(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StructureService structureService,
        StyleService styleService, SiteService siteService, RelatedService relatedService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        structureService, styleService, siteService, relatedService);
    }

    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            ArrayList<NavigationNodeResource> list = new ArrayList<NavigationNodeResource>();
            long[] ids = parameters.getLongs("op_node_id");
            for (long id : ids)
            {
                NavigationNodeResource node = NavigationNodeResourceImpl.getNavigationNodeResource(
                    coralSession, id);
                if(node.canView(coralSession, coralSession.getUserSubject()))
                {
                    list.add(node);
                }
            }
            TableState state = tableStateManager.getState(context,
                "cms:screens:structure.ConfirmMassOperation");
            if(state.isNew())
            {
                state.setPageSize(0);
            }
            TableModel model = new ResourceListTableModel(list, i18nContext.getLocale());
            try
            {
                ArrayList<TableFilter> filters = new ArrayList<TableFilter>();
                filters.add(new ProtectedViewFilter(coralSession, coralSession.getUserSubject()));
                TableTool helper = new TableTool(state, filters, model);
                templatingContext.put("table", helper);
            }
            catch(TableException e)
            {
                throw new ProcessingException("failed to create TableTool", e);
            }
            templatingContext.put("operation", parameters.get("operation"));
            templatingContext.put("permission", parameters.get("permission"));
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to prepare list of nodes", e);
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return true;
    }
}
