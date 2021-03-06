package net.cyklotron.cms;

import java.security.Principal;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.AuthenticationException;
import org.objectledge.authentication.DefaultPrincipal;
import org.objectledge.authentication.UserManager;
import org.objectledge.authentication.UserUnknownException;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.directory.DirectoryParameters;

import net.cyklotron.cms.preferences.PreferencesService;

/**
 * A user data object used to access various user properties.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: UserData.java,v 1.8 2005-05-18 08:07:56 pablo Exp $
 */
public class UserData
{
    /** the context */
    private Context context;
    
    /** The {@link Subject} */
    private Subject subject;

    /** The {@link Logger} */
    private Logger log;

    /** The {@link PreferencesService} */
    private PreferencesService preferencesService;

    /** The {@link UserManager} */
    private UserManager userManager;
   
    /** login */
    private String login;
    
    /** user preferences */
    private Parameters preferences;
    
    /** user personal data */
    private Parameters personalData;

    public UserData(Context context, Logger logger,
        PreferencesService preferencesService, UserManager userManager,
        Subject subject)
    {
        this.context = context;
        this.log = logger;
        this.preferencesService = preferencesService;
        this.userManager = userManager;
        this.subject = subject;
        if(this.subject == null)
        {
            this.subject = ((CoralSession)context.getAttribute(CoralSession.class)).
                getUserSubject();
        }
        
    }

    // subjects ///////////////////////////////////////////////////////////////

    /**
     * Return the subject.
     *
     * @return the subject.
     */
    public Subject getSubject()
    {
        return subject;
    }

    /**
     * Returns subject's name wrapped in a java.security.Principal
     * 
     * @return
     */
    public Principal getPrincipal()
    {
        return new DefaultPrincipal(subject.getName());
    }

    /**
     * Returns the login of the user.
     *
     * @return the user login.
     */
    public String getLogin()
        throws Exception
    {
    	if(login == null)
    	{
    		login = userManager.getLogin(subject.getName());
    	}
    	return login;
    }
    
    /**
     * Check if the user is anonymous rather than authenticated.
     * 
     * @return true if current user is anonymous.
     */
    public boolean isAnonymous()
        throws AuthenticationException
    {
        return subject.getName().equals(userManager.getAnonymousAccount().getName());
    }

    /**
     * Returns the user preferences.
     *
     * @return the user configuration.
     */
    public Parameters getPreferences()
    {
    	if(preferences == null)
    	{
            CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
    		preferences = preferencesService.getUserPreferences(coralSession, subject); 
    	}
        return preferences;
    }


    /**
     * Returns the user personal data.
     *
     * @return the parameter container.
     */
    public Parameters getPersonalData()
        throws AuthenticationException
    {
    	if(personalData == null)
    	{
    		try
            {
                personalData = new DirectoryParameters(userManager.getPersonalData(new DefaultPrincipal(subject.getName())));
            }
            catch(UserUnknownException e)
            {
                log.error("user " + subject.getName() + " missing from LDAP");
                return new DefaultParameters();
            } 
    	}
        return personalData;
    }


    // role & permission checking ////////////////////////////////////////////

    /**
     * checks whether subject has a role.
     *
     * @param role the role.
     */
    public boolean hasRole(String role)
        throws Exception
    {
        try
        {
            CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
            Role roleEntity = coralSession.getSecurity().getUniqueRole(role);
            return subject.hasRole(roleEntity);
        }
        catch(Exception e)
        {
            log.error("cannot check role named '"+role+"'",e);
            throw e;
        }
    }

    /**
     * Checks rights to the resource.
     *
     * @param resource the resource.
     * @param permission the permission.
     */
    public boolean hasPermission(Resource resource, String permission)
        throws Exception
    {
        try
        {
            CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
            Permission permissionEntity = coralSession.getSecurity().
                getUniquePermission(permission);
            return subject.hasPermission(resource, permissionEntity);
        }
        catch(Exception e)
        {
            log.error("Exception during ",e);
            throw e;
        }
    }

    // private methods     // //////////////////////////////////////////////////////
}

