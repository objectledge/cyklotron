/*
 * Created on Oct 28, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.actions.periodicals;

import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalResourceImpl;

/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PublishPeriodical extends BasePeriodicalsAction
{
    // inherit doc
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) 
        throws ProcessingException, NotFoundException
    {
        try
        {
            long periodicalId = parameters.getLong("periodical_id");
            PeriodicalResource periodical = null;
            periodical =
                PeriodicalResourceImpl.getPeriodicalResource(coralSession, periodicalId);
            periodicalsService.publishNow(periodical); 
        }
        catch(Exception e)
        {
            data.getContext().put("result", "exception");
            data.getContext().put("trace", StringUtils.stackTrace(e));
        }        
    }
}
