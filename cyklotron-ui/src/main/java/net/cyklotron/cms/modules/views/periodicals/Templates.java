/*
 * Created on Oct 28, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.views.periodicals;

import net.labeo.Labeo;
import net.labeo.services.table.PathTreeElement;
import net.labeo.services.table.PathTreeTableModel;
import net.labeo.services.table.TableColumn;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;

/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Templates 
    extends BasePeriodicalsScreen
{
    protected TableService tableService;

    public Templates()
    {
        tableService = (TableService)Labeo.getBroker().getService(TableService.SERVICE_NAME);
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();
            TableState state = tableService.
                getLocalState(data, "screens:cms:appearance,EditSite:"+site.getName());
            if(state.isNew())
            {
                state.setViewType(TableConstants.VIEW_AS_TREE);
                state.setRootId("0");
                state.setShowRoot(true);
                state.setExpanded("0");
                state.setPageSize(0);
                state.setMultiSelect(false);
                state.setSortColumnName("element");
            }
            TableColumn[] cols = new TableColumn[1];
            cols[0] = new TableColumn("element", PathTreeElement.getComparator("name", i18nContext.getLocale()()));
            PathTreeTableModel model = new PathTreeTableModel(cols);
            model.bind("/", new PathTreeElement("site", "label"));
            bindRenderers(model, site);
            templatingContext.put("table", new TableTool(state, model, null));
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to load information", e);
        }
    }

    protected void bindRenderers(PathTreeTableModel model, SiteResource site)
        throws ProcessingException
    {
        String[] renderers = periodicalsService.getRendererNames();
        for(int i = 0; i < renderers.length; i++)
        {
            String renderer = renderers[i];
            model.bind("/"+renderer, new PathTreeElement(renderer,"renderer"));
            bindVariants(model, site, renderer);
        }
    }
    
    protected void bindVariants(PathTreeTableModel model, SiteResource site, String renderer)
    {
        String[] variants = periodicalsService.getTemplateVariants(site, renderer);
        for(int i = 0; i < variants.length; i++)
        {
            String variant = variants[i];
            PathTreeElement elm = new PathTreeElement(variant,"variant");
            elm.set("renderer", renderer);
            model.bind("/"+renderer+"/"+variant, elm);
        }
    }
}
