package net.cyklotron.cms.modules.actions.security;

import java.security.Principal;

import net.cyklotron.cms.site.SiteResource;
import net.labeo.services.authentication.AuthenticationService;
import net.labeo.services.authentication.UnknownUserException;
import net.labeo.services.defaults.LoggingService;
import org.jcontainer.dna.Logger;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.configuration.Parameters;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;
import net.labeo.webcore.Secure;

/**
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: ChangePassword.java,v 1.1 2005-01-24 04:34:54 pablo Exp $
 */
public class ChangePassword
    extends BaseSecurityAction
    implements Secure
{
    private AuthenticationService auth;
    
    private Logger log;
    
    public ChangePassword()
    {
        super();
        auth = (AuthenticationService)broker.getService(AuthenticationService.SERVICE_NAME);
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility("security");
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Parameters param = parameters;
        Subject subject = null;
        Subject changer = coralSession.getUserSubject();
        Principal principal;
        long uid = param.get("user_id").asLong(-1);
        if(uid == -1)
        {
            subject = changer;
            principal = data.getUserPrincipal();
        }
        else
        {
            try
            {
                subject = coralSession.getSecurity().getSubject(uid);
                principal = auth.getUserByName(subject.getName());
            }
            catch(Exception e)
            {
                log.error("SecurityException: ",e);
                templatingContext.put("result","exception");
                templatingContext.put("trace","Password changing exception: "+e.getMessage());
                return;
            }
        }
        String old = param.get("old_password","");
        try
        {
            String login = auth.getLogin(subject.getName());
            if(!auth.authenticateUser(login, old))
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
            auth.changeUserPassword(principal,new1);
        }
        catch(UnknownUserException e)
        {
            log.error("UnknownUserException: ",e);
            templatingContext.put("result","exception");
            templatingContext.put("trace","Password changing exception: "+e.getMessage());
            return;
        }
        templatingContext.put("result","changed_successfully");
    }
    
    public boolean checkAccess(RunData data)
    	throws ProcessingException
    {
        return true;
    }    
}
