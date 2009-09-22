/*
 * Created on Dec 15, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.cyklotron.cms.modules.views.security;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.security.SecurityService;

/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddUser
    extends BaseSecurityScreen
{
    
    public AddUser(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        SecurityService securityService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        securityService);
        
    }
    
    
    
    /* (non-Javadoc)
     * @see net.cyklotron.cms.modules.views.BaseCMSScreen#process(org.objectledge.parameters.Parameters, org.objectledge.web.mvc.MVCContext, org.objectledge.templating.TemplatingContext, org.objectledge.web.HttpContext, org.objectledge.i18n.I18nContext, org.objectledge.coral.session.CoralSession)
     */
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession) throws ProcessingException
    {
    }
    
    public boolean checkAccessRights(Context context)
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        if(cmsSecurityService.getAllowAddUser())
        {
            return true;
        }
        Role role = coralSession.getSecurity().getUniqueRole("cms.administrator");
        return coralSession.getUserSubject().hasRole(role);
    }
    
    public boolean requiresAuthenticatedUser(Context context)
    {
        return !cmsSecurityService.getAllowAddUser();
    }
}
