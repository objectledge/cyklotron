package net.cyklotron.cms.modules.views.popup;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.CoralTableModel;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;

/**
 * Choose category query set screen.
 *
 * @author <a href="pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ChooseCategoryQueryPool.java,v 1.3 2005-01-26 09:00:36 pablo Exp $
 */
public class ChooseCategoryQueryPool extends BaseCMSScreen
{
    protected CategoryQueryService categoryQueryService;
    
    public ChooseCategoryQueryPool(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, CategoryQueryService categoryQueryService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.categoryQueryService = categoryQueryService;
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) 
        throws ProcessingException
    {
        SiteResource site = getSite();
        try
        {
            Resource queryPoolRoot = categoryQueryService.getCategoryQueryPoolRoot(coralSession,
                site);
            TableState state = tableStateManager.getState(context,
                "cms:category,query,CategoryQueryPoolList:" + site.getIdString());
            if(state.isNew())
            {
                state.setRootId(queryPoolRoot.getIdString());
                state.setTreeView(false);
                state.setShowRoot(false);
            }
            TableModel model = new CoralTableModel(coralSession, i18nContext.getLocale());
            TableTool table = new TableTool(state,null, model);
            templatingContext.put("table", table);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve information", e);    
        }
    }
}
