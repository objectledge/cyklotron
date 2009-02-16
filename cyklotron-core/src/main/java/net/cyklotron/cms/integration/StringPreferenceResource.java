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
 
package net.cyklotron.cms.integration;

import org.objectledge.coral.store.Resource;

/**
 * Defines the accessor methods of <code>integration.string_preference</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface StringPreferenceResource
    extends Resource, PreferenceResource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the ARL resource class. */    
    public static final String CLASS_NAME = "integration.string_preference";

    // public interface //////////////////////////////////////////////////////
	
    /**
     * Returns the value of the <code>maxLength</code> attribute.
     *
     * @return the value of the the <code>maxLength</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getMaxLength()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>maxLength</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>maxLength</code> attribute.
     */
    public int getMaxLength(int defaultValue);

    /**
     * Sets the value of the <code>maxLength</code> attribute.
     *
     * @param value the value of the <code>maxLength</code> attribute.
     */
    public void setMaxLength(int value);

	/**
     * Removes the value of the <code>maxLength</code> attribute.
     */
    public void unsetMaxLength();
   
	/**
	 * Checks if the value of the <code>maxLength</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>maxLength</code> attribute is defined.
	 */
    public boolean isMaxLengthDefined();
	
    /**
     * Returns the value of the <code>minLength</code> attribute.
     *
     * @return the value of the the <code>minLength</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getMinLength()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>minLength</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>minLength</code> attribute.
     */
    public int getMinLength(int defaultValue);

    /**
     * Sets the value of the <code>minLength</code> attribute.
     *
     * @param value the value of the <code>minLength</code> attribute.
     */
    public void setMinLength(int value);

	/**
     * Removes the value of the <code>minLength</code> attribute.
     */
    public void unsetMinLength();
   
	/**
	 * Checks if the value of the <code>minLength</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>minLength</code> attribute is defined.
	 */
    public boolean isMinLengthDefined();
  
    // @custom methods ///////////////////////////////////////////////////////
}
