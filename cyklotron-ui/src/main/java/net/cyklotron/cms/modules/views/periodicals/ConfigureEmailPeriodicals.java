package net.cyklotron.cms.modules.views.periodicals;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.periodicals.EmailPeriodicalsRootResource;
import net.cyklotron.cms.periodicals.PeriodicalsException;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Configuration for subscriptins screen for email periodicals subscriptions. 
 * 
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: ConfigureEmailPeriodicals.java,v 1.2 2005-01-25 11:24:04 pablo Exp $
 */
public class ConfigureEmailPeriodicals 
    extends BasePeriodicalsScreen
{
    /* 
     * (overriden)
     */
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession) 
        throws ProcessingException
    {
    	CmsData cmsData = cmsDataFactory.getCmsData(context);
    	try
        {
            EmailPeriodicalsRootResource root =
            	periodicalsService.getEmailPeriodicalsRoot(cmsData.getSite());
            templatingContext.put("subscription_node", root.getSubscriptionNode());
        }
        catch (PeriodicalsException e)
        {
        	throw new ProcessingException("cannot get email periodicals root", e);
        }
    }
}
