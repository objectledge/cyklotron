package net.cyklotron.cms.modules.views.search;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.ARLTableModel;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.search.PoolResource;
import net.cyklotron.cms.search.PoolResourceData;
import net.cyklotron.cms.search.SearchException;
import net.cyklotron.cms.site.SiteResource;

/**
 * A screen for editing index pools.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: EditPool.java,v 1.3 2005-01-25 11:24:16 pablo Exp $
 */
public class EditPool extends BaseSearchScreen
{
    /** table service for index list display. */
    TableService tableService = null;

    public EditPool()
    {
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        // get pool if it is defined
        PoolResource pool = null;
        if(parameters.isDefined("pool_id"))
        {
            pool = getPool(data);
            templatingContext.put("pool", pool);
        }
        // get pool resource data
        if(parameters.get("from_list").asBoolean(false))
        {
            PoolResourceData.removeData(data, pool);
        }
        PoolResourceData poolData = PoolResourceData.getData(data, pool);
        templatingContext.put("pool_data", poolData);
        
        // setup pool data and table data
        if(poolData.isNew())
        {
            poolData.init(pool);
        }
        else
        {
            poolData.update(data);
        }

        // get indexes list
        SiteResource site = getSite();
        try
        {
            TableState state = tableService.getLocalState(data,
                "cms.search.pool.indexes."+site.getName());
            if(state.isNew())
            {
                Resource root = searchService.getIndexesRoot(site);

                state.setRootId(root.getIdString());
                state.setShowRoot(false);
                state.setSortColumnName("name");
                state.setTreeView(false);
            }
            
            TableModel model = new ARLTableModel(i18nContext.getLocale()());
            templatingContext.put("table", new TableTool(state, model, null));
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
