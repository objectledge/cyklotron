package net.cyklotron.cms;

import java.security.Principal;

import javax.naming.NamingException;

import net.cyklotron.cms.preferences.PreferencesService;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.modules.actions.authentication.Login;

/**
 * A user data object used to access various user properties.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: UserData.java,v 1.3 2005-01-19 08:24:50 pablo Exp $
 */
public class UserData
{
    /** The {@link Subject} */
    private Subject subject;

    /** The {@link Logger} */
    private Logger log;

    /** The {@link CoralSession} */
    private CoralSession resourceService;

    /** The {@link PreferencesService} */
    private PreferencesService preferencesService;

    /** The {@link AuthenticationService} */
    private AuthenticationService authenticationService;

    /** The {@link PersonalDataService} */
    private PersonalDataService personalDataService;
    
    /** login */
    private String login;
    
    /** user preferences */
    private Configuration preferences;
    
    /** user personal data */
    private ParameterContainer personalData;

    public UserData(RunData data)
    {
        this(data, null);
    }

    public UserData(RunData data, Subject subject)
    {
        init(data.getBroker());
        this.subject = subject;
        if(this.subject == null)
        {
            this.subject = getSubject(data);
        }
        if(this.subject == null && !data.getAction().equals(Login.class))
        {
            try
            {
                data.setView("LoginRequired");
            }
            catch(Exception e)
            {
                throw new CmsError("Failed to redirect to login screen", e);
            }
        }
    }

    public UserData(ServiceBroker broker, Subject subject)
    {
        init(broker);
        if(subject == null)
        {
            throw new CmsError("Failed to initialize subject data");
        }
        this.subject = subject;
    }

    // initialization ////////////////////////////////////////////////////////

    private void init(ServiceBroker broker)
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).
            getFacility("cms");
        resourceService = (CoralSession)broker.
            getService(CoralSession.SERVICE_NAME);
        preferencesService = (PreferencesService)broker.
            getService(PreferencesService.SERVICE_NAME);
        authenticationService = (AuthenticationService)broker.
            getService(AuthenticationService.SERVICE_NAME);
        personalDataService = (PersonalDataService)broker.
            getService(PersonalDataService.SERVICE_NAME);
    }

    // public interface    // ///////////////////////////////////////////////////////



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
     * Returns the login of the user.
     *
     * @return the user login.
     */
    public String getLogin()
        throws Exception
    {
    	if(login == null)
    	{
    		login = authenticationService.getLogin(subject.getName());
    	}
    	return login;
    }

    /**
     * Returns the user preferences.
     *
     * @return the user configuration.
     */
    public Configuration getPreferences()
    {
    	if(preferences == null)
    	{
    		preferences = preferencesService.getUserPreferences(subject); 
    	}
        return preferences;
    }


    /**
     * Returns the user personal data.
     *
     * @return the parameter container.
     */
    public ParameterContainer getPersonalData()
        throws NamingException
    {
    	if(personalData == null)
    	{
    		personalData = personalDataService.getData(subject.getName()); 
    	}
        return personalData;
    }


    // role & permission checking ////////////////////////////////////////////

    /**
     * checks whether subject has a role.
     *
     * @param subject the subject.
     * @param role the role.
     */
    public boolean hasRole(String role)
        throws Exception
    {
        try
        {
            Role roleEntity = resourceService.getSecurity().getUniqueRole(role);
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
     * @param subject the subject.
     * @param resource the resource.
     * @param permission the permission.
     */
    public boolean hasPermission(Resource resource, String permission)
        throws Exception
    {
        try
        {
            Permission permissionEntity = resourceService.getSecurity().
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

    private Subject getSubject(RunData data)
    {
        try
        {
            Principal principal = data.getUserPrincipal();
            if (principal == null)
            {
                return null;
            }
            else
            {
                String username = principal.getName();
                return ((CoralSession)data.getBroker().getService(CoralSession.SERVICE_NAME)).
                    getSecurity().getSubject(username);
            }
        }
        catch(EntityDoesNotExistException e)
        {
            log.error("Resource not found",e);
            return null;
        }
    }
}

