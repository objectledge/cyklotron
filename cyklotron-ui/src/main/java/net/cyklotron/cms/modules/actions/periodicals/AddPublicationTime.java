package net.cyklotron.cms.modules.actions.periodicals;

import net.cyklotron.cms.periodicals.PeriodicalResourceData;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Publication time adding action.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: AddPublicationTime.java,v 1.2 2005-01-24 10:27:17 pablo Exp $
 */
public class AddPublicationTime
    extends BasePeriodicalsAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        PeriodicalResourceData periodicalData = PeriodicalResourceData.getData(data, null, false);
        periodicalData.update(data);
        int next = periodicalData.getPublicationTimes().size() + 1;
        parameters.add("publication_times", next);
		parameters.set("day_of_month_"+next, -1);
		parameters.set("day_of_week_"+next, -1);
		parameters.set("hour_"+next, 0);
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return true;
    }
}
