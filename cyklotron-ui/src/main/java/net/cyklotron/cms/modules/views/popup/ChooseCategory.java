package net.cyklotron.cms.modules.views.popup;

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

import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.site.SiteResource;

/**
 * Choose category screen.
 *
 * @author <a href="pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ChooseCategory.java,v 1.2 2005-01-25 11:23:55 pablo Exp $
 */
public class ChooseCategory extends BaseCMSScreen
{
    protected CategoryService categoryService;
    
    protected TableService tableService;
    
    public ChooseCategory()
    {
        categoryService = (CategoryService)broker.
            getService(CategoryService.SERVICE_NAME);
        tableService = (TableService)broker.
            getService(TableService.SERVICE_NAME); 
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) 
        throws ProcessingException
    {
        SiteResource site = getSite();
        try
        {
            Resource globalRoot = categoryService.getCategoryRoot(null);
			String globalRootId = globalRoot.getIdString();
            TableState globalState = tableService.getGlobalState(data, "cms:category,ChooseCategory:"+globalRootId);
            if(globalState.isNew())
            {
				globalState.setRootId(globalRootId);
				globalState.setViewType(TableConstants.VIEW_AS_TREE);
				globalState.setShowRoot(true);
				globalState.setExpanded(globalRootId);	
				globalState.setPageSize(0);
            }
            TableModel globalModel = new ARLTableModel(i18nContext.getLocale()());
            TableTool globalTable = new TableTool(globalState, globalModel, null);
            templatingContext.put("global_table", globalTable);

			if(site != null)
			{            
				Resource localRoot = categoryService.getCategoryRoot(site);
				String localRootId = localRoot.getIdString();
				TableState localState = tableService.getGlobalState(data, "cms:category,ChooseCategory:"+localRootId);
				if(localState.isNew())
				{
					localState.setRootId(localRootId);
					localState.setViewType(TableConstants.VIEW_AS_TREE);
					localState.setShowRoot(true);
					localState.setExpanded(localRootId);	
					localState.setPageSize(0);
				}
				TableModel localModel = new ARLTableModel(i18nContext.getLocale()());
				TableTool localTable = new TableTool(localState, localModel, null);
				templatingContext.put("local_table", localTable);
			}
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve information", e);    
        }
    }
}
