package net.cyklotron.cms.modules.actions.periodicals;

import net.cyklotron.cms.periodicals.PeriodicalResourceData;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Publication time adding action.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: DeletePublicationTime.java,v 1.2 2005-01-24 10:27:17 pablo Exp $
 */
public class DeletePublicationTime
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
