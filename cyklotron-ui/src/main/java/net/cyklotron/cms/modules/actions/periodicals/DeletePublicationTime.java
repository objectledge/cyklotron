package net.cyklotron.cms.modules.actions.periodicals;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.periodicals.PeriodicalResourceData;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 * Publication time adding action.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: DeletePublicationTime.java,v 1.4 2005-03-08 10:52:53 pablo Exp $
 */
public class DeletePublicationTime
    extends BasePeriodicalsAction
{
    
    
    public DeletePublicationTime(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, PeriodicalsService periodicalsService,
        SiteService siteService)
    {
        super(logger, structureService, cmsDataFactory, periodicalsService, siteService);
        
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        PeriodicalResourceData periodicalData = PeriodicalResourceData.getData(httpContext, null, false);
        periodicalData.update(parameters);
        int counter = parameters.getInt("counter");
        parameters.remove("publication_times", counter);
		parameters.remove("day_of_month_"+counter);
		parameters.remove("day_of_week_"+counter);
		parameters.remove("hour_"+counter);
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return true;
    }
}
