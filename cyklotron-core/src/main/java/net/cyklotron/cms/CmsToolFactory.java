//
//Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
//All rights reserved. 
// 
//Redistribution and use in source and binary forms, with or without modification,  
//are permitted provided that the following conditions are met: 
// 
//* Redistributions of source code must retain the above copyright notice,  
//this list of conditions and the following disclaimer. 
//* Redistributions in binary form must reproduce the above copyright notice,  
//this list of conditions and the following disclaimer in the documentation  
//and/or other materials provided with the distribution. 
//* Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//nor the names of its contributors may be used to endorse or promote products  
//derived from this software without specific prior written permission. 
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
//AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
//WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
//IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
//INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
//BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
//OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
//WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
//ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
//POSSIBILITY OF SUCH DAMAGE. 
//

package net.cyklotron.cms;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.templating.tools.ContextToolFactory;

import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedService;
import net.cyklotron.cms.security.SecurityService;

/**
* Context tool factory component to build the link tool.
* 
* @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
*/
public class CmsToolFactory implements ContextToolFactory
{
    /** logging service */
    private Logger log;

    /** preferences service */
    private PreferencesService preferencesService;

    /** authentication service */
    private UserManager userManager;

    /** integration service */
    private IntegrationService integrationService;

    /** context */
    private Context context;
    
    private CmsDataFactory cmsDataFactory;

    private final SecurityService securityService;
    
    private final RelatedService relatedService;
    
    /**
     */
    public CmsToolFactory(Context context, Logger logger, PreferencesService preferencesService,
        UserManager userManager, IntegrationService integrationService,RelatedService relatedService,
        SecurityService securityService, CmsDataFactory cmsDataFactory)
    {
        this.context = context;
        this.log = logger;
        this.preferencesService = preferencesService;
        this.userManager = userManager;
        this.integrationService = integrationService;
        this.securityService = securityService;
        this.relatedService = relatedService;
        this.cmsDataFactory = cmsDataFactory;
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getTool()
    {
        return new CmsTool(context, log, preferencesService, userManager, integrationService, relatedService,
            securityService, cmsDataFactory);
    }
    
    /**
     * {@inheritDoc}
     */
    public void recycleTool(Object tool)
    {
        // recycling not implemented
    }

    /**
     * {@inheritDoc}
     */
    public String getKey()
    {
        return "cms_tool";
    }    
}
