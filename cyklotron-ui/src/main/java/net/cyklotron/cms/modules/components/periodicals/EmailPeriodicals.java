/*
 * Created on Nov 6, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.components.periodicals;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.periodicals.EmailPeriodicalsRootResource;
import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalsException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureService;
import net.labeo.Labeo;
import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EmailPeriodicals 
    extends BasePeriodicalsComponent
{
    protected StructureService structureService;
    
    public EmailPeriodicals()
    {
        structureService = (StructureService)Labeo.getBroker().
            getService(StructureService.SERVICE_NAME);
    }
    
    protected PeriodicalResource[] getPeriodicals(SiteResource site)
        throws ProcessingException
    {
        try
        {
            return periodicalsService.getEmailPeriodicals(site);
        }
        catch(PeriodicalsException e)
        {
            throw new ProcessingException("failed to retrieve periodicals", e);
        }
    }
    
    protected String getSessionKey()
    {
        return "cms:periodicals,EmailPeriodicals";
    }
    
    public void prepareDefault(RunData data, Context context)
        throws ProcessingException
    {
        super.prepareDefault(data, context);
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        try
        {
			EmailPeriodicalsRootResource root =
				periodicalsService.getEmailPeriodicalsRoot(cmsData.getSite());
			templatingContext.put("subscriptionNode", root.getSubscriptionNode());
        }
        catch (PeriodicalsException e)
        {
        	throw new ProcessingException("cannot get email periodicals root", e);
        }
    }

    protected Resource getPeriodicalRoot(SiteResource site)
	throws PeriodicalsException
    {
        return periodicalsService.getEmailPeriodicalsRoot(site);
    }
}
