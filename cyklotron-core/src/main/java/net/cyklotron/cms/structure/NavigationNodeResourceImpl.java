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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.objectledge.coral.BackendException;
import org.objectledge.coral.datatypes.NodeImpl;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.CoralSchema;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.database.Database;
import org.objectledge.parameters.Parameters;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.style.StyleResource;
import net.cyklotron.cms.workflow.StateResource;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Subject;
import org.jcontainer.dna.Logger;

/**
 * An implementation of <code>structure.navigation_node</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class NavigationNodeResourceImpl
    extends NodeImpl
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

    /** The AttributeDefinition object for the <code>editorial_priority</code> attribute. */
    private AttributeDefinition editorial_priorityDef;

    /** The AttributeDefinition object for the <code>last_editor</code> attribute. */
    private AttributeDefinition last_editorDef;

    /** The AttributeDefinition object for the <code>last_redactor</code> attribute. */
    private AttributeDefinition last_redactorDef;

    /** The AttributeDefinition object for the <code>local_visitor</code> attribute. */
    private AttributeDefinition local_visitorDef;

    /** The AttributeDefinition object for the <code>locked_by</code> attribute. */
    private AttributeDefinition locked_byDef;

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

    /** The AttributeDefinition object for the <code>validity_end</code> attribute. */
    private AttributeDefinition validity_endDef;

    /** The AttributeDefinition object for the <code>validity_start</code> attribute. */
    private AttributeDefinition validity_startDef;

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
            editorial_priorityDef = rc.getAttribute("editorial_priority");
            last_editorDef = rc.getAttribute("last_editor");
            last_redactorDef = rc.getAttribute("last_redactor");
            local_visitorDef = rc.getAttribute("local_visitor");
            locked_byDef = rc.getAttribute("locked_by");
            priorityDef = rc.getAttribute("priority");
            redactorDef = rc.getAttribute("redactor");
            reporterDef = rc.getAttribute("reporter");
            sequenceDef = rc.getAttribute("sequence");
            stateDef = rc.getAttribute("state");
            styleDef = rc.getAttribute("style");
            thumbnailDef = rc.getAttribute("thumbnail");
            validity_endDef = rc.getAttribute("validity_end");
            validity_startDef = rc.getAttribute("validity_start");
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
     * Returns the value of the <code>editorial_priority</code> attribute.
     *
     * @return the value of the <code>editorial_priority</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public int getEditorial_priority()
        throws IllegalStateException
    {
        if(isDefined(editorial_priorityDef))
        {
            return ((Integer)get(editorial_priorityDef)).intValue();
        }
        else
        {
            throw new IllegalStateException("attribute value is undefined");
        }
    }

    /**
     * Returns the value of the <code>editorial_priority</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>editorial_priority</code> attribute.
     */
    public int getEditorial_priority(int defaultValue)
    {
        if(isDefined(editorial_priorityDef))
        {
            return ((Integer)get(editorial_priorityDef)).intValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>editorial_priority</code> attribute.
     *
     * @param value the value of the <code>editorial_priority</code> attribute.
     */
    public void setEditorial_priority(int value)
    {
        try
        {
            set(editorial_priorityDef, new Integer(value));
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
     * Removes the value of the <code>editorial_priority</code> attribute.
     */
    public void unsetEditorial_priority()
    {
        try
        {
            unset(editorial_priorityDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>editorial_priority</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>editorial_priority</code> attribute is defined.
	 */
    public boolean isEditorial_priorityDefined()
	{
	    return isDefined(editorial_priorityDef);
	}
 
    /**
     * Returns the value of the <code>last_editor</code> attribute.
     *
     * @return the value of the <code>last_editor</code> attribute.
     */
    public Subject getLast_editor()
    {
        return (Subject)get(last_editorDef);
    }
    
    /**
     * Returns the value of the <code>last_editor</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>last_editor</code> attribute.
     */
    public Subject getLast_editor(Subject defaultValue)
    {
        if(isDefined(last_editorDef))
        {
            return (Subject)get(last_editorDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>last_editor</code> attribute.
     *
     * @param value the value of the <code>last_editor</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLast_editor(Subject value)
    {
        try
        {
            if(value != null)
            {
                set(last_editorDef, value);
            }
            else
            {
                unset(last_editorDef);
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
	 * Checks if the value of the <code>last_editor</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>last_editor</code> attribute is defined.
	 */
    public boolean isLast_editorDefined()
	{
	    return isDefined(last_editorDef);
	}
 
    /**
     * Returns the value of the <code>last_redactor</code> attribute.
     *
     * @return the value of the <code>last_redactor</code> attribute.
     */
    public Subject getLast_redactor()
    {
        return (Subject)get(last_redactorDef);
    }
    
    /**
     * Returns the value of the <code>last_redactor</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>last_redactor</code> attribute.
     */
    public Subject getLast_redactor(Subject defaultValue)
    {
        if(isDefined(last_redactorDef))
        {
            return (Subject)get(last_redactorDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>last_redactor</code> attribute.
     *
     * @param value the value of the <code>last_redactor</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLast_redactor(Subject value)
    {
        try
        {
            if(value != null)
            {
                set(last_redactorDef, value);
            }
            else
            {
                unset(last_redactorDef);
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
	 * Checks if the value of the <code>last_redactor</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>last_redactor</code> attribute is defined.
	 */
    public boolean isLast_redactorDefined()
	{
	    return isDefined(last_redactorDef);
	}
 
    /**
     * Returns the value of the <code>local_visitor</code> attribute.
     *
     * @return the value of the <code>local_visitor</code> attribute.
     */
    public Role getLocal_visitor()
    {
        return (Role)get(local_visitorDef);
    }
    
    /**
     * Returns the value of the <code>local_visitor</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>local_visitor</code> attribute.
     */
    public Role getLocal_visitor(Role defaultValue)
    {
        if(isDefined(local_visitorDef))
        {
            return (Role)get(local_visitorDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>local_visitor</code> attribute.
     *
     * @param value the value of the <code>local_visitor</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLocal_visitor(Role value)
    {
        try
        {
            if(value != null)
            {
                set(local_visitorDef, value);
            }
            else
            {
                unset(local_visitorDef);
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
	 * Checks if the value of the <code>local_visitor</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>local_visitor</code> attribute is defined.
	 */
    public boolean isLocal_visitorDefined()
	{
	    return isDefined(local_visitorDef);
	}
 
    /**
     * Returns the value of the <code>locked_by</code> attribute.
     *
     * @return the value of the <code>locked_by</code> attribute.
     */
    public Subject getLocked_by()
    {
        return (Subject)get(locked_byDef);
    }
    
    /**
     * Returns the value of the <code>locked_by</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>locked_by</code> attribute.
     */
    public Subject getLocked_by(Subject defaultValue)
    {
        if(isDefined(locked_byDef))
        {
            return (Subject)get(locked_byDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>locked_by</code> attribute.
     *
     * @param value the value of the <code>locked_by</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLocked_by(Subject value)
    {
        try
        {
            if(value != null)
            {
                set(locked_byDef, value);
            }
            else
            {
                unset(locked_byDef);
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
	 * Checks if the value of the <code>locked_by</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>locked_by</code> attribute is defined.
	 */
    public boolean isLocked_byDefined()
	{
	    return isDefined(locked_byDef);
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
            throw new IllegalStateException("attribute value is undefined");
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
            throw new IllegalStateException("attribute value is undefined");
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
     * Returns the value of the <code>validity_end</code> attribute.
     *
     * @return the value of the <code>validity_end</code> attribute.
     */
    public Date getValidity_end()
    {
        return (Date)get(validity_endDef);
    }
    
    /**
     * Returns the value of the <code>validity_end</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>validity_end</code> attribute.
     */
    public Date getValidity_end(Date defaultValue)
    {
        if(isDefined(validity_endDef))
        {
            return (Date)get(validity_endDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>validity_end</code> attribute.
     *
     * @param value the value of the <code>validity_end</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setValidity_end(Date value)
    {
        try
        {
            if(value != null)
            {
                set(validity_endDef, value);
            }
            else
            {
                unset(validity_endDef);
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
	 * Checks if the value of the <code>validity_end</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>validity_end</code> attribute is defined.
	 */
    public boolean isValidity_endDefined()
	{
	    return isDefined(validity_endDef);
	}
 
    /**
     * Returns the value of the <code>validity_start</code> attribute.
     *
     * @return the value of the <code>validity_start</code> attribute.
     */
    public Date getValidity_start()
    {
        return (Date)get(validity_startDef);
    }
    
    /**
     * Returns the value of the <code>validity_start</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>validity_start</code> attribute.
     */
    public Date getValidity_start(Date defaultValue)
    {
        if(isDefined(validity_startDef))
        {
            return (Date)get(validity_startDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>validity_start</code> attribute.
     *
     * @param value the value of the <code>validity_start</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setValidity_start(Date value)
    {
        try
        {
            if(value != null)
            {
                set(validity_startDef, value);
            }
            else
            {
                unset(validity_startDef);
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
	 * Checks if the value of the <code>validity_start</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>validity_start</code> attribute is defined.
	 */
    public boolean isValidity_startDefined()
	{
	    return isDefined(validity_startDef);
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
    // @extends coral.Node
    // @import java.util.List
    // @import java.util.ArrayList
    // @import java.util.Iterator
    // @import java.util.StringTokenizer
    // @import net.labeo.services.resource.Subject
    // @import net.labeo.services.resource.Permission
    // @import net.cyklotron.cms.style.StyleResource
    // @import net.cyklotron.cms.CmsData

    // @order title, site, preferences

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
    public boolean isValid(Date time)
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
    public boolean canView(Subject subject)
    {
        if(viewPermission == null)
        {
            viewPermission = rs.getSecurity().getUniquePermission("cms.structure.view");
        }
        return subject.hasPermission(this, viewPermission);
    }
    
    /**
     * Checks if the specified subject can view this resource at the given time.
     */
    public boolean canView(Subject subject, Date time)
    {
        if(!canView(subject))
        {
            return false;
        }
        StateResource state = this.getState();
        if(state == null)
        {
            return isValid(time);
        }
        return (state.getName().equals("published") && isValid(time));
    }

    /**
     * Checks if the specified subject can view this resource
     */
    public boolean canView(CmsData data, Subject subject)
    {
        if(data.getBrowseMode().equals(CmsData.BROWSE_MODE_ADMINISTER))
        {
            return canView(subject);
        }
        if(!canView(subject))
        {
            return false;
        }
        StateResource state = this.getState();
        if(state == null)
        {
            return isValid(data.getDate());
        }
        if(data.getBrowseMode().equals("time_travel"))
        {
            if((state.getName().equals("published") ||
                state.getName().equals("expired") ||
                state.getName().equals("accepted")) && isValid(data.getDate()))
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
    public boolean canModify(Subject subject)
    {
        if(modifyPermission == null)
        {
            modifyPermission = rs.getSecurity().getUniquePermission("cms.structure.modify");
        }
        if(modifyOwnPermission == null)
        {
            modifyOwnPermission = rs.getSecurity().getUniquePermission("cms.structure.modify_own");
        }
        
        return subject.hasPermission(this, modifyPermission) ||
            (this.getOwner().equals(subject) && subject.hasPermission(this, modifyOwnPermission));
    }

    /**
     * Checks if the specified subject can remove this resource.
     */
    public boolean canRemove(Subject subject)
    {
        if(deletePermission == null)
        {
            deletePermission = rs.getSecurity().getUniquePermission("cms.structure.delete");
        }
        return subject.hasPermission(this, deletePermission);
    }
    
    /**
     * Checks if the specified subject can add children to this resource.
     */
    public boolean canAddChild(Subject subject)
    {
        if(addPermission == null)
        {
            addPermission = rs.getSecurity().getUniquePermission("cms.structure.add");
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
