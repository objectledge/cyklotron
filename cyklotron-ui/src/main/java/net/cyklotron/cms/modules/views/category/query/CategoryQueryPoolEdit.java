package net.cyklotron.cms.modules.views.category.query;

import net.cyklotron.cms.category.query.CategoryQueryException;
import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryPoolResourceData;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.category.query.CategoryQueryUtil;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.site.SiteResource;
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

/**
 * A screen for editing category query pools.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryPoolEdit.java,v 1.1 2005-01-24 04:35:06 pablo Exp $
 */
public class CategoryQueryPoolEdit extends BaseCMSScreen
{
	protected CategoryQueryService categoryQueryService;
    
	protected TableService tableService;
    
    public CategoryQueryPoolEdit()
    {
        tableService = (TableService) broker.getService(TableService.SERVICE_NAME);
        categoryQueryService =
            (CategoryQueryService) broker.getService(CategoryQueryService.SERVICE_NAME);
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession) throws ProcessingException
    {
        // get pool if it is defined
		CategoryQueryPoolResource pool = null;
        if (parameters.get(CategoryQueryUtil.QUERY_POOL_PARAM).isDefined())
        {
            pool = CategoryQueryUtil.getPool(coralSession, data);
            templatingContext.put("pool", pool);
        }
        // get pool resource data
        if (parameters.get("from_list").asBoolean(false))
        {
			CategoryQueryPoolResourceData.removeData(data, pool);
        }
		CategoryQueryPoolResourceData poolData = CategoryQueryPoolResourceData.getData(data, pool);
        templatingContext.put("pool_data", poolData);

        // setup pool data and table data
        if (poolData.isNew())
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
            TableState state =
                tableService.getLocalState(data, "cms.category.query.pool.queries." + site.getName());
            if (state.isNew())
            {
                Resource root = categoryQueryService.getCategoryQueryRoot(site);

                state.setRootId(root.getIdString());
                state.setShowRoot(false);
                state.setSortColumnName("name");
                state.setViewType(TableConstants.VIEW_AS_LIST);
            }

            TableModel model = new ARLTableModel(i18nContext.getLocale()());
            templatingContext.put("table", new TableTool(state, model, null));
        }
        catch (CategoryQueryException e)
        {
            throw new ProcessingException("could not get queries root", e);
        }
        catch (TableException e)
        {
            throw new ProcessingException("table tool setup failed", e);
        }
    }

    public boolean checkAccess(RunData data) throws ProcessingException
    {
        if (parameters.get(CategoryQueryUtil.QUERY_POOL_PARAM).isDefined())
        {
            return checkPermission(context, coralSession, "cms.category.query.pool.modify");
        }
        else
        {
            return checkPermission(context, coralSession, "cms.category.query.pool.add");
        }
    }
}
