package net.cyklotron.cms.modules.views.category.query;

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
import net.cyklotron.cms.category.query.CategoryQueryException;
import net.cyklotron.cms.category.query.CategoryQueryPoolResource;
import net.cyklotron.cms.category.query.CategoryQueryPoolResourceData;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.category.query.CategoryQueryUtil;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;

/**
 * A screen for editing category query pools.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: CategoryQueryPoolEdit.java,v 1.4 2005-01-26 06:44:10 pablo Exp $
 */
public class CategoryQueryPoolEdit extends BaseCMSScreen
{
	protected CategoryQueryService categoryQueryService;
    

    
    public CategoryQueryPoolEdit(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, CategoryQueryService categoryQueryService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.categoryQueryService =categoryQueryService;
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) throws ProcessingException
    {
        // get pool if it is defined
		CategoryQueryPoolResource pool = null;
        if (parameters.isDefined(CategoryQueryUtil.QUERY_POOL_PARAM))
        {
            pool = CategoryQueryUtil.getPool(coralSession, parameters);
            templatingContext.put("pool", pool);
        }
        // get pool resource data
        if (parameters.getBoolean("from_list",false))
        {
			CategoryQueryPoolResourceData.removeData(httpContext, pool);
        }
		CategoryQueryPoolResourceData poolData = CategoryQueryPoolResourceData.getData(httpContext, pool);
        templatingContext.put("pool_data", poolData);

        // setup pool data and table data
        if (poolData.isNew())
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
            TableState state =
                tableStateManager.getState(context, "cms.category.query.pool.queries." + site.getName());
            if (state.isNew())
            {
                Resource root = categoryQueryService.getCategoryQueryRoot(coralSession, site);

                state.setRootId(root.getIdString());
                state.setShowRoot(false);
                state.setSortColumnName("name");
                state.setTreeView(false);
            }

            TableModel model = new CoralTableModel(coralSession,i18nContext.getLocale());
            templatingContext.put("table", new TableTool(state, null, model));
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

    public boolean checkAccessRights(Context context) throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        if (parameters.isDefined(CategoryQueryUtil.QUERY_POOL_PARAM))
        {
            return checkPermission(context, coralSession, "cms.category.query.pool.modify");
        }
        else
        {
            return checkPermission(context, coralSession, "cms.category.query.pool.add");
        }
    }
}
