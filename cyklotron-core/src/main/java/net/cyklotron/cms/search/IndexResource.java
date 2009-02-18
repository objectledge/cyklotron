// 
// Copyright (c) 2004, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
// 
// * Redistributions of source code must retain the above copyright notice,  
//       this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//       this list of conditions and the following disclaimer in the documentation  
//       and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//       nor the names of its contributors may be used to endorse or promote products  
//       derived from this software without specific prior written permission. 
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
// POSSIBILITY OF SUCH DAMAGE. 
// 
 
package net.cyklotron.cms.search;

import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

import net.cyklotron.cms.CmsNodeResource;

/**
 * Defines the accessor methods of <code>search.index</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface IndexResource
    extends Resource, CmsNodeResource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the ARL resource class. */    
    public static final String CLASS_NAME = "search.index";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>filesLocation</code> attribute.
     *
     * @return the value of the the <code>filesLocation</code> attribute.
     */
    public String getFilesLocation();
 
    /**
     * Sets the value of the <code>filesLocation</code> attribute.
     *
     * @param value the value of the <code>filesLocation</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setFilesLocation(String value)
        throws ValueRequiredException;
   	
    /**
     * Returns the value of the <code>optimise</code> attribute.
     *
     * @return the value of the the <code>optimise</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getOptimise()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>optimise</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>optimise</code> attribute.
     */
    public boolean getOptimise(boolean defaultValue);

    /**
     * Sets the value of the <code>optimise</code> attribute.
     *
     * @param value the value of the <code>optimise</code> attribute.
     */
    public void setOptimise(boolean value);

	/**
     * Removes the value of the <code>optimise</code> attribute.
     */
    public void unsetOptimise();
   
	/**
	 * Checks if the value of the <code>optimise</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>optimise</code> attribute is defined.
	 */
    public boolean isOptimiseDefined();
	
    /**
     * Returns the value of the <code>public</code> attribute.
     *
     * @return the value of the the <code>public</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getPublic()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>public</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>public</code> attribute.
     */
    public boolean getPublic(boolean defaultValue);

    /**
     * Sets the value of the <code>public</code> attribute.
     *
     * @param value the value of the <code>public</code> attribute.
     */
    public void setPublic(boolean value);

	/**
     * Removes the value of the <code>public</code> attribute.
     */
    public void unsetPublic();
   
	/**
	 * Checks if the value of the <code>public</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>public</code> attribute is defined.
	 */
    public boolean isPublicDefined();
  
    // @custom methods ///////////////////////////////////////////////////////

}
