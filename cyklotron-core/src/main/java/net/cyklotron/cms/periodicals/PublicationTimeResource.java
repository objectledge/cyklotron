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
 
package net.cyklotron.cms.periodicals;

import org.objectledge.coral.datatypes.Node;
import org.objectledge.coral.store.Resource;

/**
 * Defines the accessor methods of <code>cms.periodicals.publication_time</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface PublicationTimeResource
    extends Resource, Node
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the ARL resource class. */    
    public static final String CLASS_NAME = "cms.periodicals.publication_time";

    // public interface //////////////////////////////////////////////////////
	
    /**
     * Returns the value of the <code>day_of_month</code> attribute.
     *
     * @return the value of the the <code>day_of_month</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getDay_of_month()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>day_of_month</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>day_of_month</code> attribute.
     */
    public int getDay_of_month(int defaultValue);

    /**
     * Sets the value of the <code>day_of_month</code> attribute.
     *
     * @param value the value of the <code>day_of_month</code> attribute.
     */
    public void setDay_of_month(int value);

	/**
     * Removes the value of the <code>day_of_month</code> attribute.
     */
    public void unsetDay_of_month();
   
	/**
	 * Checks if the value of the <code>day_of_month</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>day_of_month</code> attribute is defined.
	 */
    public boolean isDay_of_monthDefined();
	
    /**
     * Returns the value of the <code>day_of_week</code> attribute.
     *
     * @return the value of the the <code>day_of_week</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getDay_of_week()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>day_of_week</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>day_of_week</code> attribute.
     */
    public int getDay_of_week(int defaultValue);

    /**
     * Sets the value of the <code>day_of_week</code> attribute.
     *
     * @param value the value of the <code>day_of_week</code> attribute.
     */
    public void setDay_of_week(int value);

	/**
     * Removes the value of the <code>day_of_week</code> attribute.
     */
    public void unsetDay_of_week();
   
	/**
	 * Checks if the value of the <code>day_of_week</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>day_of_week</code> attribute is defined.
	 */
    public boolean isDay_of_weekDefined();
	
    /**
     * Returns the value of the <code>hour</code> attribute.
     *
     * @return the value of the the <code>hour</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getHour()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>hour</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>hour</code> attribute.
     */
    public int getHour(int defaultValue);

    /**
     * Sets the value of the <code>hour</code> attribute.
     *
     * @param value the value of the <code>hour</code> attribute.
     */
    public void setHour(int value);

	/**
     * Removes the value of the <code>hour</code> attribute.
     */
    public void unsetHour();
   
	/**
	 * Checks if the value of the <code>hour</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>hour</code> attribute is defined.
	 */
    public boolean isHourDefined();
  
    // @custom methods ///////////////////////////////////////////////////////
}
