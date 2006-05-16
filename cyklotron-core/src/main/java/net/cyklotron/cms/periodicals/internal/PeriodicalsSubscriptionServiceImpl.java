// 
// Copyright (c) 2003-2005, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
//   
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//   
// * Redistributions of source code must retain the above copyright notice,  
// this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
// this list of conditions and the following disclaimer in the documentation  
// and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
// nor the names of its contributors may be used to endorse or promote products  
// derived from this software without specific prior written permission. 
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
// POSSIBILITY OF SUCH DAMAGE. 
//

package net.cyklotron.cms.periodicals.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.periodicals.EmailPeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalsException;
import net.cyklotron.cms.periodicals.PeriodicalsNodeResource;
import net.cyklotron.cms.periodicals.PeriodicalsNodeResourceImpl;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.periodicals.PeriodicalsSubscriptionService;
import net.cyklotron.cms.periodicals.SubscriptionRequestResource;
import net.cyklotron.cms.periodicals.SubscriptionRequestResourceImpl;
import net.cyklotron.cms.site.SiteResource;

/**
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: PeriodicalsSubscriptionServiceImpl.java,v 1.1 2006-05-16 09:47:44 rafal Exp $
 */
public class PeriodicalsSubscriptionServiceImpl
    implements PeriodicalsSubscriptionService
{
    final private PeriodicalsService periodicalsService;

    /** pseudo-random number generator */
    final private Random random;

    public PeriodicalsSubscriptionServiceImpl(PeriodicalsService periodicalsService)
    {
        this.periodicalsService = periodicalsService;
        this.random = new Random();
    }

    // inherit doc
    public synchronized SubscriptionRequestResource getSubscriptionRequest(
        CoralSession coralSession, String cookie)
        throws PeriodicalsException
    {
        Resource[] res = coralSession.getStore().getResourceByPath(
            "/cms/sites/*/applications/periodicals/requests/" + cookie);
        if(res.length > 0)
        {
            return (SubscriptionRequestResourceImpl)res[0];
        }
        else
        {
            return null;
        }
    }

    // interit doc
    public synchronized String createSubsriptionRequest(CoralSession coralSession,
        SiteResource site, String email, String items)
        throws PeriodicalsException
    {
        Resource root = getSubscriptionChangeRequestsRoot(coralSession, site);
        String cookie;
        Resource[] res;
        do
        {
            cookie = getRandomCookie();
            res = coralSession.getStore().getResource(root, cookie);
        }
        while(res.length > 0);
        try
        {
            SubscriptionRequestResource request = SubscriptionRequestResourceImpl
                .createSubscriptionRequestResource(coralSession, cookie, root, email);
            request.setItems(items);
            request.update();
        }
        catch(Exception e)
        {
            throw new PeriodicalsException("failed to create subscription request record", e);
        }
        return cookie;
    }

    // inherit doc
    public synchronized void discardSubscriptionRequest(CoralSession coralSession, String cookie)
        throws PeriodicalsException
    {
        SubscriptionRequestResource r = getSubscriptionRequest(coralSession, cookie);
        if(r != null)
        {
            try
            {
                coralSession.getStore().deleteResource(r);
            }
            catch(EntityInUseException e)
            {
                throw new PeriodicalsException("failed to delete subscription change request", e);
            }
        }
    }

    public EmailPeriodicalResource[] getSubscribedEmailPeriodicals(CoralSession coralSession,
        SiteResource site, String email)
        throws PeriodicalsException
    {
        EmailPeriodicalResource[] periodicals = periodicalsService.getEmailPeriodicals(
            coralSession, site);
        List temp = new ArrayList();
        for(int i = 0; i < periodicals.length; i++)
        {
            EmailPeriodicalResource periodical = periodicals[i];
            if(periodical.getAddresses().indexOf(email) >= 0)
            {
                temp.add(periodical);
            }
        }
        EmailPeriodicalResource[] result = new EmailPeriodicalResource[temp.size()];
        temp.toArray(result);
        return result;
    }

    /**
     * Return the root node for email periodicals
     * 
     * @param site the site.
     * @return the periodicals root.
     */
    private PeriodicalsNodeResource getSubscriptionChangeRequestsRoot(CoralSession coralSession,
        SiteResource site)
        throws PeriodicalsException
    {
        PeriodicalsNodeResource applicationRoot = periodicalsService.getApplicationRoot(
            coralSession, site);
        Resource[] res = coralSession.getStore().getResource(applicationRoot, "requests");
        if(res.length == 0)
        {
            try
            {
                return PeriodicalsNodeResourceImpl.createPeriodicalsNodeResource(coralSession,
                    "requests", applicationRoot);
            }
            catch(InvalidResourceNameException e)
            {
                throw new RuntimeException("unexpected exception", e);
            }
        }
        else
        {
            return (PeriodicalsNodeResource)res[0];
        }
    }
    
    private String getRandomCookie()
    {
        String cookie = "0000000000000000".concat(Long.toString(random.nextLong(), 16));
        return cookie.substring(cookie.length() - 16, cookie.length());
    }    
}
