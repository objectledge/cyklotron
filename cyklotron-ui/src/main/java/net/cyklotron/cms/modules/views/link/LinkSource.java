package net.cyklotron.cms.modules.views.link;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.ARLTableModel;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;
/**
 *
 */
public class LinkSource
    extends BaseLinkScreen
{
    /** table service */
    TableService tableService;

    /** structure service */
    StructureService structureService;

    public LinkSource()
    {
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
        structureService = (StructureService)broker.getService(StructureService.SERVICE_NAME);
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {

        Boolean fromComponent = (Boolean)httpContext.getSessionAttribute(FROM_COMPONENT);
        if(fromComponent != null && fromComponent.booleanValue())
        {
            templatingContext.put("from_component",fromComponent);
            templatingContext.put("component_node",(Long)httpContext.getSessionAttribute(COMPONENT_NODE));
            templatingContext.put("component_instance",(String)httpContext.getSessionAttribute(COMPONENT_INSTANCE));
        }

        SiteResource site = getSite();
        if(site != null)
        {
            try
            {
                Resource root = structureService.getRootNode(site);

                String componentName = "link_structure_tree";
                String rootId = root.getIdString();
                TableState state = tableService.getLocalState(data, componentName);
                int viewType = parameters.getInt("viewType", TableConstants.VIEW_AS_TREE);
                state.setViewType(viewType);
                state.setShowRoot(true);
                state.setMultiSelect(false);
                state.setRootId(rootId);
                state.setExpanded(rootId);
                if(state.getSortColumnName() == null)
                {
                    state.setSortColumnName("creation.time");
                }
                TableModel model = new ARLTableModel(i18nContext.getLocale()());
                TableTool helper = null;
                helper = new TableTool(state, model, null);
                templatingContext.put("table", helper);
            }
            catch(StructureException e)
            {
                throw new ProcessingException("Cannot create TableTool", e);
            }
            catch(TableException e)
            {
                throw new ProcessingException("Cannot create TableTool", e);
            }
        }
    }
}
