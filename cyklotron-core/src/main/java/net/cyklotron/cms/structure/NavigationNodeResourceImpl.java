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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.BackendException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.CoralSchema;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.database.Database;
import org.objectledge.parameters.Parameters;

import net.cyklotron.cms.CmsConstants;
import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsNodeResourceImpl;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.style.StyleResource;
import net.cyklotron.cms.workflow.StateResource;

/**
 * An implementation of <code>structure.navigation_node</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class NavigationNodeResourceImpl
    extends CmsNodeResourceImpl
    implements NavigationNodeResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>title</code> attribute. */
    private AttributeDefinition titleDef;

    /** The AttributeDefinition object for the <code>site</code> attribute. */
    private AttributeDefinition siteDef;

    /** The AttributeDefinition object for the <code>preferences</code> attribute. */
    private AttributeDefinition preferencesDef;

    /** The AttributeDefinition object for the <code>administrator</code> attribute. */
    private AttributeDefinition administratorDef;

    /** The AttributeDefinition object for the <code>editor</code> attribute. */
    private AttributeDefinition editorDef;

    /** The AttributeDefinition object for the <code>editorialPriority</code> attribute. */
    private AttributeDefinition editorialPriorityDef;

    /** The AttributeDefinition object for the <code>lastEditor</code> attribute. */
    private AttributeDefinition lastEditorDef;

    /** The AttributeDefinition object for the <code>lastRedactor</code> attribute. */
    private AttributeDefinition lastRedactorDef;

    /** The AttributeDefinition object for the <code>localVisitor</code> attribute. */
    private AttributeDefinition localVisitorDef;

    /** The AttributeDefinition object for the <code>lockedBy</code> attribute. */
    private AttributeDefinition lockedByDef;

    /** The AttributeDefinition object for the <code>priority</code> attribute. */
    private AttributeDefinition priorityDef;

    /** The AttributeDefinition object for the <code>redactor</code> attribute. */
    private AttributeDefinition redactorDef;

    /** The AttributeDefinition object for the <code>reporter</code> attribute. */
    private AttributeDefinition reporterDef;

    /** The AttributeDefinition object for the <code>sequence</code> attribute. */
    private AttributeDefinition sequenceDef;

    /** The AttributeDefinition object for the <code>state</code> attribute. */
    private AttributeDefinition stateDef;

    /** The AttributeDefinition object for the <code>style</code> attribute. */
    private AttributeDefinition styleDef;

    /** The AttributeDefinition object for the <code>thumbnail</code> attribute. */
    private AttributeDefinition thumbnailDef;

    /** The AttributeDefinition object for the <code>validityEnd</code> attribute. */
    private AttributeDefinition validityEndDef;

    /** The AttributeDefinition object for the <code>validityStart</code> attribute. */
    private AttributeDefinition validityStartDef;

    /** The AttributeDefinition object for the <code>visitor</code> attribute. */
    private AttributeDefinition visitorDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>structure.navigation_node</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public NavigationNodeResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("structure.navigation_node");
            titleDef = rc.getAttribute("title");
            siteDef = rc.getAttribute("site");
            preferencesDef = rc.getAttribute("preferences");
            administratorDef = rc.getAttribute("administrator");
            editorDef = rc.getAttribute("editor");
            editorialPriorityDef = rc.getAttribute("editorialPriority");
            lastEditorDef = rc.getAttribute("lastEditor");
            lastRedactorDef = rc.getAttribute("lastRedactor");
            localVisitorDef = rc.getAttribute("localVisitor");
            lockedByDef = rc.getAttribute("lockedBy");
            priorityDef = rc.getAttribute("priority");
            redactorDef = rc.getAttribute("redactor");
            reporterDef = rc.getAttribute("reporter");
            sequenceDef = rc.getAttribute("sequence");
            stateDef = rc.getAttribute("state");
            styleDef = rc.getAttribute("style");
            thumbnailDef = rc.getAttribute("thumbnail");
            validityEndDef = rc.getAttribute("validityEnd");
            validityStartDef = rc.getAttribute("validityStart");
            visitorDef = rc.getAttribute("visitor");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>structure.navigation_node</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static NavigationNodeResource getNavigationNodeResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof NavigationNodeResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not structure.navigation_node");
        }
        return (NavigationNodeResource)res;
    }

    /**
     * Creates a new <code>structure.navigation_node</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param title the title attribute
     * @param site the site attribute
     * @param preferences the preferences attribute
     * @return a new NavigationNodeResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     */
    public static NavigationNodeResource createNavigationNodeResource(CoralSession session,
        String name, Resource parent, String title, SiteResource site, Parameters preferences)
        throws ValueRequiredException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("structure.navigation_node");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("title"), title);
            attrs.put(rc.getAttribute("site"), site);
            attrs.put(rc.getAttribute("preferences"), preferences);
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof NavigationNodeResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (NavigationNodeResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>title</code> attribute.
     *
     * @return the value of the <code>title</code> attribute.
     */
    public String getTitle()
    {
        return (String)get(titleDef);
    }
 
    /**
     * Sets the value of the <code>title</code> attribute.
     *
     * @param value the value of the <code>title</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setTitle(String value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(titleDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute title "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>site</code> attribute.
     *
     * @return the value of the <code>site</code> attribute.
     */
    public SiteResource getSite()
    {
        return (SiteResource)get(siteDef);
    }
 
    /**
     * Sets the value of the <code>site</code> attribute.
     *
     * @param value the value of the <code>site</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setSite(SiteResource value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(siteDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute site "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>preferences</code> attribute.
     *
     * @return the value of the <code>preferences</code> attribute.
     */
    public Parameters getPreferences()
    {
        return (Parameters)get(preferencesDef);
    }
   
    /**
     * Returns the value of the <code>administrator</code> attribute.
     *
     * @return the value of the <code>administrator</code> attribute.
     */
    public Role getAdministrator()
    {
        return (Role)get(administratorDef);
    }
    
    /**
     * Returns the value of the <code>administrator</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>administrator</code> attribute.
     */
    public Role getAdministrator(Role defaultValue)
    {
        if(isDefined(administratorDef))
        {
            return (Role)get(administratorDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>administrator</code> attribute.
     *
     * @param value the value of the <code>administrator</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAdministrator(Role value)
    {
        try
        {
            if(value != null)
            {
                set(administratorDef, value);
            }
            else
            {
                unset(administratorDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>administrator</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>administrator</code> attribute is defined.
	 */
    public boolean isAdministratorDefined()
	{
	    return isDefined(administratorDef);
	}
 
    /**
     * Returns the value of the <code>editor</code> attribute.
     *
     * @return the value of the <code>editor</code> attribute.
     */
    public Role getEditor()
    {
        return (Role)get(editorDef);
    }
    
    /**
     * Returns the value of the <code>editor</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>editor</code> attribute.
     */
    public Role getEditor(Role defaultValue)
    {
        if(isDefined(editorDef))
        {
            return (Role)get(editorDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>editor</code> attribute.
     *
     * @param value the value of the <code>editor</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEditor(Role value)
    {
        try
        {
            if(value != null)
            {
                set(editorDef, value);
            }
            else
            {
                unset(editorDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>editor</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>editor</code> attribute is defined.
	 */
    public boolean isEditorDefined()
	{
	    return isDefined(editorDef);
	}

    /**
     * Returns the value of the <code>editorialPriority</code> attribute.
     *
     * @return the value of the <code>editorialPriority</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getEditorialPriority()
        throws IllegalStateException
    {
        if(isDefined(editorialPriorityDef))
        {
            return ((Integer)get(editorialPriorityDef)).intValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute editorialPriority is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>editorialPriority</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>editorialPriority</code> attribute.
     */
    public int getEditorialPriority(int defaultValue)
    {
        if(isDefined(editorialPriorityDef))
        {
            return ((Integer)get(editorialPriorityDef)).intValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>editorialPriority</code> attribute.
     *
     * @param value the value of the <code>editorialPriority</code> attribute.
     */
    public void setEditorialPriority(int value)
    {
        try
        {
            set(editorialPriorityDef, new Integer(value));
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
	
	/**
     * Removes the value of the <code>editorialPriority</code> attribute.
     */
    public void unsetEditorialPriority()
    {
        try
        {
            unset(editorialPriorityDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>editorialPriority</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>editorialPriority</code> attribute is defined.
	 */
    public boolean isEditorialPriorityDefined()
	{
	    return isDefined(editorialPriorityDef);
	}
 
    /**
     * Returns the value of the <code>lastEditor</code> attribute.
     *
     * @return the value of the <code>lastEditor</code> attribute.
     */
    public Subject getLastEditor()
    {
        return (Subject)get(lastEditorDef);
    }
    
    /**
     * Returns the value of the <code>lastEditor</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>lastEditor</code> attribute.
     */
    public Subject getLastEditor(Subject defaultValue)
    {
        if(isDefined(lastEditorDef))
        {
            return (Subject)get(lastEditorDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>lastEditor</code> attribute.
     *
     * @param value the value of the <code>lastEditor</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLastEditor(Subject value)
    {
        try
        {
            if(value != null)
            {
                set(lastEditorDef, value);
            }
            else
            {
                unset(lastEditorDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>lastEditor</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>lastEditor</code> attribute is defined.
	 */
    public boolean isLastEditorDefined()
	{
	    return isDefined(lastEditorDef);
	}
 
    /**
     * Returns the value of the <code>lastRedactor</code> attribute.
     *
     * @return the value of the <code>lastRedactor</code> attribute.
     */
    public Subject getLastRedactor()
    {
        return (Subject)get(lastRedactorDef);
    }
    
    /**
     * Returns the value of the <code>lastRedactor</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>lastRedactor</code> attribute.
     */
    public Subject getLastRedactor(Subject defaultValue)
    {
        if(isDefined(lastRedactorDef))
        {
            return (Subject)get(lastRedactorDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>lastRedactor</code> attribute.
     *
     * @param value the value of the <code>lastRedactor</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLastRedactor(Subject value)
    {
        try
        {
            if(value != null)
            {
                set(lastRedactorDef, value);
            }
            else
            {
                unset(lastRedactorDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>lastRedactor</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>lastRedactor</code> attribute is defined.
	 */
    public boolean isLastRedactorDefined()
	{
	    return isDefined(lastRedactorDef);
	}
 
    /**
     * Returns the value of the <code>localVisitor</code> attribute.
     *
     * @return the value of the <code>localVisitor</code> attribute.
     */
    public Role getLocalVisitor()
    {
        return (Role)get(localVisitorDef);
    }
    
    /**
     * Returns the value of the <code>localVisitor</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>localVisitor</code> attribute.
     */
    public Role getLocalVisitor(Role defaultValue)
    {
        if(isDefined(localVisitorDef))
        {
            return (Role)get(localVisitorDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>localVisitor</code> attribute.
     *
     * @param value the value of the <code>localVisitor</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLocalVisitor(Role value)
    {
        try
        {
            if(value != null)
            {
                set(localVisitorDef, value);
            }
            else
            {
                unset(localVisitorDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>localVisitor</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>localVisitor</code> attribute is defined.
	 */
    public boolean isLocalVisitorDefined()
	{
	    return isDefined(localVisitorDef);
	}
 
    /**
     * Returns the value of the <code>lockedBy</code> attribute.
     *
     * @return the value of the <code>lockedBy</code> attribute.
     */
    public Subject getLockedBy()
    {
        return (Subject)get(lockedByDef);
    }
    
    /**
     * Returns the value of the <code>lockedBy</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>lockedBy</code> attribute.
     */
    public Subject getLockedBy(Subject defaultValue)
    {
        if(isDefined(lockedByDef))
        {
            return (Subject)get(lockedByDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>lockedBy</code> attribute.
     *
     * @param value the value of the <code>lockedBy</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLockedBy(Subject value)
    {
        try
        {
            if(value != null)
            {
                set(lockedByDef, value);
            }
            else
            {
                unset(lockedByDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>lockedBy</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>lockedBy</code> attribute is defined.
	 */
    public boolean isLockedByDefined()
	{
	    return isDefined(lockedByDef);
	}

    /**
     * Returns the value of the <code>priority</code> attribute.
     *
     * @return the value of the <code>priority</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getPriority()
        throws IllegalStateException
    {
        if(isDefined(priorityDef))
        {
            return ((Integer)get(priorityDef)).intValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute priority is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>priority</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>priority</code> attribute.
     */
    public int getPriority(int defaultValue)
    {
        if(isDefined(priorityDef))
        {
            return ((Integer)get(priorityDef)).intValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>priority</code> attribute.
     *
     * @param value the value of the <code>priority</code> attribute.
     */
    public void setPriority(int value)
    {
        try
        {
            set(priorityDef, new Integer(value));
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
	
	/**
     * Removes the value of the <code>priority</code> attribute.
     */
    public void unsetPriority()
    {
        try
        {
            unset(priorityDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>priority</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>priority</code> attribute is defined.
	 */
    public boolean isPriorityDefined()
	{
	    return isDefined(priorityDef);
	}
 
    /**
     * Returns the value of the <code>redactor</code> attribute.
     *
     * @return the value of the <code>redactor</code> attribute.
     */
    public Role getRedactor()
    {
        return (Role)get(redactorDef);
    }
    
    /**
     * Returns the value of the <code>redactor</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>redactor</code> attribute.
     */
    public Role getRedactor(Role defaultValue)
    {
        if(isDefined(redactorDef))
        {
            return (Role)get(redactorDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>redactor</code> attribute.
     *
     * @param value the value of the <code>redactor</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRedactor(Role value)
    {
        try
        {
            if(value != null)
            {
                set(redactorDef, value);
            }
            else
            {
                unset(redactorDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>redactor</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>redactor</code> attribute is defined.
	 */
    public boolean isRedactorDefined()
	{
	    return isDefined(redactorDef);
	}
 
    /**
     * Returns the value of the <code>reporter</code> attribute.
     *
     * @return the value of the <code>reporter</code> attribute.
     */
    public Role getReporter()
    {
        return (Role)get(reporterDef);
    }
    
    /**
     * Returns the value of the <code>reporter</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>reporter</code> attribute.
     */
    public Role getReporter(Role defaultValue)
    {
        if(isDefined(reporterDef))
        {
            return (Role)get(reporterDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>reporter</code> attribute.
     *
     * @param value the value of the <code>reporter</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setReporter(Role value)
    {
        try
        {
            if(value != null)
            {
                set(reporterDef, value);
            }
            else
            {
                unset(reporterDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>reporter</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>reporter</code> attribute is defined.
	 */
    public boolean isReporterDefined()
	{
	    return isDefined(reporterDef);
	}

    /**
     * Returns the value of the <code>sequence</code> attribute.
     *
     * @return the value of the <code>sequence</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getSequence()
        throws IllegalStateException
    {
        if(isDefined(sequenceDef))
        {
            return ((Integer)get(sequenceDef)).intValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute sequence is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>sequence</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>sequence</code> attribute.
     */
    public int getSequence(int defaultValue)
    {
        if(isDefined(sequenceDef))
        {
            return ((Integer)get(sequenceDef)).intValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>sequence</code> attribute.
     *
     * @param value the value of the <code>sequence</code> attribute.
     */
    public void setSequence(int value)
    {
        try
        {
            set(sequenceDef, new Integer(value));
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
	
	/**
     * Removes the value of the <code>sequence</code> attribute.
     */
    public void unsetSequence()
    {
        try
        {
            unset(sequenceDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>sequence</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>sequence</code> attribute is defined.
	 */
    public boolean isSequenceDefined()
	{
	    return isDefined(sequenceDef);
	}
 
    /**
     * Returns the value of the <code>state</code> attribute.
     *
     * @return the value of the <code>state</code> attribute.
     */
    public StateResource getState()
    {
        return (StateResource)get(stateDef);
    }
    
    /**
     * Returns the value of the <code>state</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>state</code> attribute.
     */
    public StateResource getState(StateResource defaultValue)
    {
        if(isDefined(stateDef))
        {
            return (StateResource)get(stateDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>state</code> attribute.
     *
     * @param value the value of the <code>state</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setState(StateResource value)
    {
        try
        {
            if(value != null)
            {
                set(stateDef, value);
            }
            else
            {
                unset(stateDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>state</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>state</code> attribute is defined.
	 */
    public boolean isStateDefined()
	{
	    return isDefined(stateDef);
	}
 
    /**
     * Returns the value of the <code>style</code> attribute.
     *
     * @return the value of the <code>style</code> attribute.
     */
    public StyleResource getStyle()
    {
        return (StyleResource)get(styleDef);
    }
    
    /**
     * Returns the value of the <code>style</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>style</code> attribute.
     */
    public StyleResource getStyle(StyleResource defaultValue)
    {
        if(isDefined(styleDef))
        {
            return (StyleResource)get(styleDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>style</code> attribute.
     *
     * @param value the value of the <code>style</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setStyle(StyleResource value)
    {
        try
        {
            if(value != null)
            {
                set(styleDef, value);
            }
            else
            {
                unset(styleDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>style</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>style</code> attribute is defined.
	 */
    public boolean isStyleDefined()
	{
	    return isDefined(styleDef);
	}
 
    /**
     * Returns the value of the <code>thumbnail</code> attribute.
     *
     * @return the value of the <code>thumbnail</code> attribute.
     */
    public FileResource getThumbnail()
    {
        return (FileResource)get(thumbnailDef);
    }
    
    /**
     * Returns the value of the <code>thumbnail</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>thumbnail</code> attribute.
     */
    public FileResource getThumbnail(FileResource defaultValue)
    {
        if(isDefined(thumbnailDef))
        {
            return (FileResource)get(thumbnailDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>thumbnail</code> attribute.
     *
     * @param value the value of the <code>thumbnail</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setThumbnail(FileResource value)
    {
        try
        {
            if(value != null)
            {
                set(thumbnailDef, value);
            }
            else
            {
                unset(thumbnailDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>thumbnail</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>thumbnail</code> attribute is defined.
	 */
    public boolean isThumbnailDefined()
	{
	    return isDefined(thumbnailDef);
	}
 
    /**
     * Returns the value of the <code>validityEnd</code> attribute.
     *
     * @return the value of the <code>validityEnd</code> attribute.
     */
    public Date getValidityEnd()
    {
        return (Date)get(validityEndDef);
    }
    
    /**
     * Returns the value of the <code>validityEnd</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>validityEnd</code> attribute.
     */
    public Date getValidityEnd(Date defaultValue)
    {
        if(isDefined(validityEndDef))
        {
            return (Date)get(validityEndDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>validityEnd</code> attribute.
     *
     * @param value the value of the <code>validityEnd</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setValidityEnd(Date value)
    {
        try
        {
            if(value != null)
            {
                set(validityEndDef, value);
            }
            else
            {
                unset(validityEndDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>validityEnd</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>validityEnd</code> attribute is defined.
	 */
    public boolean isValidityEndDefined()
	{
	    return isDefined(validityEndDef);
	}
 
    /**
     * Returns the value of the <code>validityStart</code> attribute.
     *
     * @return the value of the <code>validityStart</code> attribute.
     */
    public Date getValidityStart()
    {
        return (Date)get(validityStartDef);
    }
    
    /**
     * Returns the value of the <code>validityStart</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>validityStart</code> attribute.
     */
    public Date getValidityStart(Date defaultValue)
    {
        if(isDefined(validityStartDef))
        {
            return (Date)get(validityStartDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>validityStart</code> attribute.
     *
     * @param value the value of the <code>validityStart</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setValidityStart(Date value)
    {
        try
        {
            if(value != null)
            {
                set(validityStartDef, value);
            }
            else
            {
                unset(validityStartDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>validityStart</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>validityStart</code> attribute is defined.
	 */
    public boolean isValidityStartDefined()
	{
	    return isDefined(validityStartDef);
	}
 
    /**
     * Returns the value of the <code>visitor</code> attribute.
     *
     * @return the value of the <code>visitor</code> attribute.
     */
    public Role getVisitor()
    {
        return (Role)get(visitorDef);
    }
    
    /**
     * Returns the value of the <code>visitor</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>visitor</code> attribute.
     */
    public Role getVisitor(Role defaultValue)
    {
        if(isDefined(visitorDef))
        {
            return (Role)get(visitorDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>visitor</code> attribute.
     *
     * @param value the value of the <code>visitor</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setVisitor(Role value)
    {
        try
        {
            if(value != null)
            {
                set(visitorDef, value);
            }
            else
            {
                unset(visitorDef);
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
   
	/**
	 * Checks if the value of the <code>visitor</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>visitor</code> attribute is defined.
	 */
    public boolean isVisitorDefined()
	{
	    return isDefined(visitorDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
    // @extends node
    // @import java.util.List
    // @import java.util.ArrayList
    // @import java.util.Iterator
    // @import java.util.StringTokenizer
    // @import java.util.Date
    // @import org.objectledge.context.Context
    // @import org.objectledge.coral.security.Permission
    // @import org.objectledge.coral.security.Subject
    // @import org.objectledge.coral.session.CoralSession
    // @import net.cyklotron.cms.style.StyleResource
    // @import net.cyklotron.cms.CmsData

    // @order title, site, preferences

    /**
     * Returns the path relative to site's structure root node, ie. including site's home page.
     *
     * @return path relative to site's structure node.
     */
    public String getSitePath()
    {
        List parentNodes = getParentNavigationNodes(true);
        Resource structure;
        if(parentNodes.size() == 0)
        {
            structure = this.getParent();
        }
        else
        {
            structure = ((Resource)(parentNodes.get(0))).getParent();
        }
        String fullPath = getPath();
        String relativePath = fullPath.substring(structure.getPath().length());
        return relativePath;
    }

    /**
     * Returns the effective value of the <code>style</code> attribute.
     * If local style is not set, value is inherited from ancestor.
     *
     * @return the effective value of the <code>style</code> attribute.
     */
    public StyleResource getEffectiveStyle()
    {
        StyleResource style = null;
        Resource node = this;
        while(style == null && node != null && node instanceof NavigationNodeResource)
        {
            style = ((NavigationNodeResource)node).getStyle();
            node = node.getParent();
        }

        if(style == null)
        {
            //TODO: What about this case?
            throw new RuntimeException("No style defined for the site");
        }
        return style;
    }

    public int getLevel()
    {
        int level = 0;
        Resource parent = this.getParent();
        while(parent != null && parent instanceof NavigationNodeResource)
        {
            level++;
            parent = parent.getParent();
        }
        return level;
    }

    public List getParentNavigationNodes(boolean includeRoot)
    {
        ArrayList result = new ArrayList();
        Resource parent = this.getParent();
        while(parent != null && parent instanceof NavigationNodeResource)
        {
            result.add(parent);
            parent = parent.getParent();
        }
        if(!includeRoot)
        {
            result.remove(result.size()-1);
        }
        java.util.Collections.reverse(result);
        return result;
    }
    
    /**
     * Checks if this resource can be viewed at the given time.
     */
    public boolean isValid(Context context, Date time)
    {
        Date start = this.getValidityStart();
        Date end = this.getValidityEnd();
        if(start == null)
        {
            if(end == null)
            {
                return true;
            }
            else
            {
                return time.before(end);
            }
        }
        else
        {
            if(end == null)
            {
                return time.after(start);
            }
            else
            {
                if(start.after(end))
                {
                    return (time.before(start) || time.after(end));
                }
                else
                {
                    return (time.after(start) && time.before(end));
                }
            }
        }
    }
    
    /** <code>cms.structure.view</code> */
    private Permission viewPermission;
    /** <code>cms.structure.modify</code> */
    private Permission modifyPermission;
    /** <code>cms.structure.modify_own</code> */
    private Permission modifyOwnPermission;
    /** <code>cms.structure.add</code> */
    private Permission addPermission;
    /** <code>cms.structure.delete</code> */
    private Permission deletePermission;
    /*
    <code>cms.structure.move</code>
    <code>cms.structure.moderate</code>
    */
    
    /**
     * Checks if a given subject can view this resource.
     */
    public boolean canView(Context context, Subject subject)
    {
        if(viewPermission == null)
        {
            viewPermission = getCoralSession(context).getSecurity().getUniquePermission("cms.structure.view");
        }
        return subject.hasPermission(this, viewPermission);
    }
    
    /**
     * Checks if the specified subject can view this resource at the given time.
     */
    public boolean canView(Context context, Subject subject, Date time)
    {
        if(!canView(context, subject))
        {
            return false;
        }
        StateResource state = this.getState();
        if(state == null)
        {
            return isValid(context, time);
        }
        return (state.getName().equals("published") && isValid(context, time));
    }

    /**
     * Checks if the specified subject can view this resource
     */
    public boolean canView(Context context, CmsData data, Subject subject)
    {
        if(data.getBrowseMode().equals(CmsConstants.BROWSE_MODE_ADMINISTER))
        {
            return canView(context, subject);
        }
        if(!canView(context, subject))
        {
            return false;
        }
        StateResource state = this.getState();
        if(state == null)
        {
            return isValid(context, data.getDate());
        }
        if(data.getBrowseMode().equals("time_travel"))
        {
            if((state.getName().equals("published") ||
                state.getName().equals("expired") ||
                state.getName().equals("accepted")) && isValid(context, data.getDate()))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        if(data.getBrowseMode().equals("preview"))
        {
            return true;
        }            
        return state.getName().equals("published");
    }
    
    /**
     * Checks if the specified subject can modify this resource.
     */
    public boolean canModify(Context context, Subject subject)
    {
        if(modifyPermission == null)
        {
            modifyPermission = getCoralSession(context).getSecurity().getUniquePermission("cms.structure.modify");
        }
        if(modifyOwnPermission == null)
        {
            modifyOwnPermission = getCoralSession(context).getSecurity().getUniquePermission("cms.structure.modify_own");
        }
        
        return subject.hasPermission(this, modifyPermission) ||
            (this.getOwner().equals(subject) && subject.hasPermission(this, modifyOwnPermission));
    }

    /**
     * Checks if the specified subject can remove this resource.
     */
    public boolean canRemove(Context context, Subject subject)
    {
        if(deletePermission == null)
        {
            deletePermission = getCoralSession(context).getSecurity().getUniquePermission("cms.structure.delete");
        }
        return subject.hasPermission(this, deletePermission);
    }
    
    /**
     * Checks if the specified subject can add children to this resource.
     */
    public boolean canAddChild(Context context, Subject subject)
    {
        if(addPermission == null)
        {
            addPermission = getCoralSession(context).getSecurity().getUniquePermission("cms.structure.add");
        }
        return subject.hasPermission(this, addPermission);
    }

	/**
	 * Returns the store flag of the field.
	 *
	 * @return the store flag.
	 */
	public boolean isStored(String fieldName)
	{
		return false;
	}
	
	/**
	 * Returns the indexed flag of the field.
	 *
	 * @return the indexed flag.
	 */
	public boolean isIndexed(String fieldName)
	{
		return false;
	}
		
	/**
	 * Returns the tokenized flag of the field.
	 *
	 * @return the tokenized flag.
	 */
	public boolean isTokenized(String fieldName)
	{
		return false;
	}
}
