package net.cyklotron.cms.modules.views.search;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.CoralTableModel;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.site.SiteResource;

/**
 * A base list screen for search app.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseListScreen.java,v 1.4 2005-03-08 11:08:42 pablo Exp $
 */
public abstract class BaseListScreen extends BaseSearchScreen
{

    public BaseListScreen(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, SearchService searchService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, searchService);
        
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
    throws ProcessingException
    {
        CmsData cmsData = getCmsData();
        SiteResource site = cmsData.getSite();
        if(site == null)
        {
            site = cmsData.getGlobalComponentsDataSite();
        }
        if(site == null)
        {
            throw new ProcessingException("No site seletcted");
        }
        try
        {
            TableState state = tableStateManager.getState(context, getTableStateName(coralSession, site));
            if(state.isNew())
            {
                Resource root = getTableRoot(coralSession, site);
                state.setRootId(root.getIdString());

                state.setSortColumnName("name");
                state.setTreeView(false);
            }
        
            setupTableState(state);
            
            TableModel model = new CoralTableModel(coralSession, i18nContext.getLocale());
            templatingContext.put("table", new TableTool(state, null, model));
        }
        catch(SearchException e)
        {
            throw new ProcessingException("cannot get root for list view", e);
        }
        catch(TableException e)
        {
            throw new ProcessingException("table tool setup failed", e);
        }
    }
    
    protected abstract String getTableStateName(CoralSession coralSession, SiteResource site);
    protected abstract Resource getTableRoot(CoralSession coralSession, SiteResource site)
        throws SearchException;
    protected abstract void setupTableState(TableState state);
}
