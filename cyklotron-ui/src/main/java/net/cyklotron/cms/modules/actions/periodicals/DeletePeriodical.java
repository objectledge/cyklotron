package net.cyklotron.cms.modules.actions.periodicals;

import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalResourceImpl;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Periodical delete action.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: DeletePeriodical.java,v 1.2 2005-01-24 10:27:17 pablo Exp $
 */
public class DeletePeriodical
    extends BasePeriodicalsAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();

		long periodicalId = parameters.getLong("periodical_id", -1);
		if(periodicalId == -1)
		{
			throw new ProcessingException("Periodical id couldn't be found");
		}
			
		try
		{
			PeriodicalResource periodical = PeriodicalResourceImpl.getPeriodicalResource(coralSession, periodicalId);
			Resource[] publicationTimes = coralSession.getStore().getResource(periodical);
			for(int i = 0; i < publicationTimes.length; i++)
			{
				coralSession.getStore().deleteResource(publicationTimes[i]);		
			}
    	    coralSession.getStore().deleteResource(periodical);
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace", new StackTrace(e));
            log.error("problem adding a periodical", e);
            return;
        }
	}

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return true;
    }
}
