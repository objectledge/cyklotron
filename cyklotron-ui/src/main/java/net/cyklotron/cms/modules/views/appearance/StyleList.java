package net.cyklotron.cms.modules.views.appearance;

import java.util.ArrayList;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.CoralTableModel;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableFilter;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.style.LevelResource;
import net.cyklotron.cms.style.StyleService;

public class StyleList
    extends BaseAppearanceScreen
{


    public StyleList(org.objectledge.context.Context context, Logger logger,
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
            TableModel model = new CoralTableModel(coralSession, i18nContext.getLocale());
            TableState state = tableStateManager.getState(context, "screens:cms:appearance,StyleList:"+
                                                     site.getName());
            Resource root = styleService.getStyleRoot(coralSession, site);
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
            TableTool helper = new TableTool(state, filters, model);
            templatingContext.put("table", helper);
        }
        catch(Exception e)
        {
            throw new ProcessingException("data access failed", e);
        }
    }
}
