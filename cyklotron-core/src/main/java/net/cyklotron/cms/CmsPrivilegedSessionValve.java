package net.cyklotron.cms;

import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.pipeline.ConcurrencyControlValve;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;

/**
 * This valve checks if the currently logged in user is a administrator of the site being accessed,
 * or the system administrator. If so, it sets {@code HttpSession} flag
 * {@code ConcurrencyControlValve#PRIVILEGED_SESSION_MARKER} that will grant the user's request a
 * priority over non-authenticated or non-administrative users' requests in case of traffic
 * congestion.
 * 
 * @author rafal.krzewski@caltha.pl
 */
public class CmsPrivilegedSessionValve
    implements Valve
{
    @Override
    public void process(Context context)
        throws ProcessingException
    {
        if(context.getAttribute(ConcurrencyControlValve.PRIVILEGED_SESSION_MARKER) == null)
        {
            CoralSession coralSession = context.getAttribute(CoralSession.class);
            if(coralSession.getUserSubject().getId() != Subject.ANONYMOUS)
            {
                TemplatingContext templatingContext = context.getAttribute(TemplatingContext.class);
                CmsData cmsData = (CmsData)templatingContext.get("cmsData");
                HttpContext httpContext = context.getAttribute(HttpContext.class);
                if(cmsData != null && coralSession != null && httpContext != null)
                {
                    if(cmsData.checkAdministrator(coralSession))
                    {
                        httpContext.setSessionAttribute(
                            ConcurrencyControlValve.PRIVILEGED_SESSION_MARKER, Boolean.TRUE);
                    }
                }
            }
        }
    }
}
