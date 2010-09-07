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

import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.periodicals.EmailPeriodicalResource;
import net.cyklotron.cms.periodicals.EmailPeriodicalsRootResource;
import net.cyklotron.cms.periodicals.EmailPeriodicalsRootResourceImpl;
import net.cyklotron.cms.periodicals.PeriodicalResource;
import net.cyklotron.cms.periodicals.PeriodicalsException;
import net.cyklotron.cms.periodicals.PeriodicalsNodeResource;
import net.cyklotron.cms.periodicals.PeriodicalsNodeResourceImpl;
import net.cyklotron.cms.site.SiteResource;

/**
 *
 *
 * @author <a href="rafal@caltha.pl">Rafał Krzewski</a>
 * @version $Id$
 */
public class PeriodicalsServiceUtils
{
    /**
     * Private ctor to enforce static access.
     */
    private PeriodicalsServiceUtils()
    {
        // intentionally left blank
    }
    
    /**
     * Get root node of application's data.
     * 
     * @param coralSession CoralSession.
     * @param site the site.
     * @return root node of application data.
     * @throws PeriodicalsException
     */
    public static PeriodicalsNodeResource getApplicationRoot(CoralSession coralSession, SiteResource site) throws PeriodicalsException
    {
        Resource[] apps = coralSession.getStore().getResource(site, "applications");
        if (apps.length == 0)
        {
            throw new PeriodicalsException("failed to lookup applications node in site " + site.getName());
        }
        Resource[] res = coralSession.getStore().getResource(apps[0], "periodicals");
        if(res.length == 0)
        {
            try
            {
                return PeriodicalsNodeResourceImpl.createPeriodicalsNodeResource(coralSession,
                    "periodicals", apps[0]);
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

    /**
     * Return the root node for periodicals
     * 
     * @param site the site.
     * @return the periodicals root.
     */
    public static PeriodicalsNodeResource getPeriodicalsRoot(CoralSession coralSession, SiteResource site) throws PeriodicalsException
    {
        PeriodicalsNodeResource applicationRoot = getApplicationRoot(coralSession,site);
        Resource[] res = coralSession.getStore().getResource(applicationRoot, "periodicals");
        if (res.length == 0)
        {
            try
            {
                return PeriodicalsNodeResourceImpl.createPeriodicalsNodeResource(coralSession,
                    "periodicals", applicationRoot);
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

    /**
     * Return the root node for email periodicals
     * 
     * @param site the site.
     * @return the periodicals root.
     */
    public static EmailPeriodicalsRootResource getEmailPeriodicalsRoot(CoralSession coralSession, SiteResource site)
        throws PeriodicalsException
    {
        PeriodicalsNodeResource applicationRoot = getApplicationRoot(coralSession,site);
        Resource[] res = coralSession.getStore().getResource(applicationRoot, "email_periodicals");
        if (res.length == 0)
        {
            try
            {
                return EmailPeriodicalsRootResourceImpl.createEmailPeriodicalsRootResource(
                    coralSession, "email_periodicals", applicationRoot);
            }
            catch(InvalidResourceNameException e)
            {
                throw new RuntimeException("unexpected exception", e);
            }
        }
        else
        {
            return (EmailPeriodicalsRootResource)res[0];
        }
    }

    /**
     * List the periodicals existing in the site.
     * 
     * @param site the site.
     * @return array of periodicals.
     */
    public static PeriodicalResource[] getPeriodicals(CoralSession coralSession,SiteResource site) throws PeriodicalsException
    {
        PeriodicalsNodeResource periodicalsRoot = getPeriodicalsRoot(coralSession,site);
        Resource[] resources = coralSession.getStore().getResource(periodicalsRoot);
        PeriodicalResource[] periodicals = new PeriodicalResource[resources.length];
        System.arraycopy(resources, 0, periodicals, 0, resources.length);
        return periodicals;
    }

    /**
     * List the email periodicals existing in the site.
     * 
     * @param site the site.
     * @return array of periodicals.
     */
    public static EmailPeriodicalResource[] getEmailPeriodicals(CoralSession coralSession,SiteResource site) 
        throws PeriodicalsException
    {
        PeriodicalsNodeResource periodicalsRoot = getEmailPeriodicalsRoot(coralSession,site);
        Resource[] resources = coralSession.getStore().getResource(periodicalsRoot);
        EmailPeriodicalResource[] periodicals = new EmailPeriodicalResource[resources.length];
        System.arraycopy(resources, 0, periodicals, 0, resources.length);
        return periodicals;
    }
}
