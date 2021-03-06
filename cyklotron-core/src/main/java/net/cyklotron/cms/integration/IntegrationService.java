package net.cyklotron.cms.integration;

import java.util.Map;

import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.site.SiteResource;

/**
 * Provides information about available applications, components, and resource
 * types.
 *
 * @author <a href="mailto:rkrzewsk@caltha.pl">Rafał Krzewski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: IntegrationService.java,v 1.8 2007-11-18 21:23:39 rafal Exp $
 */
public interface IntegrationService
{
    // constants /////////////////////////////////////////////////////////////

    /** The service name. */
    public static final String SERVICE_NAME = "cms_integration";
    
    /** The logging facility name. */
    public static final String LOGGING_FACILITY = "cms_integration";

    // public interface //////////////////////////////////////////////////////
    
    /**
     * Returns the descriptors of all applications deployed in the system.
     */
    public ApplicationResource[] getApplications(CoralSession coralSession);
    
    /**
     * Return the ApplicationResource for app
     * 
     * @param name the application name.
     * @return the application resource.
     */
    public ApplicationResource getApplication(CoralSession coralSession, String name);
    
    /**
     * Returns the descirptors of all components provided by an application.
     *
     * @param app the application.
     */
    public ComponentResource[] getComponents(CoralSession coralSession, ApplicationResource app);
    
    /**
     * Returns a named component from a specific application.
     * 
     * @param app the application.
     * @param name the component name. 
     * @return the component resource.
     */
    public ComponentResource getComponent(CoralSession coralSession, ApplicationResource app, String name);
    
    /**
     * Returns the descirptors of all components deployed in the system.
     */
    public ComponentResource[] getComponents(CoralSession coralSession);
    
    /**
     * Returns the application a component belongs to.
     *
     * @param comp the component.
     */
    public ApplicationResource getApplication(CoralSession coralSession, ComponentResource comp);

    /**
     * Returns the component with the given app and component name.
     *
     * @param app the application parameter.
     * @param name the component name.
     * @return the component, or <code>null</code> if not found.
     */
    public ComponentResource getComponent(CoralSession coralSession, String app, String name);

    /**
     * Get defined states of a component.
     *
     * @param component the component.
     * @return an array of defined states, or empty array if component is
     *         stateless. 
     */
    public ComponentStateResource[] getComponentStates(CoralSession coralSession, ComponentResource component);

    /**
     * Checks if a component has a given state defined.
     *
     * @param component the component.
     * @param state the state.
     */
    public boolean hasState(CoralSession coralSession, ComponentResource component, String state);

    /**
     * Returns the descirptors of all screens provided by an application.
     *
     * @param app the application.
     */
    public ScreenResource[] getScreens(CoralSession coralSession, ApplicationResource app);

    /**
     * Return a named screen defined in the application.
     * 
     * @param appRes application resource
     * @param screen screen name
     * @return a screen resource.
     */
    public ScreenResource getScreen(CoralSession coralSession, ApplicationResource appRes, String screen);
    
    /**
     * Returns the descirptors of all screens deployed in the system.
     */
    public ScreenResource[] getScreens(CoralSession coralSession);
    
    /**
     * Returns the application a screen belongs to.
     *
     * @param comp the screen.
     */
    public ApplicationResource getApplication(CoralSession coralSession, ScreenResource comp);

    /**
     * Returns the screen with the given app and screen name.
     *
     * @param app the application parameter.
     * @param name the screen name.
     * @return the screen, or <code>null</code> if not found.
     */
    public ScreenResource getScreen(CoralSession coralSession, String app, String name);
    
    /**
     * Get defined states of a screen.
     *
     * @param screen the screen.
     * @return an array of defined states, or empty array if screen is
     *         stateless. 
     */
    public ScreenStateResource[] getScreenStates(CoralSession coralSession, ScreenResource screen);

    /**
     * Checks if a screen has a given state defined.
     *
     * @param screen the screen.
     * @param state the state.
     */
    public boolean hasState(CoralSession coralSession, ScreenResource screen, String state);
    
    /**
     * Returns the descriptor of a resource class given by name.
     *
     * @param name The name of a resource class.
     * @return the descriptor;
     */
    public ResourceClassResource getResourceClass(CoralSession coralSession, String name);
    
    /**
     * Returns the descriptors of all resource classes registered in the system.
     */
    public ResourceClassResource[] getResourceClasses(CoralSession coralSession);
    
    /**
     * Returns the resource class info with the given app and resource class name.
     *
     * @param rc the resource class.
     * @return the resource class descriptor, or <code>null</code> if not found.
     */
    public ResourceClassResource getResourceClass(CoralSession coralSession, ResourceClass rc);
    
    /**
     * Returns the resource class fot the given resource class resource.
     *
     * @param rcr the resource class resource.
     * @return the resourc class for this resource class resource..
     */
    public ResourceClass getResourceClass(CoralSession coralSession, ResourceClassResource rcr);
    
    /**
     * Returns the descirptors of all resource classes provided by an application.
     *
     * @param applicationResource the application resource.
     */
    public ResourceClassResource[] getResourceClasses(CoralSession coralSession, ApplicationResource applicationResource);

    /**
     * Initializes an ResourceSelectionState object for ResourceClassResources.
     * 
     * @param items a space separated list of resource class names.
     * @param state the state to be assigned to the ResourceClassResource objects.
     */
    public Map initResourceClassSelection(CoralSession coralSession, String items, String state);
        
    /** 
     * Return the schema role root.
     *
     * @param rc the resource class.
     * @return the schema role root.
     */
    public Resource getSchemaRoleRoot(CoralSession coralSession, ResourceClass rc);
    
    /**
     * Check whether application is enabled within particular site.
     * 
     * @param site the site resource.
     * @param applicationRes the integration application resource.
     * @return <code>true</code> if application is enabled.
     */
    public boolean isApplicationEnabled(CoralSession coralSession, SiteResource site,
        ApplicationResource applicationRes);
    
    
    /**
     * Check which applications are enabled within particular site.
     * 
     * @param site the site resource.
     * @return the enabled applications.
     */
    public ApplicationResource[] getEnabledApplications(CoralSession coralSession, SiteResource site);
    
    /**
     * Change application enabled/disabled state within particular site. 
     *  
     * @param site the site resource.
     * @param applicationRes the integration application resource.
     * @return enabled <code>true</code> if application should be enabled.
     */
    public void setApplicationEnabled(CoralSession coralSession, SiteResource site,
        ApplicationResource app, boolean enabled);

    /**
     * Clear enabled applications data for the site.
     * 
     * @param coralSession Coral session.
     * @param site the site.
     */
    public void clearEnabledApplications(CoralSession coralSession, SiteResource site);
}

     
