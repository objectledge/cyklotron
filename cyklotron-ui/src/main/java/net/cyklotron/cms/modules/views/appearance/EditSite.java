package net.cyklotron.cms.modules.views.appearance;

import net.labeo.Labeo;
import net.labeo.services.resource.Resource;
import net.labeo.services.table.PathTreeTableModel;
import net.labeo.services.table.TableColumn;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.table.PathTreeElement;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinException;
import net.cyklotron.cms.skins.SkinResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.style.LayoutResource;
import net.cyklotron.cms.style.StyleException;
import net.cyklotron.cms.style.StyleResource;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: EditSite.java,v 1.1 2005-01-24 04:35:04 pablo Exp $
 */
public class EditSite 
    extends BaseAppearanceScreen
{
    protected TableService tableService;

    /**
     * 
     */
    public EditSite()
    {
        super();
        tableService = (TableService)Labeo.getBroker().
            getService(TableService.SERVICE_NAME);
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
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
            bindStyles(model, site);
            bindLayouts(model, site);
            bindSkins(model, site);
            templatingContext.put("table", new TableTool(state, model, null));
            String current = skinService.getCurrentSkin(site);
            String key = SkinService.PREVIEW_KEY_PREFIX + site.getName();
            String preview = (String)data.getGlobalContext().getAttribute(key);
            templatingContext.put("preview_skin", preview);
            templatingContext.put("current_skin", current);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to load information", e);
        }
    }

    /**
     * @param model
     * @param site
     */
    private void bindSkins(PathTreeTableModel model, SiteResource site)
        throws SkinException
    {
        model.bind("/skins", new PathTreeElement("skins", "skins"));
        SkinResource[] skins = skinService.getSkins(site);
        for (int i = 0; i < skins.length; i++)
        {   
            PathTreeElement elm = new PathTreeElement(skins[i].getName(), "skin");
            model.bind("/skins/"+skins[i].getName(), elm);
        }
    }

    /**
     * @param model
     * @param site
     */
    private void bindLayouts(PathTreeTableModel model, SiteResource site) 
        throws StyleException
    {
        model.bind("/layouts", new PathTreeElement("layouts", "layouts"));
        LayoutResource[] layouts = styleService.getLayouts(site);
        for (int i = 0; i < layouts.length; i++)
        {   
            PathTreeElement elm = new PathTreeElement(layouts[i].getName(), "layout");
            elm.set("id", layouts[i].getIdString());
            model.bind("/layouts/"+layouts[i].getName(), elm);
        }
    }

    /**
     * @param model
     * @param site
     */
    private void bindStyles(PathTreeTableModel model, SiteResource site) 
        throws StyleException
    {
        model.bind("/styles", new PathTreeElement("styles", "styles"));
        Resource root = styleService.getStyleRoot(site);
        Resource[] topStyles = coralSession.getStore().getResource(root);
        for(int i=0; i<topStyles.length; i++)
        {
            bindStyle(model, "/styles", topStyles[i]);
        }
    }
    
    private void bindStyle(PathTreeTableModel model, String path, Resource style)
    {
        path = path+"/"+style.getName();
        PathTreeElement elm = new PathTreeElement(style.getName(), "style");
        elm.set("id", style.getIdString());
        model.bind(path, elm);
        Resource[] children = coralSession.getStore().getResource(style);
        for(int i=0; i<children.length; i++)
        {
            if(children[i] instanceof StyleResource)
            {
                bindStyle(model, path, children[i]);
            }
        }       
    }
}
