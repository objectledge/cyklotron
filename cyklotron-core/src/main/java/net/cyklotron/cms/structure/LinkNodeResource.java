package net.cyklotron.cms.structure;

import java.util.Date;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.style.StyleResource;
import net.cyklotron.services.workflow.StateResource;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceClass;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.util.configuration.ParameterContainer;

/**
 * Defines the accessor methods of <code>structure.link_node</code> ARL resource class.
 *
 * @author MakeWrappers tool
 */
public interface LinkNodeResource
    extends Resource, NavigationNodeResource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the ARL resource class. */    
    public static final String CLASS_NAME = "structure.link_node";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>target</code> attribute.
     *
     * @return the value of the the <code>target</code> attribute.
     */
    public String getTarget();
 
    /**
     * Sets the value of the <code>target</code> attribute.
     *
     * @param value the value of the <code>target</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setTarget(String value)
        throws ValueRequiredException;
     
    // @custom methods ///////////////////////////////////////////////////////
}
