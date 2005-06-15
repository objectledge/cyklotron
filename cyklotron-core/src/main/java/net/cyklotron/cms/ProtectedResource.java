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
 
package net.cyklotron.cms;

import java.util.Date;

import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;

/**
 * Defines the accessor methods of <code>protected</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface ProtectedResource
    extends Resource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the ARL resource class. */    
    public static final String CLASS_NAME = "protected";
     /**
     * Returns the path name of the resource.
     *
     * <p>The path name is composed of the names of all of the resource's
     * parents, separated by / characters. If the top level parent (resource
     * that has <code>null</code> parent) is the 'root' resource #1, the
     * pathname will start with a /. Please note that the pathname can also
     * denote other resources than this one, unless all resources in your
     * system have unique names.</p>
     *
     * @return the pathname of the resource.
     */
    public String getPath();
    
    /**
     * Returns the parent resource.
     *
     * <p><code>null</code> is returned for top-level (root)
     * resources. Depending on the application one or more top-level resources
     * exist in the system.</p>
     *
     * @return the parent resource.
     */
    public Resource getParent();

	/**
     * Returns the class this resource belongs to.
     *
     * @return the class this resource belongs to.
     */
    public ResourceClass getResourceClass();

    /**
     * Returns the owner of the resource.
     *
     * @return the owner of the resource.
     */
    public Subject getOwner();	

	/**
     * Returns the {@link Subject} that created this resource.
     *
     * @return the {@link Subject} that created this resource.
     */
    public Subject getCreatedBy();
    
    /**
     * Returns the creation time for this resource.
     *
     * @return the creation time for this resource.
     */
    public Date getCreationTime();

    /**
     * Returns the {@link Subject} that modified this resource most recently.
     *
     * @return the {@link Subject} that modified this resource most recently.
     */
    public Subject getModifiedBy();

    /**
     * Returns the last modification time for this resource.
     *
     * @return the last modification time for this resource.
     */
    public Date getModificationTime();

    // @custom methods ///////////////////////////////////////////////////////

    // @import java.util.Date
    // @import org.objectledge.context.Context
    // @import org.objectledge.coral.security.Subject


    /**
     * Checks if this resource can be viewed at the given time.
     */
    public boolean isValid(Date time);

    /**
     * Checks if a given subject can view this resource.
     */
    public boolean canView(CoralSession coralSession, Subject subject);

    /**
     * Checks if the specified subject can modify this resource.
     */
    public boolean canModify(CoralSession coralSession, Subject subject);
    
    /**
     * Checks if the specified subject can remove this resource.
     * @param coralSession TODO
     */
    public boolean canRemove(CoralSession coralSession, Subject subject);
    
    /**
     * Checks if the specified subject can add children to this resource.
     */
    public boolean canAddChild(CoralSession coralSession, Subject subject);
   
    /**
     * Checks if the specified subject can view this resource at the given time.
     */
    public boolean canView(CoralSession coralSession, Subject subject, Date time);

    /**
     * Checks if the specified subject can view this resource at the given time.
     */
    public boolean canView(CoralSession coralSession, CmsData data, Subject subject);
}
