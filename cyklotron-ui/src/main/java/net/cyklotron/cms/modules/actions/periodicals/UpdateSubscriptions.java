/*
 * Created on Nov 7, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.actions.periodicals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.periodicals.EmailPeriodicalResource;
import net.cyklotron.cms.periodicals.SubscriptionRequestResource;
import net.cyklotron.cms.site.SiteResource;

/**
 * @author <a href="rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: UpdateSubscriptions.java,v 1.2 2005-01-24 10:27:17 pablo Exp $
 */
public class UpdateSubscriptions extends BasePeriodicalsAction
{
    /**
     * {@inheritdoc}
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException, NotFoundException
    {
        Context context = data.getContext();
        try
        {
            String cookie = parameters.get("cookie","");
            if (cookie.length() == 0)
            {
                templatingContext.put("result", "no_cookie");
                return;
            }
            templatingContext.put("cookie", cookie);
            SubscriptionRequestResource req = periodicalsService.getSubscriptionRequest(cookie);
            String email = req.getEmail();
            Subject rootSubject = coralSession.getSecurity().getSubject(Subject.ROOT);
            if (req == null)
            {
                // invalid cookie - screen will complain
                return;
            }
            boolean subscribe = parameters.getBoolean("subscribe", false);
            if(subscribe)
            {
                StringTokenizer st = new StringTokenizer(req.getItems(), " ");
                Set selected = new HashSet();
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
                synchronized(periodicalsService)
                {
                    Iterator i = selected.iterator();
                    while(i.hasNext())
                    {
                        EmailPeriodicalResource periodical = (EmailPeriodicalResource)i.next();
                        subscribe(periodical, email, rootSubject);
                    }
                }
            }
            else
            {
                Set selected = new HashSet();
                String[] ids = parameters.getStrings("selected");
                for(int i = 0; i < ids.length; i++)
                {
                    long periodicalId = Long.parseLong(ids[i]);
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
                SiteResource site = getSite(context);
                EmailPeriodicalResource[] subscribedArray = periodicalsService.getSubscribedEmailPeriodicals(site, email);
                Set subscribed = new HashSet(Arrays.asList(subscribedArray));
                EmailPeriodicalResource[] periodicals = periodicalsService.getEmailPeriodicals(site);
                for (int i = 0; i < periodicals.length; i++)
                {
                    EmailPeriodicalResource periodical = periodicals[i];
                    if(selected.contains(periodical) && !subscribed.contains(periodical))
                    {
                        subscribe(periodical, email, rootSubject);
                    }
                    if(!selected.contains(periodical) && subscribed.contains(periodical))
                    {
                        unsubscribe(periodical, email, rootSubject);
                    }
                }
            }
            // success
            templatingContext.put("result", "updated_successfuly");
            parameters.remove("cookie");
            periodicalsService.discardSubscriptionRequest(cookie);
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
    }

    public void subscribe(EmailPeriodicalResource periodical, String email, Subject subject)
        throws Exception
    {
        if(periodical.getAddresses().indexOf(email) < 0)
        {
            periodical.setAddresses(periodical.getAddresses()+"\n"+email);
            periodical.update(subject);
        }
    }
    
    public void unsubscribe(EmailPeriodicalResource periodical, String email, Subject subject)
        throws Exception
    {
        if(periodical.getAddresses().indexOf(email) >= 0)
        {
            String addresses = periodical.getAddresses();
            int i1 = addresses.indexOf(email);
            int i2 = addresses.indexOf('\n', i1);
            if(i2 > 0)
            {
                addresses = addresses.substring(0,i1).concat(addresses.substring(i2+1));
            }
            else
            {
                addresses = addresses.substring(0, i1);
            }
            periodical.setAddresses(addresses);
            periodical.update(subject);
        }
    }
    
    public boolean checkAccessRights(Context context) throws ProcessingException
    {
        return true;
    }

    public boolean requiresLogin(RunData data)
            throws ProcessingException
    {
        return false;
    }
}
