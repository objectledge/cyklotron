package net.cyklotron.cms.modules.views.aggregation;

import java.util.Arrays;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
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
import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.aggregation.ImportResource;
import net.cyklotron.cms.aggregation.util.ImportResourceTargetSiteComparator;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteService;


/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: Exported.java,v 1.4 2005-02-03 22:23:47 pablo Exp $
 */
public class Exported
    extends BaseAggregationScreen
{
   public Exported(org.objectledge.context.Context context, Logger logger,
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
            TableColumn[] columns = new TableColumn[1];
            columns[0] = new TableColumn("targetSite", new ImportResourceTargetSiteComparator(i18nContext.getLocale()));
            ImportResource[] exports = aggregationService.getExports(coralSession, getSite());
            TableState state = tableStateManager.getState(context,"cms:screens:aggregation:Imported");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setPageSize(10);
            }
            TableModel model = new ListTableModel(Arrays.asList(exports), columns);
            templatingContext.put("table", new TableTool(state, null, model));
        }
        catch (Exception e)
        {
            throw new ProcessingException("failed to retrieve export information", e);
        }
    }
}
