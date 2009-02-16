package net.cyklotron.cms.modules.actions.security;

import java.security.Principal;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ChangePassword.java,v 1.4 2005-03-08 10:53:56 pablo Exp $
 */
public class ChangePassword
    extends BaseSecurityAction
{

    public ChangePassword(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SecurityService cmsSecurityService, UserManager userManager)
    {
        super(logger, structureService, cmsDataFactory, cmsSecurityService, userManager);
        
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        AuthenticationContext authenticationContext = 
            AuthenticationContext.getAuthenticationContext(context);
        Parameters param = parameters;
        Subject subject = null;
        Subject changer = coralSession.getUserSubject();
        Principal principal;
        long uid = param.getLong("user_id",-1);
        if(uid == -1)
        {
            subject = changer;
            principal = authenticationContext.getUserPrincipal();
        }
        else
        {
            try
            {
                subject = coralSession.getSecurity().getSubject(uid);
                principal = userManager.getUserByName(subject.getName());
            }
            catch(Exception e)
            {
                logger.error("SecurityException: ",e);
                templatingContext.put("result","exception");
                templatingContext.put("trace","Password changing exception: "+e.getMessage());
                return;
            }
        }
        String old = param.get("old_password","");
        try
        {
            String login = userManager.getLogin(subject.getName());
            if(!userManager.checkUserPassword(principal, old))
            {
                templatingContext.put("result","invalid_password");
                return;
            }
            String new1 = param.get("new_password1","");
            String new2 = param.get("new_password2","");
            if(!new1.equals(new2))
            {
                templatingContext.put("result","passwords_do_not_match");
                return;
            }
            userManager.changeUserPassword(principal,new1);
        }
        catch(Exception e)
        {
            logger.error("UnknownUserException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace","Password changing exception: "+e.getMessage());
            return;
        }
        templatingContext.put("result","changed_successfully");
    }
    
    public boolean checkAccessRights(Context context)
    	throws ProcessingException
    {
        return true;
    }    
}
