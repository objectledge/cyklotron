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

import org.objectledge.coral.datatypes.Node;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

/**
 * Defines the accessor methods of <code>integration.application</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface ApplicationResource
    extends Resource, Node
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the ARL resource class. */    
    public static final String CLASS_NAME = "integration.application";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>applicationName</code> attribute.
     *
     * @return the value of the the <code>applicationName</code> attribute.
     */
    public String getApplicationName();
 
    /**
     * Sets the value of the <code>applicationName</code> attribute.
     *
     * @param value the value of the <code>applicationName</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setApplicationName(String value)
        throws ValueRequiredException;
   	
    /**
     * Returns the value of the <code>enabled</code> attribute.
     *
     * @return the value of the the <code>enabled</code> attribute.
     */
    public boolean getEnabled();

    /**
     * Sets the value of the <code>enabled</code> attribute.
     *
     * @param value the value of the <code>enabled</code> attribute.
     */
    public void setEnabled(boolean value);
   	
    /**
     * Returns the value of the <code>priority</code> attribute.
     *
     * @return the value of the the <code>priority</code> attribute.
     */
    public int getPriority();

    /**
     * Sets the value of the <code>priority</code> attribute.
     *
     * @param value the value of the <code>priority</code> attribute.
     */
    public void setPriority(int value);
    
    /**
     * Returns the value of the <code>vendor</code> attribute.
     *
     * @return the value of the the <code>vendor</code> attribute.
     */
    public String getVendor();
 
    /**
     * Sets the value of the <code>vendor</code> attribute.
     *
     * @param value the value of the <code>vendor</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setVendor(String value)
        throws ValueRequiredException;
    
    /**
     * Returns the value of the <code>version</code> attribute.
     *
     * @return the value of the the <code>version</code> attribute.
     */
    public String getVersion();
 
    /**
     * Sets the value of the <code>version</code> attribute.
     *
     * @param value the value of the <code>version</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setVersion(String value)
        throws ValueRequiredException;
     
    // @custom methods ///////////////////////////////////////////////////////
}
