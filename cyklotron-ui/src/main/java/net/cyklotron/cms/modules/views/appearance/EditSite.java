package net.cyklotron.cms.modules.views.appearance;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.table.generic.PathTreeElement;
import org.objectledge.table.generic.PathTreeTableModel;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinException;
import net.cyklotron.cms.skins.SkinResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.style.LayoutResource;
import net.cyklotron.cms.style.StyleException;
import net.cyklotron.cms.style.StyleResource;
import net.cyklotron.cms.style.StyleService;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: EditSite.java,v 1.4 2005-03-08 10:57:31 pablo Exp $
 */
public class EditSite 
    extends BaseAppearanceScreen
{
    
    public EditSite(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StyleService styleService, SkinService skinService,
        IntegrationService integrationService, Templating templating)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, styleService,
                        skinService, integrationService, templating);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();
            TableState state = tableStateManager.
                getState(context, "screens:cms:appearance,EditSite:"+site.getName());
            if(state.isNew())
            {
                state.setTreeView(true);
                state.setRootId("0");
                state.setShowRoot(true);
                state.setExpanded("0");
                state.setPageSize(0);
                state.setSortColumnName("element");
            }
            TableColumn[] cols = new TableColumn[1];
            cols[0] = new TableColumn("element", PathTreeElement.getComparator("name", i18nContext.getLocale()));
            PathTreeTableModel model = new PathTreeTableModel(cols);
            model.bind("/", new PathTreeElement("site", "label"));
            bindStyles(model, site, coralSession);
            bindLayouts(model, site, coralSession);
            bindSkins(model, site, coralSession);
            templatingContext.put("table", new TableTool(state, null, model));
            String current = skinService.getCurrentSkin(coralSession, site);
            String key = SkinService.PREVIEW_KEY_PREFIX + site.getName();
            String preview = (String)httpContext.getSessionAttribute(key);
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
    private void bindSkins(PathTreeTableModel model, SiteResource site, CoralSession coralSession)
        throws SkinException
    {
        model.bind("/skins", new PathTreeElement("skins", "skins"));
        SkinResource[] skins = skinService.getSkins(coralSession, site);
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
    private void bindLayouts(PathTreeTableModel model, SiteResource site, CoralSession coralSession) 
        throws StyleException
    {
        model.bind("/layouts", new PathTreeElement("layouts", "layouts"));
        LayoutResource[] layouts = styleService.getLayouts(coralSession, site);
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
    private void bindStyles(PathTreeTableModel model, SiteResource site, CoralSession coralSession) 
        throws StyleException
    {
        model.bind("/styles", new PathTreeElement("styles", "styles"));
        Resource root = styleService.getStyleRoot(coralSession, site);
        Resource[] topStyles = coralSession.getStore().getResource(root);
        for(int i=0; i<topStyles.length; i++)
        {
            bindStyle(model, "/styles", topStyles[i], coralSession);
        }
    }
    
    private void bindStyle(PathTreeTableModel model, String path, Resource style, CoralSession coralSession)
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
                bindStyle(model, path, children[i], coralSession);
            }
        }       
    }
}
