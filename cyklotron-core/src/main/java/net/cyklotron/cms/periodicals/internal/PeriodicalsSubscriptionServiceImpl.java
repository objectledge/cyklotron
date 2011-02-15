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



import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.session.CoralSession;

import net.cyklotron.cms.confirmation.ConfirmationRequestException;
import net.cyklotron.cms.confirmation.EmailConfirmationRequestResource;
import net.cyklotron.cms.confirmation.EmailConfirmationRequestServiceImpl;
import net.cyklotron.cms.periodicals.EmailPeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalsException;
import net.cyklotron.cms.periodicals.PeriodicalsSubscriptionService;
import net.cyklotron.cms.periodicals.UnsubscriptionInfo;
import net.cyklotron.cms.site.SiteResource;

/**
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: PeriodicalsSubscriptionServiceImpl.java,v 1.9 2007-11-18 21:23:25 rafal Exp $
 */
public class PeriodicalsSubscriptionServiceImpl 
    implements PeriodicalsSubscriptionService
{
    private static EmailConfirmationRequestServiceImpl emailConfirmationRequestService;
    

    public PeriodicalsSubscriptionServiceImpl(EmailConfirmationRequestServiceImpl emailConfirmationRequestService)
    {
        this.emailConfirmationRequestService = emailConfirmationRequestService;
    }

    /**
     * {@inheritDoc}
     */
    public void createEncryptionKey() throws PeriodicalsException
    {
        try
        {
            emailConfirmationRequestService.createEncryptionKey();
        }
        catch(Exception e)
        {
            throw new PeriodicalsException("failed to create new encryption key", e);
        }
    }

    // inherit doc
    public synchronized EmailConfirmationRequestResource getSubscriptionRequest(
        CoralSession coralSession, String cookie)
        throws PeriodicalsException
    {
        try
        {
            return emailConfirmationRequestService.getEmailConfirmationRequest(coralSession, cookie);
        }
        catch(ConfirmationRequestException e)
        {
            throw new PeriodicalsException("failed to get email confirmation request resource", e);
        }
    }

    // interit doc
    public synchronized String createSubscriptionRequest(CoralSession coralSession,
        SiteResource site, String email, String data)
        throws PeriodicalsException
    {
        try
        {
            return emailConfirmationRequestService.createEmailConfirmationRequest(coralSession, email, data);
        }
        catch(Exception e)
        {
            throw new PeriodicalsException("failed to create subscription request record", e);
        }
    }

    // inherit doc
    public synchronized void discardSubscriptionRequest(CoralSession coralSession,
        String cookie)
        throws PeriodicalsException
    {
        try
        {
            EmailConfirmationRequestResource r = emailConfirmationRequestService
                .getEmailConfirmationRequest(coralSession, cookie);
            if(r != null)
            {
                coralSession.getStore().deleteResource(r);
            }
        }
        catch(EntityInUseException e)
        {
            throw new PeriodicalsException("failed to delete subscription change request", e);
        }
        catch(ConfirmationRequestException e)
        {
            throw new PeriodicalsException("subscription change request dose not exists.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public EmailPeriodicalResource[] getSubscribedEmailPeriodicals(CoralSession coralSession,
        SiteResource site, String email)
        throws PeriodicalsException
    {
        EmailPeriodicalResource[] periodicals = PeriodicalsServiceUtils.getEmailPeriodicals(
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
     * {@inheritDoc}
     */
    public String createUnsubscriptionToken(long periodicalId, String address) throws PeriodicalsException
    {
        try
        {
            return emailConfirmationRequestService.createUnsubscriptionToken(periodicalId, address);
        }
        catch(Exception e)
        {
            throw new PeriodicalsException("failed to create unsubscription token", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public UnsubscriptionInfo decodeUnsubscriptionToken(String encoded, boolean urlEncoded) throws PeriodicalsException
    {
        try
        {
            return emailConfirmationRequestService.decodeUnsubscriptionToken(encoded, urlEncoded);
        }
        catch(Exception e)
        {
            throw new PeriodicalsException("failed to decode unsubscription token", e);
        }
    }

}
