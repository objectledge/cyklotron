package net.cyklotron.cms.style;

import java.util.List;

import net.labeo.services.Service;
import net.labeo.services.resource.AmbigousNameException;
import net.labeo.services.resource.CircularDependencyException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;

import net.cyklotron.cms.site.SiteResource;

public interface StyleService
    extends Service
{
    public static final String SERVICE_NAME = "cms_style";

    public static final String LOGGING_FACILITY = "cms_style";

    // styles ////////////////////////////////////////////////////////////////

    /** 
     * add new style to system.
     *
     * @param name the style name.
     * @param description the style description.
     * @param site the site.
     * @param parent the parent style or <code>null</code> for top level style.
     * @param subject the creator.
     * @return style resource.
     */
    public StyleResource addStyle(String name, String description, 
                                  SiteResource site, StyleResource parent, 
                                  Subject subject)
        throws StyleException, AmbigousNameException;

    /**
     * returns the documents that have the style explicytly set.
     * 
     * @param style the style.
     */
    public List getReferringNodes(StyleResource style)
        throws StyleException;
    
    /** 
     * delete the style from the system.
     *
     * @param style the style to delete.
     * @param subject the subject performing delete action.
     */
    public void deleteStyle(StyleResource style, Subject subject)
        throws StyleException;

    /**
     * Update the style info.
     *
     * @param style the style resource.
     * @param name the name of the style.
     * @param description the description of the style.
     * @param parent the parent style or <code>null</code> for top level style.
     * @param subject the subject who performs the action.
     */
    public void updateStyle(StyleResource resource, String name, String description, 
                            StyleResource parent, Subject subject)
        throws AmbigousNameException, CircularDependencyException, StyleException;

    /**
     * Returns the site a style belongs to.
     *
     * @param syle the style resource.
     * @return the site.
     */
    public SiteResource getSite(StyleResource style)
        throws StyleException;

    /** 
     * Return the style resource for the given site and style name.
     *
     * @param site the site.
     * @param style the style name.
     * @return style resource.
     */
    public StyleResource getStyle(SiteResource site, String style)
        throws StyleException;
    
    /**
     * Returns the super style of a style.
     *
     * @param style the style resource.
     * @returns the super style of the given style, or <code>null</code> for a
     *          top level style.
     */
    public StyleResource getSuperStyle(StyleResource style);
    
    /**
     * Returns the sub styles of a style.
     *
     * @param style the style resource.
     * @returns the sub styles of a style.
     */
    public StyleResource[] getSubStyles(StyleResource style);

    /**
     * Return the full list of styles.
     *
     * @param site the site.
     * @return the style resource.
     */
    public StyleResource[] getStyles(SiteResource site)
        throws StyleException;

    /**
     * Returns the style root resource for a given site.
     *
     * @param site the site.
     * @return the style root resource for a given site.
	 * @throws <code>StyleException</code>
     */
    public Resource getStyleRoot(SiteResource site)
        throws StyleException;

    // levels ////////////////////////////////////////////////////////////////

    /**
     * Add level to the style.
     *
     * @param style the style.
     * @param layout the layout.
     * @param level the level.
     * @param description the description.
     * @param subject the creator.
     * @return the level resource.
     */
    public LevelResource addLevel(StyleResource style, LayoutResource layout, 
                                  int level, String description, Subject subject)
        throws StyleException;
    
    /** 
     * delete the level from the system.
     *
     * @param level the level to delete.
     * @param subject the subject performing delete action.
     */
    public void deleteLevel(LevelResource level, Subject subject)
        throws StyleException;

    /** 
     * Return the layout level resource.
     *
     * @param style the style.
     * @param level the level.
     * @return the level resources.
     */    
    public LevelResource getLevel(StyleResource style, int level);
    
    /** 
     * Return all definied levels for the style.
     *
     * @param style the style.
     * @return the list of level resources.
     */
    public LevelResource[] getLevels(StyleResource style);

    /**
     * Return the layout template name corresponding to the style and level.
     *
     * @param style the style resource.
     * @param level the level.
     * @return the layout name.
     */
    public String getLayout(StyleResource style, int level);

    // layouts ///////////////////////////////////////////////////////////////

    /** 
     * Add new layout to the system.
     *
     * @param name the name of the layout.
     * @param description the description of the layout.
     * @param site the site.
     * @param subject the creator.
     * @return layout resource.
     */
    public LayoutResource addLayout(String name, String description, 
                                    SiteResource site, Subject subject)
        throws StyleException, AmbigousNameException;
    
    /** 
     * delete the layout from the system.
     *
     * @param layout the layout to delete.
     * @param subject the subject performing delete action.
     */
    public void deleteLayout(LayoutResource layout, Subject subject)
        throws StyleException;

    /**
     * Update the layout info.
     *
     * @param layout the layout resource.
     * @param name the name of the layout.
     * @param description the description of the layout.
     * @param subject the subject who performs the action.
     */
    public void updateLayout(LayoutResource resource, String name, 
                             String description, Subject subject)
        throws StyleException, AmbigousNameException;
    
    /**
     * Return the layout.
     *
     * @param site the site.
     * @param layout the name of the layout.
     * @return the layout resource, or <code>null</code> if not found.
     */
    public LayoutResource getLayout(SiteResource site, String layout)
        throws StyleException;

    /**
     * Return full list of layouts.
     *
     * @param site the site.
     * @return the list of layouts.
     */
    public LayoutResource[] getLayouts(SiteResource site)
        throws StyleException;

    /**
     * Returns the layout root resource for a given site.
     *
     * @param site the site.
     * @return the layout root resource for a given site.
     * @throws <code>StyleException</code>
     */
    public Resource getLayoutRoot(SiteResource site)
        throws StyleException;

    /**
     * Return the component sockets in a layout.
     *
     * @param layout the layout.
     */
    public ComponentSocketResource[] getSockets(LayoutResource layout)
        throws StyleException;
    
    /**
     * Adds a component socket to a layout.
     *
     * @param layout the layout
     * @param name the name of the socket.
     * @param subject the subject that performs the operation.
     */
    public ComponentSocketResource addSocket(LayoutResource layout, 
                                             String name, Subject subject)
        throws StyleException;
    
    /**
     * Deletes a component socket from a layout.
     *
     * @param layout the layout.
     * @param name the name of the socket.
     * @param subject the subject that performs the operation.
     */
    public void deleteSocket(LayoutResource layout, String name, Subject subject)
        throws StyleException;

    /**
     * Parses the velocity template and finds declared component sockets.
     *
     * @param templateContents.
     * @return names of the declared sockets.
     */
    public String[] findSockets(String templateContents)
        throws StyleException;
        
    /**
     * Matches a set of sockets in a template with sockets defined for a layout.
     * 
     * @param layout a layout object.
     * @param sockets list of socket names.
     * @return <code>true</code> if the sockets sets are identical.
     */
    public boolean matchSockets(LayoutResource layout, String[] sockets)
        throws StyleException;
}
