package net.cyklotron.cms.preferences;

import net.cyklotron.cms.structure.NavigationNodeResource;

import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;

/**
 * Manages the preferences: system-wide, user's and specific to navigation nodes.
 *
 * @author <a href="mailto:pablo@ngo.pl">Pawel Potempski</a>
 * @author <a href="mailto:rkrzewsk@ngo.pl">Rafal Krzewski</a>
 * @version $Id: PreferencesService.java,v 1.3 2005-02-09 19:22:57 rafal Exp $
 */
public interface PreferencesService 
{
    /** The name of the service (<code>"preferences"</code>). */
    public final static String SERVICE_NAME = "preferences";

    /**
     * The logging facility where the service issues it's informational
     * messages.
     */
    public static final String LOGGING_FACILITY = "preferences";

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
    public Parameters getNodePreferences(NavigationNodeResource node);
    
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
    public Parameters getUserPreferences(CoralSession coralSession, Subject subject);
    
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
    public Parameters getSystemPreferences();
    
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
    public Parameters getPreferences(CoralSession coralSession, NavigationNodeResource node,
                                        Subject subject);

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
    public Parameters getCombinedNodePreferences(NavigationNodeResource node);

    /**
     * Returns a value of a preference as defined by the node and it's ancestors
     * up to the site's root.
     *
     * @param node the node where search for value should start.
     * @param preference the preference name.
     * @return preference value, possibly undefined.
     */
    public String getNodePreferenceValue(NavigationNodeResource node, 
                                            String preference);

    /**
     * Returns the most nested node in the chain between the selected node and
     * the site root that defines a value of a preference.
     *
     * @param node the node where search for value should start.
     * @param preference the preference name.
     * @return navigation node, possibly <code>null</code>.
     */
    public NavigationNodeResource getNodePreferenceOrigin(NavigationNodeResource node, 
                                                          String preference);
}
