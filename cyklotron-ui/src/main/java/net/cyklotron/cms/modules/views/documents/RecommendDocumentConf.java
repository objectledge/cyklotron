package net.cyklotron.cms.modules.views.documents;

import org.jcontainer.dna.Logger;
import net.labeo.services.logging.LoggingService;
import net.labeo.services.resource.table.ARLTableModel;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableException;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Configuration;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;

public class RecommendDocumentConf
	extends BaseCMSScreen
{
	protected Logger log;
    
	protected StructureService structureService;

	private TableService tableService;
		    
	public RecommendDocumentConf()
	{
		log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("documents");
		tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
    }

    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        // get config
        Parameters componentConfig = prepareComponentConfig(parameters, templatingContext);
		try
		{
			NavigationNodeResource home = getHomePage();
			
			TableState state = tableService.getLocalState(data, "cms:screens:documents:recommend_document_conf");
			if(state.isNew())
			{
				state.setViewType(TableConstants.VIEW_AS_TREE);
				state.setMultiSelect(false);
				String rootId = home.getIdString();
				state.setRootId(rootId);
				state.setCurrentPage(0);
				state.setShowRoot(true);
				state.setExpanded(rootId);
				state.setPageSize(0);
				state.setSortColumnName("name");
			}

			TableModel model = new ARLTableModel(i18nContext.getLocale()());
			TableTool helper = new TableTool(state, model, null);
			templatingContext.put("table", helper);
		}
		catch(TableException e)
		{
			throw new ProcessingException("Cannot create TableTool", e);
		}

        
    }
}
