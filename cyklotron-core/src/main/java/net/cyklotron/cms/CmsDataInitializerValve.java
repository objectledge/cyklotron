// 
// Copyright (c) 2003, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
//  
// * Redistributions of source code must retain the above copyright notice,  
//   this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//   this list of conditions and the following disclaimer in the documentation  
//   and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//   nor the names of its contributors may be used to endorse or promote products  
//   derived from this software without specific prior written permission. 
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

package net.cyklotron.cms;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.templating.TemplatingContext;

import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 * Pipeline processing valve that initialize pipeline context.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: CmsDataInitializerValve.java,v 1.2 2005-02-09 19:22:38 rafal Exp $
 */
public class CmsDataInitializerValve 
    implements Valve
{
    private Logger logger;
    /** structure service */
    private StructureService structureService;
    /** preferences service */
    private PreferencesService preferencesService;
    /** site service */
    private SiteService siteService;
    /** user manager */
    private UserManager userManager;

    /**
	 * Constructor.
	 * 
	 */
	public CmsDataInitializerValve(Logger logger, 
        StructureService structureService, PreferencesService preferencesService,
        SiteService siteService, UserManager userManager)
	{
        this.logger = logger;
        this.structureService = structureService;
        this.preferencesService = preferencesService;
        this.siteService = siteService;
        this.userManager = userManager;
	}
	
    /**
     * Run the pipeline valve - initialize and store the pipeline context.
     * 
     * @param context the context.
     */
    public void process(Context context)
        throws ProcessingException
    {
    	CmsData cmsData = new CmsData(context, logger, structureService,
            preferencesService, siteService, userManager);
        TemplatingContext templatingContext = (TemplatingContext)context.
            getAttribute(TemplatingContext.class);
        templatingContext.put("cmsData", cmsData);
    }
}
