package net.cyklotron.cms.modules.views.search;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.CoralTableModel;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.search.PoolResource;
import net.cyklotron.cms.search.PoolResourceData;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.search.SearchService;
import net.cyklotron.cms.site.SiteResource;

/**
 * A screen for editing index pools.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: EditPool.java,v 1.4 2005-01-26 09:00:39 pablo Exp $
 */
public class EditPool extends BaseSearchScreen
{
    
    public EditPool(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        SearchService searchService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, searchService);
        // TODO Auto-generated constructor stub
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
            TableState state = tableStateManager.getState(context,
                "cms.search.pool.indexes."+site.getName());
            if(state.isNew())
            {
                Resource root = searchService.getIndexesRoot(coralSession, site);

                state.setRootId(root.getIdString());
                state.setShowRoot(false);
                state.setSortColumnName("name");
                state.setTreeView(false);
            }
            
            TableModel model = new CoralTableModel(coralSession, i18nContext.getLocale());
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
}
