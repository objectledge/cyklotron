package net.cyklotron.cms.modules.views.aggregation;

import java.util.ArrayList;
import java.util.Arrays;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.table.ResourceListTableModel;
import org.objectledge.coral.table.comparator.ModificationTimeComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableColumn;
import org.objectledge.table.TableModel;
import org.objectledge.table.TableState;
import org.objectledge.table.TableStateManager;
import org.objectledge.table.TableTool;
import org.objectledge.table.generic.ListTableModel;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.aggregation.AggregationConstants;
import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.aggregation.ImportResource;
import net.cyklotron.cms.aggregation.RecommendationResource;
import net.cyklotron.cms.aggregation.util.SourceNameComparator;
import net.cyklotron.cms.aggregation.util.SourceSiteNameComparator;
import net.cyklotron.cms.aggregation.util.TargetSiteNameComparator;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteService;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: Recommendations.java,v 1.4 2005-03-08 10:55:50 pablo Exp $
 */
public class Recommendations 
    extends BaseAggregationScreen
{

    public Recommendations(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        SiteService siteService, AggregationService aggregationService,
        SecurityService securityService, TableStateManager tableStateManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, siteService, aggregationService,
                        securityService, tableStateManager);
        
    }
    /* 
     * (overriden)
     */
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) 
        throws ProcessingException
    {
        try
        {
            TableColumn[] columns = new TableColumn[4];
            columns[0] = new TableColumn("modification.time", new ModificationTimeComparator());
            columns[1] = new TableColumn("sourceSite", new SourceSiteNameComparator(i18nContext.getLocale()));
            columns[2] = new TableColumn("targetSite", new TargetSiteNameComparator(i18nContext.getLocale()));            
            columns[3] = new TableColumn("source", new SourceNameComparator(i18nContext.getLocale()));            
            
            RecommendationResource[] pending = aggregationService.
                getPendingRecommendations(coralSession, getSite());
            TableState state = tableStateManager.getState(context, "cms:screens:aggregation:Recommendations-pending");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
            }
            TableModel model = new ListTableModel(Arrays.asList(pending), columns);
            templatingContext.put("pending", new TableTool(state, null, model));
            RecommendationResource[] submitted = aggregationService.
                getSubmittedRecommendations(coralSession, getSite(), coralSession.getUserSubject());            
            
            TableState state2 = tableStateManager.getState(context, "cms:screens:aggregation:Recommendations-submitted");
            if(state2.isNew())
            {
                state2.setTreeView(false);
                state2.setPageSize(10);
            }
            TableModel model2 = new ListTableModel(Arrays.asList(submitted), columns);
            templatingContext.put("submitted", new TableTool(state2, null, model2));
            
            ImportResource[] imports = aggregationService.getImports(coralSession, getSite());
            ArrayList changed = new ArrayList(imports.length);
            for (int i = 0; i < imports.length; i++)
            {
                if(imports[i].getState(coralSession) == AggregationConstants.IMPORT_MODIFIED)
                {
                    changed.add(imports[i]);
                }
            }
            TableState state3 = tableStateManager.getState(context, "cms:screens:aggregation:Recommendations-changed");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
            }
            TableModel model3 = new ResourceListTableModel(changed, i18nContext.getLocale());
            templatingContext.put("changed", new TableTool(state3, null, model3));

        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve information", e);                
        }
    }
}
