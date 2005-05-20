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

import java.util.Date;

import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.store.Resource;

/**
 * Defines the accessor methods of <code>search.indexable</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public interface IndexableResource
    extends Resource
{
    // constants /////////////////////////////////////////////////////////////

    /** The name of the ARL resource class. */    
    public static final String CLASS_NAME = "search.indexable";
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

    /**
     * Returns a content of a <code>index_title</code> field which will be shown as a title of
     * a search result. This field is indexed, tokenized and stored.
     *
     * @return title of a search result.
     */
    public String getIndexTitle();

    /**
     * Returns a content of a <code>index_abbreviation</code> field which will be shown as an
     * excerpt in a search result. This field is indexed, tokenized and stored.
     *
     * @return excerpt of a search result.
     */
    public String getIndexAbbreviation();

    /**
     * Returns a content of a <code>index_content</code> field which will be used as a main field
     * for searching. This field is indexed and tokenized.
     *
     * @return content of content field
     */
    public String getIndexContent();
    
    /**
     * Returns the contents of a additionally defined fields.
     * As this method returns an object it is up to the indexer to interpret varoius object classe.
     * An assumption is made that only basic datatypes are returned, ie.:
     * <ul>
     * <li>Date</li>
     * <li>String</li>
     * <li>Long</li>
     * <li>Integer</li>
     * <li>Double</li>
     * <li>Float</li>
     * </ul>
     * 
     * <p>Apart from that, ARL attributes are supported, including:</p>
     * <ul> 
     * <li>Subject</li>
     * </ul>
     * 
     * <p>From the indexer's point of view, a resource is composed of a number
     * of textual fields. Usualy there is a one to one mapping to the
     * resource's attributes, but it's not neccesarily the case. A field may be
     * composed of a series of tokens that can be retrieved sequentially.</p>
     *
     * @return the field content.
     */
    public Object getFieldValue(String fieldName);
    
	/**
	 * Returns the store flag of the field.
	 *
	 * @return the store flag.
	 */
	public boolean isStored(String fieldName);
	
	/**
	 * Returns the indexed flag of the field.
	 *
	 * @return the indexed flag.
	 */
	public boolean isIndexed(String fieldName);
		
	/**
	 * Returns the tokenized flag of the field.
	 *
	 * @return the tokenized flag.
	 */
	public boolean isTokenized(String fieldName);
}
