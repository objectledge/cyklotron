/*
 * Created on Nov 6, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.components.periodicals;

import net.labeo.services.resource.Resource;
import net.labeo.webcore.ProcessingException;

import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalsException;
import net.cyklotron.cms.site.SiteResource;


/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Periodicals 
    extends BasePeriodicalsComponent
{
    protected PeriodicalResource[] getPeriodicals(SiteResource site)
        throws ProcessingException
    {
        try
        {
            return periodicalsService.getPeriodicals(site);
        }
        catch(PeriodicalsException e)
        {
            throw new ProcessingException("failed to retrieve periodicals", e);
        }
    }

    protected String getSessionKey()
    {
        return "cms:periodicals,Periodicals";
    }    

	protected Resource getPeriodicalRoot(SiteResource site)
	throws PeriodicalsException
	{
		return periodicalsService.getPeriodicalsRoot(site);
	}
}
