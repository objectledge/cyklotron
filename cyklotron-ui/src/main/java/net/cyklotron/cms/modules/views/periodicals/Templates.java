/*
 * Created on Oct 28, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.views.periodicals;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.table.generic.PathTreeElement;
import org.objectledge.table.generic.PathTreeTableModel;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.periodicals.PeriodicalsTemplatingService;
import net.cyklotron.cms.preferences.PreferencesService;
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


    private final PeriodicalsTemplatingService periodicalsTemplatingService;

    public Templates(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, PeriodicalsService periodicalsService,
        PeriodicalsTemplatingService periodicalsTemplatingService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        periodicalsService);
        this.periodicalsTemplatingService = periodicalsTemplatingService;
        
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
                String rootId = Integer.toString("/".hashCode());
                state.setTreeView(true);
                state.setRootId(rootId);
                state.setShowRoot(true);
                state.setExpanded(rootId);
                state.setPageSize(0);
                state.setSortColumnName("element");
            }
            TableColumn[] cols = new TableColumn[1];
            cols[0] = new TableColumn("element", PathTreeElement.getComparator("name", i18nContext.getLocale()));
            PathTreeTableModel model = new PathTreeTableModel(cols);
            model.bind("/", new PathTreeElement("site", "label"));
            bindRenderers(model, site);
            templatingContext.put("table", new TableTool(state, null, model));
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
        throws ProcessingException
    {
        try
        {
        String[] variants = periodicalsTemplatingService.getTemplateVariants(site, renderer);
        for(int i = 0; i < variants.length; i++)
        {
            String variant = variants[i];
            PathTreeElement elm = new PathTreeElement(variant,"variant");
            elm.set("renderer", renderer);
            model.bind("/"+renderer+"/"+variant, elm);
        }
        }
        catch(Exception e)
        {
            throw new ProcessingException(e);
        }
       
    }
}
