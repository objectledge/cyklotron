package net.cyklotron.cms.modules.views.periodicals;

import java.util.Arrays;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.table.comparator.NameComparator;
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
import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 * Periodicals screen. 
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: EmailPeriodicals.java,v 1.4 2005-03-08 11:07:58 pablo Exp $
 */
public class EmailPeriodicals 
    extends BasePeriodicalsScreen
{


    public EmailPeriodicals(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, PeriodicalsService periodicalsService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        periodicalsService);
        
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
            columns[0] = new TableColumn("name", new NameComparator(i18nContext.getLocale()));
            PeriodicalResource[] periodicals = periodicalsService.
                getEmailPeriodicals(coralSession, getSite());
            TableState state = tableStateManager.getState(context, "cms:screens:periodicals:EmailPeriodicals");
            if(state.isNew())
            {
                state.setTreeView(false);
                state.setSortColumnName("name");
                state.setPageSize(10);
            }
            TableModel model = new ListTableModel(Arrays.asList(periodicals), columns);
            templatingContext.put("periodicals", new TableTool(state, null, model));
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve information", e);                
        }
    }
}
