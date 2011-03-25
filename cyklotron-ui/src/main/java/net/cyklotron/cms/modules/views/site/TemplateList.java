package net.cyklotron.cms.modules.views.site;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.table.ResourceListTableModel;
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
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 *
 */
public class TemplateList
    extends BaseSiteScreen
{
    
    public TemplateList(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, SiteService siteService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, siteService);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource[] templates = siteService.getTemplates(coralSession);
            TableModel model = new ResourceListTableModel(templates, i18nContext.getLocale());
            TableState state = tableStateManager.getState(context, "cms:screens:site,TemplateList");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
            }
            templatingContext.put("table", new TableTool(state, null, model));
        }
        catch(Exception e)
        {
            if(e instanceof ProcessingException)
            {
                throw (ProcessingException)e;
            }
            else
            {
                throw new ProcessingException("failed to load node list", e);
            }
        }
    }
}


