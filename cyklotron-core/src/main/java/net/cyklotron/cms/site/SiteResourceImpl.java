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
 
package net.cyklotron.cms.site;

import java.util.HashMap;
import java.util.Map;

import org.objectledge.coral.BackendException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.CoralSchema;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.database.Database;

import net.cyklotron.cms.CmsNodeResourceImpl;
import org.jcontainer.dna.Logger;

/**
 * An implementation of <code>site.site</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class SiteResourceImpl
    extends CmsNodeResourceImpl
    implements SiteResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>administrator</code> attribute. */
    private AttributeDefinition administratorDef;

    /** The AttributeDefinition object for the <code>layoutAdministrator</code> attribute. */
    private AttributeDefinition layoutAdministratorDef;

    /** The AttributeDefinition object for the <code>siteRole</code> attribute. */
    private AttributeDefinition siteRoleDef;

    /** The AttributeDefinition object for the <code>teamMember</code> attribute. */
    private AttributeDefinition teamMemberDef;

    /** The AttributeDefinition object for the <code>template</code> attribute. */
    private AttributeDefinition templateDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>site.site</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public SiteResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("site.site");
            administratorDef = rc.getAttribute("administrator");
            layoutAdministratorDef = rc.getAttribute("layoutAdministrator");
            siteRoleDef = rc.getAttribute("siteRole");
            teamMemberDef = rc.getAttribute("teamMember");
            templateDef = rc.getAttribute("template");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>site.site</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static SiteResource getSiteResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof SiteResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not site.site");
        }
        return (SiteResource)res;
    }

    /**
     * Creates a new <code>site.site</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param template the template attribute
     * @return a new SiteResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static SiteResource createSiteResource(CoralSession session, String name, Resource
        parent, boolean template)
        throws ValueRequiredException, InvalidResourceNameException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("site.site");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("template"), new Boolean(template));
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof SiteResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (SiteResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
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
     * Returns the value of the <code>layoutAdministrator</code> attribute.
     *
     * @return the value of the <code>layoutAdministrator</code> attribute.
     */
    public Role getLayoutAdministrator()
    {
        return (Role)get(layoutAdministratorDef);
    }
    
    /**
     * Returns the value of the <code>layoutAdministrator</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>layoutAdministrator</code> attribute.
     */
    public Role getLayoutAdministrator(Role defaultValue)
    {
        if(isDefined(layoutAdministratorDef))
        {
            return (Role)get(layoutAdministratorDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>layoutAdministrator</code> attribute.
     *
     * @param value the value of the <code>layoutAdministrator</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLayoutAdministrator(Role value)
    {
        try
        {
            if(value != null)
            {
                set(layoutAdministratorDef, value);
            }
            else
            {
                unset(layoutAdministratorDef);
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
	 * Checks if the value of the <code>layoutAdministrator</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>layoutAdministrator</code> attribute is defined.
	 */
    public boolean isLayoutAdministratorDefined()
	{
	    return isDefined(layoutAdministratorDef);
	}
 
    /**
     * Returns the value of the <code>siteRole</code> attribute.
     *
     * @return the value of the <code>siteRole</code> attribute.
     */
    public Role getSiteRole()
    {
        return (Role)get(siteRoleDef);
    }
    
    /**
     * Returns the value of the <code>siteRole</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>siteRole</code> attribute.
     */
    public Role getSiteRole(Role defaultValue)
    {
        if(isDefined(siteRoleDef))
        {
            return (Role)get(siteRoleDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>siteRole</code> attribute.
     *
     * @param value the value of the <code>siteRole</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setSiteRole(Role value)
    {
        try
        {
            if(value != null)
            {
                set(siteRoleDef, value);
            }
            else
            {
                unset(siteRoleDef);
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
	 * Checks if the value of the <code>siteRole</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>siteRole</code> attribute is defined.
	 */
    public boolean isSiteRoleDefined()
	{
	    return isDefined(siteRoleDef);
	}
 
    /**
     * Returns the value of the <code>teamMember</code> attribute.
     *
     * @return the value of the <code>teamMember</code> attribute.
     */
    public Role getTeamMember()
    {
        return (Role)get(teamMemberDef);
    }
    
    /**
     * Returns the value of the <code>teamMember</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>teamMember</code> attribute.
     */
    public Role getTeamMember(Role defaultValue)
    {
        if(isDefined(teamMemberDef))
        {
            return (Role)get(teamMemberDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>teamMember</code> attribute.
     *
     * @param value the value of the <code>teamMember</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setTeamMember(Role value)
    {
        try
        {
            if(value != null)
            {
                set(teamMemberDef, value);
            }
            else
            {
                unset(teamMemberDef);
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
	 * Checks if the value of the <code>teamMember</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>teamMember</code> attribute is defined.
	 */
    public boolean isTeamMemberDefined()
	{
	    return isDefined(teamMemberDef);
	}
 
    /**
     * Returns the value of the <code>template</code> attribute.
     *
     * @return the value of the <code>template</code> attribute.
     */
    public boolean getTemplate()
    {
        if(isDefined(templateDef))
        {
            return ((Boolean)get(templateDef)).booleanValue();
        }
        else
        {
            throw new BackendException("incompatible schema change");
        }
    }    

    /**
     * Sets the value of the <code>template</code> attribute.
     *
     * @param value the value of the <code>template</code> attribute.
     */
    public void setTemplate(boolean value)
    {
        try
        {
            set(templateDef, new Boolean(value));
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
     
    // @custom methods ///////////////////////////////////////////////////////
}
