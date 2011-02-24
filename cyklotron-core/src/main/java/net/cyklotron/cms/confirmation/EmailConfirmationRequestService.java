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

package net.cyklotron.cms.confirmation;

import org.objectledge.coral.session.CoralSession;

import net.cyklotron.cms.confirmation.EmailConfirmationRequestResource;
import net.cyklotron.cms.site.SiteResource;

/**
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id: PeriodicalsSubscriptionService.java,v 1.4 2006-05-18 13:58:09 rafal Exp $
 */
public interface EmailConfirmationRequestService
{

    /**
     * Create a subscription change request.
     * 
     * @param email the requestors email address.
     * @return a magic cookie to be returned to the user.
     */
    public String createEmailConfirmationRequest(CoralSession coralSession, String email, String items)
    throws ConfirmationRequestException;

    /**
     * Return a subscription change request.
     * 
     * @param cookie the magic cookie recieved form the user.
     * @return the request object, or null if invalid.
     */
    public EmailConfirmationRequestResource getEmailConfirmationRequest(CoralSession coralSession,
        String cookie)
    throws ConfirmationRequestException;

    /**
     * Discard a subscription change request.
     * 
     * @param cookie the magic cookie recieved form the user.
     */
    public void discardEmailConfirmationRequest(CoralSession coralSession, String cookie)
    throws ConfirmationRequestException;
    
}