package net.cyklotron.cms.modules.views.appearance;

import java.util.ArrayList;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.ARLTableModel;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableFilter;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.style.LevelResource;

public class StyleList
    extends BaseAppearanceScreen
{
    protected TableService tableService;

    public StyleList()
    {
        tableService = (TableService)broker.
            getService(TableService.SERVICE_NAME);
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();
            TableModel model = new ARLTableModel(i18nContext.getLocale()());
            TableState state = tableService.getLocalState(data, "screens:cms:appearance,StyleList:"+
                                                     site.getName());
            Resource root = styleService.getStyleRoot(site);
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setCurrentPage(0);
                state.setPageSize(10);
                state.setShowRoot(false);
                String rootId = root.getIdString();
                state.setRootId(rootId);
                state.setExpanded(rootId);
            }
            ArrayList filters = new ArrayList();
            filters.add(new TableFilter()
                {
                    public boolean accept(Object o)
                    {
                        return !(o instanceof LevelResource);
                    }
                }
            );
            TableTool helper = new TableTool(state, model, filters);
            templatingContext.put("table", helper);
        }
        catch(Exception e)
        {
            throw new ProcessingException("data access failed", e);
        }
    }
}
