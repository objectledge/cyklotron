package net.cyklotron.cms.modules.views.aggregation;

import java.util.ArrayList;
import java.util.Arrays;

import net.cyklotron.cms.aggregation.AggregationConstants;
import net.cyklotron.cms.aggregation.ImportResource;
import net.cyklotron.cms.aggregation.RecommendationResource;
import net.cyklotron.cms.aggregation.util.SourceNameComparator;
import net.cyklotron.cms.aggregation.util.SourceSiteNameComparator;
import net.cyklotron.cms.aggregation.util.TargetSiteNameComparator;
import net.labeo.services.resource.table.ModificationTimeComparator;
import net.labeo.services.resource.table.ResourceListTableModel;
import net.labeo.services.table.ListTableModel;
import net.labeo.services.table.TableColumn;
import net.labeo.services.table.TableConstants;
import net.labeo.services.table.TableModel;
import net.labeo.services.table.TableService;
import net.labeo.services.table.TableState;
import net.labeo.services.table.TableTool;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: Recommendations.java,v 1.1 2005-01-24 04:34:51 pablo Exp $
 */
public class Recommendations 
    extends BaseAggregationScreen
{
    protected TableService tableService;

    public Recommendations()
        throws ProcessingException
    {
        tableService = (TableService)broker.getService(TableService.SERVICE_NAME);
    }

    /* 
     * (overriden)
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession) 
        throws ProcessingException
    {
        try
        {
            TableColumn[] columns = new TableColumn[4];
            columns[0] = new TableColumn("modification.time", new ModificationTimeComparator());
            columns[1] = new TableColumn("sourceSite", new SourceSiteNameComparator(i18nContext.getLocale()()));
            columns[2] = new TableColumn("targetSite", new TargetSiteNameComparator(i18nContext.getLocale()()));            
            columns[3] = new TableColumn("source", new SourceNameComparator(i18nContext.getLocale()()));            
            
            RecommendationResource[] pending = aggregationService.
                getPendingRecommendations(getSite());
            TableState state = tableService.getLocalState(data, "cms:screens:aggregation:Recommendations-pending");
            if(state.isNew())
            {
                state.setViewType(TableConstants.VIEW_AS_LIST);
                state.setPageSize(10);
            }
            TableModel model = new ListTableModel(Arrays.asList(pending), columns);
            templatingContext.put("pending", new TableTool(state, model, null));
            RecommendationResource[] submitted = aggregationService.
                getSubmittedRecommendations(getSite(), coralSession.getUserSubject());            
            
            TableState state2 = tableService.getLocalState(data, "cms:screens:aggregation:Recommendations-submitted");
            if(state2.isNew())
            {
                state2.setViewType(TableConstants.VIEW_AS_LIST);
                state2.setPageSize(10);
            }
            TableModel model2 = new ListTableModel(Arrays.asList(submitted), columns);
            templatingContext.put("submitted", new TableTool(state2, model2, null));
            
            ImportResource[] imports = aggregationService.getImports(getSite());
            ArrayList changed = new ArrayList(imports.length);
            for (int i = 0; i < imports.length; i++)
            {
                if(imports[i].getState() == AggregationConstants.IMPORT_MODIFIED)
                {
                    changed.add(imports[i]);
                }
            }
            TableState state3 = tableService.getLocalState(data, "cms:screens:aggregation:Recommendations-changed");
            if(state.isNew())
            {
                state.setViewType(TableConstants.VIEW_AS_LIST);
                state.setPageSize(10);
            }
            TableModel model3 = new ResourceListTableModel(changed, i18nContext.getLocale()());
            templatingContext.put("changed", new TableTool(state3, model3, null));

        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve information", e);                
        }
    }
}
