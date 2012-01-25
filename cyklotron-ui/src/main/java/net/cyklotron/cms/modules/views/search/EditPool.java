package net.cyklotron.cms.modules.views.search;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableException;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.table.generic.PathTreeElement;
import org.objectledge.table.generic.PathTreeTableModel;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.CmsTool;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.search.IndexResource;
import net.cyklotron.cms.search.PoolResource;
import net.cyklotron.cms.search.PoolResourceData;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * A screen for editing index pools.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: EditPool.java,v 1.6 2007-02-25 14:18:52 pablo Exp $
 */
public class EditPool extends BaseSearchScreen
{
    private final SiteService siteService;

    public EditPool(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        SearchService searchService, SiteService siteService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, searchService);
        this.siteService = siteService;
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        // get pool if it is defined
        PoolResource pool = null;
        if(parameters.isDefined("pool_id"))
        {
            pool = getPool(coralSession, parameters);
            templatingContext.put("pool", pool);
        }
        // get pool resource data
        if(parameters.getBoolean("from_list",false))
        {
            PoolResourceData.removeData(httpContext, pool);
        }
        PoolResourceData poolData = PoolResourceData.getData(httpContext, pool);
        templatingContext.put("pool_data", poolData);
        
        // setup pool data and table data
        if(poolData.isNew())
        {
            poolData.init(pool);
        }
        else
        {
            poolData.update(parameters);
        }

        // get indexes list
        SiteResource site = getSite();
        try
        {
            PathTreeTableModel model = getIndexesTableModel(site, coralSession, i18nContext.getLocale());
            TableState state = tableStateManager.getState(context,
                "cms.search.pool.indexes."+site.getName());
            if(state.isNew())
            {
                state.setSortColumnName("name");
                state.setTreeView(true);
                String rootId = model.getId(null, model.getObjectByPath("/"));
                state.setRootId(rootId);
                state.setExpanded(rootId);
                state.setShowRoot(false);
                expandRelevantSites(state, model, site, pool);
            }
            
            templatingContext.put("table", new TableTool(state, null, model));
        }
        catch(SearchException e)
        {
            throw new ProcessingException("could not get indexes root", e);
        }
        catch(TableException e)
        {
            throw new ProcessingException("table tool setup failed", e);
        }
    }
    
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        if(!cmsData.isApplicationEnabled("search"))
        {
            logger.debug("Application 'search' not enabled in site");
            return false;
        }
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        if(parameters.isDefined("pool_id"))
        {
            return checkPermission(context, coralSession, "cms.search.pool.modify");
        }
        else
        {
            return checkPermission(context, coralSession, "cms.search.pool.add");
        }
    }
    
    private PathTreeTableModel<PathTreeElement> getIndexesTableModel(SiteResource currentSite,
        CoralSession coralSession, Locale locale)
        throws TableException, SearchException
    {
        PathTreeTableModel<PathTreeElement> model = new PathTreeTableModel<PathTreeElement>(
            PathTreeElement.getTableColumn("name", locale));
        model.bind("/", new PathTreeElement("/", "root"));
        
        for(SiteResource site : siteService.getSites(coralSession))
        {
            if(coralSession.getUserSubject().hasRole(site.getAdministrator()))
            {
                model.bind("/"+site.getName(), new PathTreeElement(site.getName(), "site"));
                for(Resource res: searchService.getIndexesRoot(coralSession, site).getChildren())
                {
                    if(res instanceof IndexResource)
                    {
                        PathTreeElement elem = new PathTreeElement(res.getName(), "index");
                        elem.set("id", res.getIdObject());
                        model.bind("/"+site.getName()+"/"+res.getName(), elem);
                    }
                }
            }
        }

        return model;
    }
    
    private void expandRelevantSites(TableState state, PathTreeTableModel<PathTreeElement> model, SiteResource currentSite, PoolResource pool)
    {
        Set<SiteResource> sites = new HashSet<SiteResource>();
        sites.add(currentSite);
        if(pool != null)
        {
            for(Resource index : (List<Resource>)pool.getIndexes())
            {
                sites.add(CmsTool.getSite(index));
            }            
        }
        for(SiteResource site : sites)
        {
            state.setExpanded(model.getId("/", model.getObjectByPath("/"+site.getName())));
        }
    }
}
