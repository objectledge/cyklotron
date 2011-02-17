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

import org.objectledge.ComponentInitializationError;
import org.objectledge.coral.entity.EntityInUseException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;


public class EmailConfirmationRequestServiceImpl
    implements EmailConfirmationRequestService
{   
    /** the confirmationRequest data root node. */
    protected Resource confirmationRoot;    
    
    protected CryptographyService cipherCryptographyService;

    public EmailConfirmationRequestServiceImpl(CryptographyService cipherCryptographyService)
    {
        this.cipherCryptographyService = cipherCryptographyService;
    }

    // inherit doc
    public synchronized EmailConfirmationRequestResource getEmailConfirmationRequest(
        CoralSession coralSession, String cookie)
        throws ConfirmationRequestException
    {
        Resource[] res = coralSession.getStore().getResourceByPath(
            "/cms/confirmationRequests/" + cookie);
        if(res.length > 0)
        {
            return (EmailConfirmationRequestResourceImpl)res[0];
        }
        else
        {
            return null;
        }
    }

    // interit doc
    
    public synchronized String createEmailConfirmationRequest(CoralSession coralSession, String email, String data)
        throws ConfirmationRequestException
    {
        Resource root = getConfirmationRequestsRoot(coralSession);
        String cookie;
        Resource[] res;
        do
        {
            cookie = cipherCryptographyService.getRandomCookie();
            res = coralSession.getStore().getResource(root, cookie);
        }
        while(res.length > 0);
        try
        {
            EmailConfirmationRequestResource request = EmailConfirmationRequestResourceImpl
                .createEmailConfirmationRequestResource(coralSession, cookie, root, email);
            request.setData(data);
            request.update();
        }
        catch(Exception e)
        {
            throw new ConfirmationRequestException("failed to create subscription request record", e);
        }
        return cookie;
    }

    // inherit doc
    public synchronized void discardEmailConfirmationRequest(CoralSession coralSession, String cookie)
        throws ConfirmationRequestException
    {
        EmailConfirmationRequestResource r = getEmailConfirmationRequest(coralSession, cookie);
        if(r != null)
        {
            try
            {
                coralSession.getStore().deleteResource(r);
            }
            catch(EntityInUseException e)
            {
                throw new ConfirmationRequestException("failed to delete subscription change request", e);
            }
        }
    }
    
    /**
     * Get root node of confirmationRoot's data.
     * 
     * @param coralSession CoralSession.
     * @return the confirmationResources root.
     */
    public Resource getConfirmationRequestsRoot(CoralSession coralSession)
    {
        if(confirmationRoot == null)
        {
            Resource res[] = coralSession.getStore().getResourceByPath("/cms/confirmationRequests");
            if(res.length == 1)
            {
                confirmationRoot = res[0];
            }
            else
            {
                throw new ComponentInitializationError(
                    "failed to lookup /cms/confirmationRequests node");
            }
        }
        return confirmationRoot;
    }
}
