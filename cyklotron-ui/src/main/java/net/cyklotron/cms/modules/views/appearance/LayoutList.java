package net.cyklotron.cms.modules.views.appearance;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.table.generic.ListTableModel;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.style.LayoutResource;
import net.cyklotron.cms.style.StyleService;

public class LayoutList
    extends BaseAppearanceScreen
{

    
    public LayoutList(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StyleService styleService, SkinService skinService,
        IntegrationService integrationService, Templating templating)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, styleService,
                        skinService, integrationService, templating);
        // TODO Auto-generated constructor stub
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();
            LayoutResource[] layouts = styleService.getLayouts(coralSession, site);
            TableColumn[] columns = new TableColumn[2];
            columns[0] = new TableColumn("name", new NameComparator(i18nContext.getLocale()));
            columns[1] = new TableColumn("description", null);
            TableModel model = new ListTableModel(layouts, columns);
            TableState state = tableStateManager.getState(context, "screens:cms:layout,LayoutList:"+
                                                     site.getName());
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setCurrentPage(0);
                state.setPageSize(10);
            }
            templatingContext.put("table", new TableTool(state, null, model));
        }
        catch(Exception e)
        {
            throw new ProcessingException("data access failed", e);
        }
    }
}
