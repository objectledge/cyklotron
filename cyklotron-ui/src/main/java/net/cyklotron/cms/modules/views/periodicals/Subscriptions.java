/*
 * Created on Nov 5, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.views.periodicals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import net.labeo.Labeo;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.table.NameComparator;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.BaseSkinableScreen;
import net.cyklotron.cms.periodicals.EmailPeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalsException;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.periodicals.SubscriptionRequestResource;
import net.cyklotron.cms.site.SiteResource;

/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Subscriptions
    extends BaseSkinableScreen
{
    /** Periodicals service */
    protected PeriodicalsService periodicalsService;    

    public Subscriptions()
    {
        periodicalsService = (PeriodicalsService)Labeo.getBroker().
            getService(PeriodicalsService.SERVICE_NAME);
    }

    public void prepareDefault(RunData data, Context context)
        throws ProcessingException
    {
        try
        {
            EmailPeriodicalResource[] periodicals = periodicalsService.
                getEmailPeriodicals(getSite());
            List list = Arrays.asList(periodicals);
            Collections.sort(list, new NameComparator(i18nContext.getLocale()()));                
            templatingContext.put("periodicals", list);
        }
        catch(PeriodicalsException e)
        {
            throw new ProcessingException("failed to retrieve data", e);
        }
    }
    
    public void prepareTicketSent(RunData data, Context context)
        throws ProcessingException
    {
    }
    
    public void prepareInvalidTicket(RunData data, Context context)
    {
    }
    
    public void prepareEdit(RunData data, Context context)
        throws ProcessingException
    {
        try
        {
            String cookie = parameters.get("cookie");
            templatingContext.put("cookie", cookie);
            SiteResource site = getSite();
            SubscriptionRequestResource req = periodicalsService.getSubscriptionRequest(cookie);
            templatingContext.put("email", req.getEmail());
            List periodicals = Arrays.asList(periodicalsService.getEmailPeriodicals(site));
            List selectedList = Arrays.asList(periodicalsService.getSubscribedEmailPeriodicals(site, req.getEmail()));
            Set selected = new HashSet(selectedList);
            templatingContext.put("periodicals", periodicals);
            templatingContext.put("selected", selected);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve information", e);
        }
    }
    
    public void prepareConfirm(RunData data, Context context)
        throws ProcessingException
    {
        try
        {
            String cookie = parameters.get("cookie");
            templatingContext.put("cookie", cookie);
            SubscriptionRequestResource req = periodicalsService.getSubscriptionRequest(cookie);
            templatingContext.put("email", req.getEmail());
            StringTokenizer st = new StringTokenizer(req.getItems(), " ");
            List selected = new ArrayList();
            while (st.hasMoreTokens())
            {
                long periodicalId = Long.parseLong(st.nextToken());
                try
                {
                    Resource periodical = coralSession.getStore().getResource(periodicalId);
                    selected.add(periodical);
                }
                catch(EntityDoesNotExistException e)
                {
                    // periodical was deleted, ignore
                }
            }
            templatingContext.put("selected", selected);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to retrieve information", e);
        }
    }
    
    public String getState(RunData data)
        throws ProcessingException
    {
        
        if("ticket_sent".equals(templatingContext.get("result")))
        {
            return "TicketSent";
        }
        String cookie = parameters.get("cookie","");
        if(cookie.length() > 0)
        {
            try
            {
                SubscriptionRequestResource req = periodicalsService.getSubscriptionRequest(cookie);
                if (req == null)
                {
                    return "InvalidTicket";
                }
                else if (req.getItems() == null)
                {
                    return "Edit";
                }
                else
                {
                    return "Confirm";
                }
            }
            catch(Exception e)
            {
                throw new ProcessingException("failed to validate cookie", e);
            }
        }
        return "Default";
    }
}
