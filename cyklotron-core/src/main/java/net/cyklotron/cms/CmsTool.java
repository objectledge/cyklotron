package net.cyklotron.cms;
import java.security.Principal;

import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteException;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.NavigationNodeResourceImpl;

import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.pipeline.ProcessingException;
import org.omg.CORBA.UnknownUserException;

/**
 * A context tool used for cms application.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: CmsTool.java,v 1.2 2005-01-18 17:38:09 pablo Exp $
 */
public class CmsTool
    extends RecyclableObject
    implements ContextTool
{
    /** the rundata for future use */
    private RunData data;

    /** the current subject */
    private Subject subject;
    
    /** logging service */
    private Logger log;

    /** resource service */
    private CoralSession resourceService;

    /** preferences service */
    private PreferencesService preferencesService;

    /** authentication service */
    private AuthenticationService authenticationService;

    /** integration service */
    private IntegrationService integrationService;

    /** initialization flag. */
    private boolean initialized = false;

    /** user data */
    private UserData userData;
    
    // initialization ////////////////////////////////////////////////////////

    public void init(ServiceBroker broker, Configuration config)
    {
        if(!initialized)
        {
            log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).
                getFacility("cms");
            resourceService = (CoralSession)broker.
                getService(CoralSession.SERVICE_NAME);
            preferencesService = (PreferencesService)broker.
                getService(PreferencesService.SERVICE_NAME);
            authenticationService = (AuthenticationService)broker.
                getService(AuthenticationService.SERVICE_NAME);
            integrationService = (IntegrationService)broker.
                getService(IntegrationService.SERVICE_NAME);
            initialized = true;
        }
    }

    public void prepare(RunData data)
    {
        this.data = data;
        Principal principal = data.getUserPrincipal();
        setSubject(principal);
    }
    
    public void reset()
    {
        data = null;
    }
    
    // public interface ///////////////////////////////////////////////////////

    // subjects ///////////////////////////////////////////////////////////////

    /**
     * Sets the user for the current request.
     * 
     * <p>This method is called from the post login hook.
     */
    public void setSubject(Principal principal)
    {
        try
        {
            if (principal == null)
            {
                subject = null;
            }
            else
            {
                String username = principal.getName();
                subject = resourceService.getSecurity().getSubject(username);
            }
        }
        catch(EntityDoesNotExistException e)
        {
            log.debug("CmsTool",e);
            subject = null;
        }
        userData = new UserData(data, subject);
    }

    /**
     * Return the user data.
     *
     * @return the subject.
     */
    public UserData getUserData()
    {
        return userData;
    }

    /**
     * Return the user data.
     *
     * @return the subject.
     */
    public UserData getUserData(Subject subject)
    {
        return new UserData(data, subject);
    }    
    /**
     * Return current logged subject.
     *
     * @return the subject.
     */
    public Subject getSubject()
    {
        return subject;
    }
    
    /**
     * Return the subject with the given name.
     */
    public Subject getSubject(String dn)
        throws Exception
    {
        return resourceService.getSecurity().getSubject(dn);
    }
    
    /**
     * Returns the login of the subject with given dn.
     */
    public String getSubjectLogin(String dn)
        throws Exception
    {
        return authenticationService.getLogin(dn);
    }
    
    /**
     * Returns the dn of the subject with given login.
     */
    public String getSubjectName(String login)
    {
    	try
    	{
            return authenticationService.getUserByLogin(login).getName();
    	}
    	catch(UnknownUserException e)
    	{
    		return null;
    	}
    }

    /**
     * Return the anonymous subject.
     *
     */
    public Subject getAnonymousSubject()
        throws Exception
    {
        return resourceService.getSecurity().getSubject(
            authenticationService.getAnonymousUser().getName());
    }
    

    // role & permission checking ////////////////////////////////////////////

    /**
     * checks whether subject has a role.
     *
     * @param subject the subject.
     * @param role the role.
     */
    public boolean hasRole(Subject subject, String role)
        throws Exception
    {
        try
        {
            Role roleEntity = resourceService.getSecurity().getUniqueRole(role);
            return subject.hasRole(roleEntity);
        }
        catch(Exception e)
        {
            log.error("CmsTool",e);
            throw e;
        }
    }
    
    /**
     * checks right to the resource.
     *
     * @param subject the subject.
     * @param resource the resource.
     * @param permission the permission.
     */
    public boolean hasPermission(Subject subject, Resource resource, String permission)
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
            log.error("CmsTool",e);
            throw e;
        }
    }

    // application data access ///////////////////////////////////////////////

    /**
     * Returns the aplication data root.
     *
     * @param site the site resource.
     * @return the application resource. 
     */
    public Resource getApplication(String application)
        throws Exception
    {
        try
        {
            SiteResource site = CmsData.getCmsData(data).getSite();
            return getApplication(site,application);
        }
        catch(Exception e)
        {
            log.error("CmsTool exception ",e);
            throw e;
        }
    }

    /**
     * Returns the application data root.
     *
     * @param site the site resource.
     * @return the application resource. 
     */
    public Resource getApplication(SiteResource site, String application)
        throws Exception
    {
        Resource[] match = resourceService.getStore().
            getResourceByPath(site.getPath()+"/applications/"+application);
        if(match.length != 1)
        {
            throw new SiteException("application "+application+" not found");
        }
        return match[0];
    }

    // preferences ///////////////////////////////////////////////////////////
    
    public NavigationNodeResource getNodePreferenceOrigin(NavigationNodeResource node, 
                                                          String preference)
    {
        return preferencesService.getNodePreferenceOrigin(node, preference);
    }
    
    public Configuration getCombinedNodePreferences(NavigationNodeResource node) 
    {
        return preferencesService.getCombinedNodePreferences(node);
    }

    // resource lookup (?) ///////////////////////////////////////////////////

    public NavigationNodeResource getNavigationNodeResource(long id)
        throws EntityDoesNotExistException
    {
        return NavigationNodeResourceImpl.
            getNavigationNodeResource(resourceService,id);
    }
    
    public NavigationNodeResource getNavigationNodeResource(String id)
        throws EntityDoesNotExistException
    {
        return NavigationNodeResourceImpl.
            getNavigationNodeResource(resourceService,(new Long(id)).longValue());
    }

    // integration ///////////////////////////////////////////////////////////

    public ResourceClassResource getClassDefinition(long id)
        throws EntityDoesNotExistException
    {
        return getClassDefinition(resourceService.getSchema().getResourceClass(id));
    }
    
    public ResourceClassResource getClassDefinition(Resource res)
    {
        return integrationService.getResourceClass(res.getResourceClass());
    }
    
    public ResourceClassResource getClassDefinition(ResourceClass rc)
    {
        return integrationService.getResourceClass(rc);
    }

    public static SiteResource getSite(Resource res)
    {
        Resource save = res;
        while(res != null && !(res instanceof SiteResource))
        {
            res = res.getParent();
        }
        return (SiteResource)res;
    }
    
    public String getSitePath(Resource res)
    throws Exception
    {
        ResourceClassResource rcr = getClassDefinition(res);
        String[] targetPaths = rcr.getAggregationTargetPathsList();
        
        String sitePath = getSite(res).getPath();
        String resPath = res.getPath();
        
        for(int i=0; i<targetPaths.length; i++)
        {
            String targetPath = targetPaths[i];
            if(targetPath.charAt(0) != '/')
            {
                String startPath = sitePath+'/'+targetPath;
                if(resPath.startsWith(startPath))
                {
                    return resPath.substring(startPath.length());
                }
            }
        }
        return null;
    }

    // attribute access (introspection) //////////////////////////////////////

    public Object resourceAttribute(Resource resource, String attributeName)
    {
        AttributeDefinition attribute = resource.getResourceClass().
            getAttribute(attributeName);
        return resource.get(attribute);
    }

    // current navigation node access //////////////////////////////////////////////////////////////

    /**
     * Return the subject if logged
     */
    public static Subject getSubject(RunData data)
    {
        CoralSession resourceService = (CoralSession)data.getBroker().
            getService(CoralSession.SERVICE_NAME);

        AuthenticationService authenticationService = (AuthenticationService)data.getBroker().
            getService(AuthenticationService.SERVICE_NAME);
        
        try
        {
            Principal principal = data.getUserPrincipal();
            if (principal == null)
            {
                return resourceService.getSecurity().getSubject(
                    authenticationService.getAnonymousUser().getName());
            }
            else
            {
                return resourceService.getSecurity().getSubject(principal.getName());
            }
        }
        catch(EntityDoesNotExistException e)
        {
            return null;
        }
    }
    
    // current site adminstrative access ///////////////////////////////////////////////////////////
    
    /**
     * Checks if the current user has administrative privileges on the current site.
     */
    public static boolean checkAdministrator(RunData data)
        throws ProcessingException
    {
        SiteResource site = CmsData.getCmsData(data).getSite();

        if(site != null)
        {
            if(getSubject(data).hasRole(site.getAdministrator()))
            {
                return true;
            }
        }

        CoralSession resourceService = (CoralSession)data.getBroker().
            getService(CoralSession.SERVICE_NAME);

        Role cmsAdministrator = resourceService.getSecurity().
            getUniqueRole("cms.administrator");
        
        return getSubject(data).hasRole(cmsAdministrator);
    }
    
    /**
     * Checks if a resource is an instance of a given class
     *
     * @param resource the resource.
     * @param className the name of resource class. 
     */
    public boolean isInstance(Resource resource, String className)
        throws Exception
    {
        ResourceClass rc = resourceService.getSchema().getResourceClass(className);
        if(resource.getResourceClass().equals(rc))
        {
            return true;
        }
        ResourceClass[] ancestors = resource.getResourceClass().getParentClasses();
        for (int i = 0; i < ancestors.length; i++)
        {
            if(ancestors[i].equals(rc))
            {
                return true;
            }            
        }
        return false;
    }
    
    public Resource getResource(long id)
    	throws Exception
    {
		return resourceService.getStore().getResource(id);    	
    }
    
}

