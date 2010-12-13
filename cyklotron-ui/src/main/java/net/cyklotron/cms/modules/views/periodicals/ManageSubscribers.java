package net.cyklotron.cms.modules.views.periodicals;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalResourceImpl;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.preferences.PreferencesService;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

public class ManageSubscribers
    extends BasePeriodicalsScreen
{
    /**
     * Creates a new instance of the view.
     * 
     * @param context request context
     * @param logger logger
     * @param preferencesService preferecnces service
     * @param cmsDataFactory CmsData factory
     * @param tableStateManager table state manager
     * @param periodicalsService periodicals service
     */
    public ManageSubscribers(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        PeriodicalsService periodicalsService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        periodicalsService);
    }

    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext context,
        CoralSession coralSession)
        throws ProcessingException
    {
        long perId = parameters.getLong("periodical_id", -1);
        PeriodicalResource periodical = null;
        try
        {
            if(perId != -1)
            {
                periodical = PeriodicalResourceImpl.getPeriodicalResource(coralSession, perId);
                templatingContext.put("periodical", periodical);
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("Exception occured", e);
        }
    }
}
