/*
 * Created on Oct 14, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.views.category.query;

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
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CategoryQueryList extends BaseCMSScreen
{
    protected CategoryQueryService categoryQueryService;
    
    protected TableService tableService;
    
    public CategoryQueryList()
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
            Resource queryRoot = categoryQueryService.getCategoryQueryRoot(site);
            TableState state = tableService.getGlobalState(data, "cms:category,query,CategoryQueryList:"+site.getIdString());
            if(state.isNew())
            {
                state.setRootId(queryRoot.getIdString());
                state.setViewType(TableConstants.VIEW_AS_LIST);
                state.setShowRoot(false);
            }
            TableModel model = new ARLTableModel(i18nContext.getLocale()());
            TableTool table = new TableTool(state, model, null);
            templatingContext.put("table", table);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve information", e);    
        }
    }
    
    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        return checkPermission(context, coralSession, "cms.category.query.modify");
    }
}
