package net.cyklotron.cms.modules.views.site;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.table.comparator.CreationTimeComparator;
import org.objectledge.coral.table.comparator.CreatorNameComparator;
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
        // TODO Auto-generated constructor stub
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource[] templates = siteService.getTemplates(coralSession);
            TableColumn[] columns = new TableColumn[4];
            columns[0] = new TableColumn("name", new NameComparator(i18nContext.getLocale()));
            columns[1] = new TableColumn("creator", new CreatorNameComparator(i18nContext.getLocale()));
            columns[2] = new TableColumn("creation_date", new CreationTimeComparator());
            columns[3] = new TableColumn("description", null);
            TableModel model = new ListTableModel(templates, columns);
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


