package net.cyklotron.cms.modules.views.popup;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.table.CoralTableModel;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.CategoryService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;

/**
 * Choose category screen.
 *
 * @author <a href="pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ChooseCategory.java,v 1.3 2005-01-26 09:00:36 pablo Exp $
 */
public class ChooseCategory extends BaseCMSScreen
{
    protected CategoryService categoryService;
    
    public ChooseCategory(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, CategoryService categoryService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.categoryService = categoryService; 
    }
    
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) 
        throws ProcessingException
    {
        SiteResource site = getSite();
        try
        {
            Resource globalRoot = categoryService.getCategoryRoot(coralSession, null);
			String globalRootId = globalRoot.getIdString();
            TableState globalState = tableStateManager.getState(context, "cms:category,ChooseCategory:"+globalRootId);
            if(globalState.isNew())
            {
				globalState.setRootId(globalRootId);
				globalState.setTreeView(true);
				globalState.setShowRoot(true);
				globalState.setExpanded(globalRootId);	
				globalState.setPageSize(0);
            }
            TableModel globalModel = new CoralTableModel(coralSession, i18nContext.getLocale());
            TableTool globalTable = new TableTool(globalState, null, globalModel);
            templatingContext.put("global_table", globalTable);

			if(site != null)
			{            
				Resource localRoot = categoryService.getCategoryRoot(coralSession, site);
				String localRootId = localRoot.getIdString();
				TableState localState = tableStateManager.getState(context, "cms:category,ChooseCategory:"+localRootId);
				if(localState.isNew())
				{
					localState.setRootId(localRootId);
					localState.setTreeView(true);
					localState.setShowRoot(true);
					localState.setExpanded(localRootId);	
					localState.setPageSize(0);
				}
				TableModel localModel = new CoralTableModel(coralSession, i18nContext.getLocale());
				TableTool localTable = new TableTool(localState, null, localModel);
				templatingContext.put("local_table", localTable);
			}
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve information", e);    
        }
    }
}
