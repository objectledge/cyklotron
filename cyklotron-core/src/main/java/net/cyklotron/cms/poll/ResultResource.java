package net.cyklotron.cms.poll;

import java.util.Date;
import java.util.List;

import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceClass;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.resource.generic.NodeResource;
import net.labeo.services.resource.generic.NodeResourceImpl;

/**
 * Defines the accessor methods of <code>cms.poll.result</code> ARL resource class.
 *
 * @author MakeWrappers tool
 */
public interface ResultResource
    extends Resource, NodeResource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the ARL resource class. */    
    public static final String CLASS_NAME = "cms.poll.result";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>answers</code> attribute.
     *
     * @return the value of the the <code>answers</code> attribute.
     */
    public List getAnswers();

    /**
     * Sets the value of the <code>answers</code> attribute.
     *
     * @param value the value of the <code>answers</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAnswers(List value);   
     
    // @custom methods ///////////////////////////////////////////////////////
}
