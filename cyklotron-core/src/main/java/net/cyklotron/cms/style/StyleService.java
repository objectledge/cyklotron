package net.cyklotron.cms.style;

import java.util.List;

import net.cyklotron.cms.site.SiteResource;

import org.objectledge.coral.entity.AmbigousEntityNameException;
import org.objectledge.coral.schema.CircularDependencyException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;

public interface StyleService
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
     * @return style resource.
     */
    public StyleResource addStyle(CoralSession coralSession, String name, String description, 
                                  SiteResource site, StyleResource parent)
        throws StyleException, AmbigousEntityNameException;

    /**
     * returns the documents that have the style explicytly set.
     * 
     * @param style the style.
     */
    public List getReferringNodes(CoralSession coralSession, StyleResource style)
        throws StyleException;
    
    /** 
     * delete the style from the system.
     *
     * @param style the style to delete.
     */
    public void deleteStyle(CoralSession coralSession, StyleResource style)
        throws StyleException;

    /**
     * Update the style info.
     *
     * @param name the name of the style.
     * @param description the description of the style.
     * @param parent the parent style or <code>null</code> for top level style.
     */
    public void updateStyle(CoralSession coralSession, StyleResource resource, String name, String description, 
                            StyleResource parent)
        throws AmbigousEntityNameException, CircularDependencyException, StyleException;

    /**
     * Returns the site a style belongs to.
     *
     * @param style the style resource.
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
    public StyleResource getStyle(CoralSession coralSession, SiteResource site, String style)
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
    public StyleResource[] getSubStyles(CoralSession coralSession, StyleResource style);

    /**
     * Return the full list of styles.
     *
     * @param site the site.
     * @return the style resource.
     */
    public StyleResource[] getStyles(CoralSession coralSession, SiteResource site)
        throws StyleException;

    /**
     * Returns the style root resource for a given site.
     *
     * @param site the site.
     * @return the style root resource for a given site.
	 * @throws StyleException
     */
    public Resource getStyleRoot(CoralSession coralSession, SiteResource site)
        throws StyleException;

    // levels ////////////////////////////////////////////////////////////////

    /**
     * Add level to the style.
     *
     * @param style the style.
     * @param layout the layout.
     * @param level the level.
     * @param description the description.
     * @return the level resource.
     */
    public LevelResource addLevel(CoralSession coralSession, StyleResource style, LayoutResource layout, 
                                  int level, String description)
        throws StyleException;
    
    /** 
     * delete the level from the system.
     *
     * @param level the level to delete.
     */
    public void deleteLevel(CoralSession coralSession, LevelResource level)
        throws StyleException;

    /** 
     * Return the layout level resource.
     *
     * @param style the style.
     * @param level the level.
     * @return the level resources.
     */    
    public LevelResource getLevel(CoralSession coralSession, StyleResource style, int level);
    
    /** 
     * Return all definied levels for the style.
     *
     * @param style the style.
     * @return the list of level resources.
     */
    public LevelResource[] getLevels(CoralSession coralSession, StyleResource style);

    /**
     * Return the layout template name corresponding to the style and level.
     *
     * @param style the style resource.
     * @param level the level.
     * @return the layout name.
     */
    public String getLayout(CoralSession coralSession, StyleResource style, int level);

    // layouts ///////////////////////////////////////////////////////////////

    /** 
     * Add new layout to the system.
     *
     * @param name the name of the layout.
     * @param description the description of the layout.
     * @param site the site.
     * @return layout resource.
     */
    public LayoutResource addLayout(CoralSession coralSession, String name, String description, 
                                    SiteResource site)
        throws StyleException, AmbigousEntityNameException;
    
    /** 
     * delete the layout from the system.
     *
     * @param layout the layout to delete.
     */
    public void deleteLayout(CoralSession coralSession, LayoutResource layout)
        throws StyleException;

    /**
     * Update the layout info.
     *
     * @param resource the layout resource.
     * @param name the name of the layout.
     * @param description the description of the layout.
     */
    public void updateLayout(CoralSession coralSession, LayoutResource resource, String name, 
                             String description)
        throws StyleException, AmbigousEntityNameException;
    
    /**
     * Return the layout.
     *
     * @param site the site.
     * @param layout the name of the layout.
     * @return the layout resource, or <code>null</code> if not found.
     */
    public LayoutResource getLayout(CoralSession coralSession, SiteResource site, String layout)
        throws StyleException;

    /**
     * Return full list of layouts.
     *
     * @param site the site.
     * @return the list of layouts.
     */
    public LayoutResource[] getLayouts(CoralSession coralSession, SiteResource site)
        throws StyleException;

    /**
     * Returns the layout root resource for a given site.
     *
     * @param site the site.
     * @return the layout root resource for a given site.
     * @throws StyleException
     */
    public Resource getLayoutRoot(CoralSession coralSession, SiteResource site)
        throws StyleException;

    /**
     * Return the component sockets in a layout.
     *
     * @param layout the layout.
     */
    public ComponentSocketResource[] getSockets(CoralSession coralSession, LayoutResource layout)
        throws StyleException;
    
    /**
     * Adds a component socket to a layout.
     *
     * @param layout the layout
     * @param name the name of the socket.
     */
    public ComponentSocketResource addSocket(CoralSession coralSession, LayoutResource layout, 
                                             String name)
        throws StyleException;
    
    /**
     * Deletes a component socket from a layout.
     *
     * @param layout the layout.
     * @param name the name of the socket.
     */
    public void deleteSocket(CoralSession coralSession, LayoutResource layout, String name)
        throws StyleException;

    /**
     * Parses the velocity template and finds declared component sockets.
     *
     * @param templateContents
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
    public boolean matchSockets(CoralSession coralSession, LayoutResource layout, String[] sockets)
        throws StyleException;
}
