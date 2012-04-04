package net.cyklotron.cms.security;

import java.security.Principal;

import org.objectledge.authentication.AuthenticationContext;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;

/**
 * On-demand setup of Coral subjects for users logged in.
 * <p>
 * When the user is successfully logged in by the user manager, but no Coral subject found, the
 * subject will be created and assigned {@code cms.registered} ("everyone") role by the
 * {@SecurityService}.
 * </p>
 * 
 * @author rafal.krzewski@objectledge.org
 */
public class CmsSecurityValve
    implements Valve
{
    private final SecurityService cmsSecurityService;

    private final CoralSessionFactory coralSessionFactory;

    public CmsSecurityValve(SecurityService cmsSecurityService,
        CoralSessionFactory coralSessionFactory)
    {
        this.cmsSecurityService = cmsSecurityService;
        this.coralSessionFactory = coralSessionFactory;
    }

    @Override
    public void process(Context context)
        throws ProcessingException
    {
        AuthenticationContext authContext = context.getAttribute(AuthenticationContext.class);
        Principal principal = authContext.getUserPrincipal();

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
    }
}
