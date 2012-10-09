//
// Copyright (c) 2012, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  All rights reserved. 
// 
package net.cyklotron.cms.docimport;

import java.util.Date;

import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

import net.cyklotron.cms.CmsNodeResource;

/**
 * Defines the accessor methods of <code>docimport.trace</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface ImportTraceResource
    extends Resource, CmsNodeResource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the Coral resource class. */    
    public static final String CLASS_NAME = "docimport.trace";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>navigationNode</code> attribute.
     *
     * @return the value of the the <code>navigationNode</code> attribute.
     */
    public Resource getNavigationNode();
 
    /**
     * Sets the value of the <code>navigationNode</code> attribute.
     *
     * @param value the value of the <code>navigationNode</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setNavigationNode(Resource value)
        throws ValueRequiredException;
    
    /**
     * Returns the value of the <code>originalURL</code> attribute.
     *
     * @return the value of the the <code>originalURL</code> attribute.
     */
    public String getOriginalURL();
 
    /**
     * Sets the value of the <code>originalURL</code> attribute.
     *
     * @param value the value of the <code>originalURL</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setOriginalURL(String value)
        throws ValueRequiredException;
    
    /**
     * Returns the value of the <code>sourceModificationTime</code> attribute.
     *
     * @return the value of the the <code>sourceModificationTime</code> attribute.
     */
    public Date getSourceModificationTime();
 
    /**
     * Sets the value of the <code>sourceModificationTime</code> attribute.
     *
     * @param value the value of the <code>sourceModificationTime</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setSourceModificationTime(Date value)
        throws ValueRequiredException;
    
    /**
     * Returns the value of the <code>targetUpdateTime</code> attribute.
     *
     * @return the value of the the <code>targetUpdateTime</code> attribute.
     */
    public Date getTargetUpdateTime();
 
    /**
     * Sets the value of the <code>targetUpdateTime</code> attribute.
     *
     * @param value the value of the <code>targetUpdateTime</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setTargetUpdateTime(Date value)
        throws ValueRequiredException;
     
    // @custom methods ///////////////////////////////////////////////////////
}
