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
 
package net.cyklotron.cms.structure;

import java.util.Date;
import java.util.List;

import org.objectledge.coral.datatypes.Node;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.parameters.Parameters;

import net.cyklotron.cms.PrioritizedResource;
import net.cyklotron.cms.ProtectedResource;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.style.StyleResource;
import net.cyklotron.cms.workflow.StatefulResource;
import net.labeo.services.resource.Subject;

/**
 * Defines the accessor methods of <code>structure.navigation_node</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface NavigationNodeResource
    extends Resource, Node, PrioritizedResource, ProtectedResource, StatefulResource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the ARL resource class. */    
    public static final String CLASS_NAME = "structure.navigation_node";

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>administrator</code> attribute.
     *
     * @return the value of the the <code>administrator</code> attribute.
     */
    public Role getAdministrator();

    /**
     * Sets the value of the <code>administrator</code> attribute.
     *
     * @param value the value of the <code>administrator</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAdministrator(Role value);   
   
	/**
	 * Checks if the value of the <code>administrator</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>administrator</code> attribute is defined.
	 */
    public boolean isAdministratorDefined();
 
    /**
     * Returns the value of the <code>editor</code> attribute.
     *
     * @return the value of the the <code>editor</code> attribute.
     */
    public Role getEditor();

    /**
     * Sets the value of the <code>editor</code> attribute.
     *
     * @param value the value of the <code>editor</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEditor(Role value);   
   
	/**
	 * Checks if the value of the <code>editor</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>editor</code> attribute is defined.
	 */
    public boolean isEditorDefined();
	
    /**
     * Returns the value of the <code>editorial_priority</code> attribute.
     *
     * @return the value of the the <code>editorial_priority</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getEditorial_priority()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>editorial_priority</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>editorial_priority</code> attribute.
     */
    public int getEditorial_priority(int defaultValue);

    /**
     * Sets the value of the <code>editorial_priority</code> attribute.
     *
     * @param value the value of the <code>editorial_priority</code> attribute.
     */
    public void setEditorial_priority(int value);

	/**
     * Removes the value of the <code>editorial_priority</code> attribute.
     */
    public void unsetEditorial_priority();
   
	/**
	 * Checks if the value of the <code>editorial_priority</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>editorial_priority</code> attribute is defined.
	 */
    public boolean isEditorial_priorityDefined();
 
    /**
     * Returns the value of the <code>last_editor</code> attribute.
     *
     * @return the value of the the <code>last_editor</code> attribute.
     */
    public Subject getLast_editor();

    /**
     * Sets the value of the <code>last_editor</code> attribute.
     *
     * @param value the value of the <code>last_editor</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLast_editor(Subject value);   
   
	/**
	 * Checks if the value of the <code>last_editor</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>last_editor</code> attribute is defined.
	 */
    public boolean isLast_editorDefined();
 
    /**
     * Returns the value of the <code>last_redactor</code> attribute.
     *
     * @return the value of the the <code>last_redactor</code> attribute.
     */
    public Subject getLast_redactor();

    /**
     * Sets the value of the <code>last_redactor</code> attribute.
     *
     * @param value the value of the <code>last_redactor</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLast_redactor(Subject value);   
   
	/**
	 * Checks if the value of the <code>last_redactor</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>last_redactor</code> attribute is defined.
	 */
    public boolean isLast_redactorDefined();
 
    /**
     * Returns the value of the <code>local_visitor</code> attribute.
     *
     * @return the value of the the <code>local_visitor</code> attribute.
     */
    public Role getLocal_visitor();

    /**
     * Sets the value of the <code>local_visitor</code> attribute.
     *
     * @param value the value of the <code>local_visitor</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLocal_visitor(Role value);   
   
	/**
	 * Checks if the value of the <code>local_visitor</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>local_visitor</code> attribute is defined.
	 */
    public boolean isLocal_visitorDefined();
 
    /**
     * Returns the value of the <code>locked_by</code> attribute.
     *
     * @return the value of the the <code>locked_by</code> attribute.
     */
    public Subject getLocked_by();

    /**
     * Sets the value of the <code>locked_by</code> attribute.
     *
     * @param value the value of the <code>locked_by</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLocked_by(Subject value);   
   
	/**
	 * Checks if the value of the <code>locked_by</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>locked_by</code> attribute is defined.
	 */
    public boolean isLocked_byDefined();
 
    /**
     * Returns the value of the <code>preferences</code> attribute.
     *
     * @return the value of the the <code>preferences</code> attribute.
     */
    public Parameters getPreferences();
   
    /**
     * Returns the value of the <code>redactor</code> attribute.
     *
     * @return the value of the the <code>redactor</code> attribute.
     */
    public Role getRedactor();

    /**
     * Sets the value of the <code>redactor</code> attribute.
     *
     * @param value the value of the <code>redactor</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRedactor(Role value);   
   
	/**
	 * Checks if the value of the <code>redactor</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>redactor</code> attribute is defined.
	 */
    public boolean isRedactorDefined();
 
    /**
     * Returns the value of the <code>reporter</code> attribute.
     *
     * @return the value of the the <code>reporter</code> attribute.
     */
    public Role getReporter();

    /**
     * Sets the value of the <code>reporter</code> attribute.
     *
     * @param value the value of the <code>reporter</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setReporter(Role value);   
   
	/**
	 * Checks if the value of the <code>reporter</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>reporter</code> attribute is defined.
	 */
    public boolean isReporterDefined();
	
    /**
     * Returns the value of the <code>sequence</code> attribute.
     *
     * @return the value of the the <code>sequence</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getSequence()
		throws IllegalStateException;

	/**
     * Returns the value of the <code>sequence</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>sequence</code> attribute.
     */
    public int getSequence(int defaultValue);

    /**
     * Sets the value of the <code>sequence</code> attribute.
     *
     * @param value the value of the <code>sequence</code> attribute.
     */
    public void setSequence(int value);

	/**
     * Removes the value of the <code>sequence</code> attribute.
     */
    public void unsetSequence();
   
	/**
	 * Checks if the value of the <code>sequence</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>sequence</code> attribute is defined.
	 */
    public boolean isSequenceDefined();
 
    /**
     * Returns the value of the <code>site</code> attribute.
     *
     * @return the value of the the <code>site</code> attribute.
     */
    public SiteResource getSite();
 
    /**
     * Sets the value of the <code>site</code> attribute.
     *
     * @param value the value of the <code>site</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setSite(SiteResource value)
        throws ValueRequiredException;
    
    /**
     * Returns the value of the <code>style</code> attribute.
     *
     * @return the value of the the <code>style</code> attribute.
     */
    public StyleResource getStyle();

    /**
     * Sets the value of the <code>style</code> attribute.
     *
     * @param value the value of the <code>style</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setStyle(StyleResource value);   
   
	/**
	 * Checks if the value of the <code>style</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>style</code> attribute is defined.
	 */
    public boolean isStyleDefined();
 
    /**
     * Returns the value of the <code>thumbnail</code> attribute.
     *
     * @return the value of the the <code>thumbnail</code> attribute.
     */
    public FileResource getThumbnail();

    /**
     * Sets the value of the <code>thumbnail</code> attribute.
     *
     * @param value the value of the <code>thumbnail</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setThumbnail(FileResource value);   
   
	/**
	 * Checks if the value of the <code>thumbnail</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>thumbnail</code> attribute is defined.
	 */
    public boolean isThumbnailDefined();
 
    /**
     * Returns the value of the <code>title</code> attribute.
     *
     * @return the value of the the <code>title</code> attribute.
     */
    public String getTitle();
 
    /**
     * Sets the value of the <code>title</code> attribute.
     *
     * @param value the value of the <code>title</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setTitle(String value)
        throws ValueRequiredException;
    
    /**
     * Returns the value of the <code>validity_end</code> attribute.
     *
     * @return the value of the the <code>validity_end</code> attribute.
     */
    public Date getValidity_end();

    /**
     * Sets the value of the <code>validity_end</code> attribute.
     *
     * @param value the value of the <code>validity_end</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setValidity_end(Date value);   
   
	/**
	 * Checks if the value of the <code>validity_end</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>validity_end</code> attribute is defined.
	 */
    public boolean isValidity_endDefined();
 
    /**
     * Returns the value of the <code>validity_start</code> attribute.
     *
     * @return the value of the the <code>validity_start</code> attribute.
     */
    public Date getValidity_start();

    /**
     * Sets the value of the <code>validity_start</code> attribute.
     *
     * @param value the value of the <code>validity_start</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setValidity_start(Date value);   
   
	/**
	 * Checks if the value of the <code>validity_start</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>validity_start</code> attribute is defined.
	 */
    public boolean isValidity_startDefined();
 
    /**
     * Returns the value of the <code>visitor</code> attribute.
     *
     * @return the value of the the <code>visitor</code> attribute.
     */
    public Role getVisitor();

    /**
     * Sets the value of the <code>visitor</code> attribute.
     *
     * @param value the value of the <code>visitor</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setVisitor(Role value);   
   
	/**
	 * Checks if the value of the <code>visitor</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>visitor</code> attribute is defined.
	 */
    public boolean isVisitorDefined();
  
    // @custom methods ///////////////////////////////////////////////////////

    // @import java.util.List
    // @import net.labeo.services.resource.Subject
    // @import net.cyklotron.cms.style.StyleResource

    public int getSequence(int defaultValue);
    
    /**
     * Returns the path relative to site's structure root node, ie. including site's home page.
     *
     * @return path relative to site's structure node.
     */
    public String getSitePath();

    /**
     * Returns the effective value of the <code>style</code> attribute.
     * If local style is not set, value is inherited from ancestor.
     *
     * @return the effective value of the <code>style</code> attribute.
     */
    public StyleResource getEffectiveStyle();

    public int getLevel();

    public List getParentNavigationNodes(boolean includeRoot);    

    /** Checks validity time constraints. */
    public boolean isValid(Date time);

    /**
     * Checks if a given subject has a view permission assignment on this node.
     */
    public boolean canView(Subject subject);

    /**
     * Checks if the specified subject can access this navigation node at the given time.
     */
    public boolean canView(Subject subject, Date time);
}
