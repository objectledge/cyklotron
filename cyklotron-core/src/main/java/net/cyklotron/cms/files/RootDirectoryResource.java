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
 
package net.cyklotron.cms.files;

import org.objectledge.coral.store.Resource;

/**
 * Defines the accessor methods of <code>cms.files.root_directory</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface RootDirectoryResource
    extends Resource, DirectoryResource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the Coral resource class. */    
    public static final String CLASS_NAME = "cms.files.root_directory";

    // public interface //////////////////////////////////////////////////////
	
    /**
     * Returns the value of the <code>external</code> attribute.
     *
     * @return the value of the the <code>external</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getExternal()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>external</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>external</code> attribute.
     */
    public boolean getExternal(boolean defaultValue);

    /**
     * Sets the value of the <code>external</code> attribute.
     *
     * @param value the value of the <code>external</code> attribute.
     */
    public void setExternal(boolean value);

	/**
     * Removes the value of the <code>external</code> attribute.
     */
    public void unsetExternal();
   
	/**
	 * Checks if the value of the <code>external</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>external</code> attribute is defined.
	 */
    public boolean isExternalDefined();
 
    /**
     * Returns the value of the <code>rootPath</code> attribute.
     *
     * @return the value of the the <code>rootPath</code> attribute.
     */
    public String getRootPath();
    
    /**
     * Returns the value of the <code>rootPath</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>rootPath</code> attribute.
     */
    public String getRootPath(String defaultValue);

    /**
     * Sets the value of the <code>rootPath</code> attribute.
     *
     * @param value the value of the <code>rootPath</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRootPath(String value);   
   
	/**
	 * Checks if the value of the <code>rootPath</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>rootPath</code> attribute is defined.
	 */
    public boolean isRootPathDefined();
  
    // @custom methods ///////////////////////////////////////////////////////
}
