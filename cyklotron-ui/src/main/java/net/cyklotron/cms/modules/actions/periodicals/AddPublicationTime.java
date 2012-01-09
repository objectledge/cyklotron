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
 * @version $Id: AddPublicationTime.java,v 1.5 2005-06-02 11:15:01 pablo Exp $
 */
public class AddPublicationTime
    extends BasePeriodicalsAction
{
    
    
    public AddPublicationTime(Logger logger, StructureService structureService,
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
        int next = periodicalData.getPublicationTimes().size() + 1;
        parameters.add("publication_times", next);
		parameters.set("day_of_month_"+next, -1);
		parameters.set("day_of_week_"+next, -1);
		parameters.set("hour_"+next, 0);
    }
}
