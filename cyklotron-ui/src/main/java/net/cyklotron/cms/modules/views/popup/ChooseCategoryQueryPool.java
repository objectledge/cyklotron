package net.cyklotron.cms.modules.views.popup;

import net.labeo.Labeo;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.ARLTableModel;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.site.SiteResource;

/**
 * Choose category query set screen.
 *
 * @author <a href="pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ChooseCategoryQueryPool.java,v 1.1 2005-01-24 04:34:11 pablo Exp $
 */
public class ChooseCategoryQueryPool extends BaseCMSScreen
{
    protected CategoryQueryService categoryQueryService;
    
    protected TableService tableService;
    
    public ChooseCategoryQueryPool()
    {
        categoryQueryService = (CategoryQueryService)Labeo.getBroker().
            getService(CategoryQueryService.SERVICE_NAME);
        tableService = (TableService)Labeo.getBroker().
            getService(TableService.SERVICE_NAME); 
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession) 
        throws ProcessingException
    {
        SiteResource site = getSite();
        try
        {
            Resource queryRoot = categoryQueryService.getCategoryQueryPoolRoot(site);
            TableState state = tableService.getGlobalState(data, "cms:category,query,CategoryQueryPoolList:"+site.getIdString());
            if(state.isNew())
            {
                state.setRootId(queryRoot.getIdString());
                state.setViewType(TableConstants.VIEW_AS_LIST);
                state.setShowRoot(false);
            }
            TableModel model = new ARLTableModel(i18nContext.getLocale()());
            TableTool table = new TableTool(state, model,null);
            templatingContext.put("table", table);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve information", e);    
        }
    }
}
