package net.cyklotron.cms.modules.views.documents;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
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

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;

public class RecommendDocumentConf
	extends BaseCMSScreen
{
	protected StructureService structureService;

    
    
    public RecommendDocumentConf(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StructureService structureService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.structureService = structureService;
    }
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        // get config
        Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
		try
		{
			NavigationNodeResource home = getHomePage();
			
			TableState state = tableStateManager.getState(context, "cms:screens:documents:recommend_document_conf");
			if(state.isNew())
			{
				state.setTreeView(true);
			    String rootId = home.getIdString();
				state.setRootId(rootId);
				state.setCurrentPage(0);
				state.setShowRoot(true);
				state.setExpanded(rootId);
				state.setPageSize(0);
				state.setSortColumnName("name");
			}

			TableModel model = new CoralTableModel(coralSession, i18nContext.getLocale());
			TableTool helper = new TableTool(state, null, model);
			templatingContext.put("table", helper);
		}
		catch(TableException e)
		{
			throw new ProcessingException("Cannot create TableTool", e);
		}

        
    }
}
