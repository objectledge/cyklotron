package net.cyklotron.cms.modules.actions.authentication;

import java.security.Principal;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.authentication.AuthenticationException;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.modules.actions.authentication.BaseAuthenticationAction;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.WebConstants;

import net.cyklotron.cms.security.SecurityService;

public class CMSLogin
    extends BaseAuthenticationAction
{
    private final SecurityService cmsSecurityService;
    private final CoralSessionFactory coralSessionFactory;

    public CMSLogin(Logger logger, UserManager userManager, SecurityService cmsSecurityService,
        CoralSessionFactory coralSessionFactory)
    {
        super(logger, userManager);
        this.cmsSecurityService = cmsSecurityService;
        this.coralSessionFactory = coralSessionFactory;
    }

    /**
     * Attempts to authenticate the user.
     * 
     * @param context the context.
     * @throws ProcessingException if action processing fails.
     */
    public void process(Context context)
        throws ProcessingException
    {
        HttpContext httpContext = HttpContext.getHttpContext(context);
        TemplatingContext templatingContext = context.getAttribute(TemplatingContext.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        String result = null;

        String login = parameters.get(LOGIN_PARAM, null);
        String password = parameters.get(PASSWORD_PARAM, null);
        parameters.remove(LOGIN_PARAM);
        parameters.remove(PASSWORD_PARAM);

        Principal anonymous = null;
        try
        {
            anonymous = userManager.getAnonymousAccount();
        }
        catch(AuthenticationException e)
        {
            throw new ProcessingException("UserManager exception", e);
        }
        if(login == null || password == null)
        {
            result = "login_parameters_missing";
        }
        else
        {
            Principal principal = null;
            try
            {
                principal = userManager.getUserByLogin(login);
                if(userManager.checkUserPassword(principal, password))
                {
                    httpContext.clearSessionAttributes();
                }
                else
                {
                    logger.debug("Invalid password for user " + login);
                    principal = null;
                }
            }
            catch(Exception e)
            {
                logger.debug("unknown username " + login);
                principal = null;
            }
            boolean authenticated;
            if(principal == null)
            {
                principal = anonymous;
                authenticated = false;
            }
            else
            {
                authenticated = true;
                httpContext.getRequest().getSession().setAttribute(
                    WebConstants.PRINCIPAL_SESSION_KEY, principal);
            }
            AuthenticationContext authenticationContext = AuthenticationContext
                .getAuthenticationContext(context);
            authenticationContext.setUserPrincipal(principal, authenticated);

            // Create Coral Subject if necessary
            CoralSession coralSession = null;
            try
            {
                coralSession = coralSessionFactory.getRootSession();
                cmsSecurityService.getSubject(coralSession, principal.getName());
            }
            finally
            {
                coralSession.close();
            }         

            result = authenticated ? "login_successful" : "login_failed";
        }
        templatingContext.put("result", result);
    }
}
