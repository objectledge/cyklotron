package net.cyklotron.cms.preferences.internal;

import java.util.ArrayList;
import java.util.List;

import net.labeo.services.BaseService;
import net.labeo.services.InitializationError;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.util.configuration.BaseConfiguration;
import net.labeo.util.configuration.CompoundParameterContainer;
import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameter;

import net.cyklotron.cms.preferences.PreferencesResource;
import net.cyklotron.cms.preferences.PreferencesResourceImpl;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * An implementation of <code>Preferences Service</code>.
 */
public class PreferencesServiceImpl
    extends BaseService
    implements PreferencesService
{
    // instance variables ////////////////////////////////////////////////////

    /** The resource service. */
    private ResourceService resourceService;

    /** The system-wide preferences. */
    private PreferencesResource systemPrefs;
    
    /** The parent node of user's preferences. */
    private Resource userPrefsRoot;

    // initialization ////////////////////////////////////////////////////////

    /**
     * Initializes the service.
     */
    public void init()
    {
        resourceService = (ResourceService)getBroker().
            getService(ResourceService.SERVICE_NAME);
        Resource[] res = resourceService.getStore().getResourceByPath("/cms/preferences/system");
        if(res.length != 1)
        {
            throw new InitializationError("failed to find system preferences node");
        }
        systemPrefs = (PreferencesResource)res[0];
        res = resourceService.getStore().getResourceByPath("/cms/preferences/users");
        if(res.length != 1)
        {
            throw new InitializationError("failed to find user preferences root");
        }
        userPrefsRoot = res[0];
    }

    // PreferenceService interface ///////////////////////////////////////////

    // preference manipulation ///////////////////////////////////////////////

    /**
     * Returns the preferences of a specific navigation node.
     *
     * <p>You could use <code>NavigationNodeResource.getPreferences()</code> with the
     * same effect. This method is here for completness.</p>
     *
     * <p>The returnced Configuration object is backed by the database. Any
     * changes made to it will be persistent.</p>
     *
     * @param node the navigation node.
     * @return the node preferences.
     */
    public Configuration getNodePreferences(NavigationNodeResource node)
    {
        return new BaseConfiguration(node.getPreferences());
    }
    
    /**
     * Returns the preferences of a specific user.
     *
     * <p>This is the officialy endorsed method of stetting persistent
     * preferences of an user in Cyklotron system.</p>
     *
     * <p>The returnced Configuration object is backed by the database. Any
     * changes made to it will be persistent.</p>
     *
     * @param subject the user.
     * @return the user's preferences.
     */
    public Configuration getUserPreferences(Subject subject)
    {
        Resource[] res = resourceService.getStore().
            getResource(userPrefsRoot, subject.getName());
        PreferencesResource prefs = null;
        if(res.length != 0)
        {
            prefs = (PreferencesResource)res[0];
        }
        else
        {
            try
            {
                prefs = PreferencesResourceImpl.
                    createPreferencesResource(resourceService,
                                              subject.getName(),
                                              userPrefsRoot,
                                              new BaseConfiguration(),
                                              subject);
            }
            catch(ValueRequiredException e)
            {
                // won't happen
            }
        }
        return new BaseConfiguration(prefs.getPreferences());
    }
    
    /**
     * Returns the system wide default preferences.
     *
     * <p>Returns the system wide preferences. This is not related to
     * configuration settings available from Labeo ConfigurationService.</p>
     * 
     * <p>The returnced Configuration object is backed by the database. Any
     * changes made to it will be persistent.</p>
     *
     * @return the user's preferences.
     */
    public Configuration getSystemPreferences()
    {
        return new BaseConfiguration(systemPrefs.getPreferences());
    }
    
    // effective preferences /////////////////////////////////////////////////

    /**
     * Returns preferences in effect for a given navigation node and user.
     *
     * <p>The effective preferences are a combination of system preferences,
     * preferences of navigation nodes that form the path between site root
     * and the selected node, and user's preferences. A preference value
     * defined further in the chain replaces any value that might have been
     * defined earlier.</p>
     *
     * <p>The returned configuration is immutable. Any attempt to modify
     * the information will result in an exception being thrown.</p>
     *
     * @param node the navigation node. 
     * @param subject the user.  
     * @return the effective preferences.
     */
    public Configuration getPreferences(NavigationNodeResource node,
                                        Subject subject)
    {
        List containers = new ArrayList();
        containers.add(systemPrefs.getPreferences());
        containers.add(node.getPreferences());
        while(node.getParent() != null && 
              node.getParent() instanceof NavigationNodeResource)
        {
            node = (NavigationNodeResource)node.getParent();
            containers.add(0, node.getPreferences());
        }
        containers.add(getUserPreferences(subject));
        return new BaseConfiguration(new CompoundParameterContainer(containers));
    }

    // combining preferences /////////////////////////////////////////////////

    /**
     * Returns combined preferences of a node and it's ancestors.
     *
     * <p>The returned configuration is immutable. Any attempt to modify
     * the information will result in an exception being thrown.</p>
     *
     * @param node the navigation node.
     * @return the combined preferences.
     */
    public Configuration getCombinedNodePreferences(NavigationNodeResource node)
    {
        List containers = new ArrayList();
        containers.add(node.getPreferences());
        while(node.getParent() != null && 
              node.getParent() instanceof NavigationNodeResource)
        {
            node = (NavigationNodeResource)node.getParent();
            containers.add(0, node.getPreferences());
        }
        containers.add(0, systemPrefs.getPreferences());
        return new BaseConfiguration(new CompoundParameterContainer(containers));
    }

    /**
     * Returns a value of a preference as defined by the node and it's ancestors
     * up to the site's root.
     *
     * @param node the node where search for value should start.
     * @param pereference the preference name.
     * @return preference value, possibly undefined.
     */
    public Parameter getNodePreferenceValue(NavigationNodeResource node, 
                                            String preference)
    {
        Parameter value = node.getPreferences().get(preference);
        while(!value.isDefined() && node.getParent() != null &&
              node.getParent() instanceof NavigationNodeResource)
        {
            node = (NavigationNodeResource)node.getParent();
            value = node.getPreferences().get(preference);
        }
        return value;
    }

    /**
     * Returns the most nested node in the chain between the selected node and
     * the site root that defines a value of a preference.
     *
     * @param node the node where search for value should start.
     * @param pereference the preference name.
     * @return navigation node, possibly <code>null</code>.
     */
    public NavigationNodeResource getNodePreferenceOrigin(NavigationNodeResource node, 
                                                          String preference)
    {
        Parameter value = node.getPreferences().get(preference);
        while(!value.isDefined() && node.getParent() != null &&
              node.getParent() instanceof NavigationNodeResource)
        {
            node = (NavigationNodeResource)node.getParent();
            value = node.getPreferences().get(preference);
        }
        return value.isDefined() ? node : null;
    }
}
