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
import org.objectledge.coral.datatypes.NodeImpl;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.CoralSchema;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.database.Database;

import org.jcontainer.dna.Logger;

/**
 * An implementation of <code>site.site</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class SiteResourceImpl
    extends NodeImpl
    implements SiteResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>administrator</code> attribute. */
    private AttributeDefinition administratorDef;

    /** The AttributeDefinition object for the <code>layout_administrator</code> attribute. */
    private AttributeDefinition layout_administratorDef;

    /** The AttributeDefinition object for the <code>site_role</code> attribute. */
    private AttributeDefinition site_roleDef;

    /** The AttributeDefinition object for the <code>team_member</code> attribute. */
    private AttributeDefinition team_memberDef;

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
            layout_administratorDef = rc.getAttribute("layout_administrator");
            site_roleDef = rc.getAttribute("site_role");
            team_memberDef = rc.getAttribute("team_member");
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
     */
    public static SiteResource createSiteResource(CoralSession session, String name, Resource
        parent, boolean template)
        throws ValueRequiredException
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
     * Returns the value of the <code>layout_administrator</code> attribute.
     *
     * @return the value of the <code>layout_administrator</code> attribute.
     */
    public Role getLayout_administrator()
    {
        return (Role)get(layout_administratorDef);
    }
    
    /**
     * Returns the value of the <code>layout_administrator</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>layout_administrator</code> attribute.
     */
    public Role getLayout_administrator(Role defaultValue)
    {
        if(isDefined(layout_administratorDef))
        {
            return (Role)get(layout_administratorDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>layout_administrator</code> attribute.
     *
     * @param value the value of the <code>layout_administrator</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLayout_administrator(Role value)
    {
        try
        {
            if(value != null)
            {
                set(layout_administratorDef, value);
            }
            else
            {
                unset(layout_administratorDef);
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
	 * Checks if the value of the <code>layout_administrator</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>layout_administrator</code> attribute is defined.
	 */
    public boolean isLayout_administratorDefined()
	{
	    return isDefined(layout_administratorDef);
	}
 
    /**
     * Returns the value of the <code>site_role</code> attribute.
     *
     * @return the value of the <code>site_role</code> attribute.
     */
    public Role getSite_role()
    {
        return (Role)get(site_roleDef);
    }
    
    /**
     * Returns the value of the <code>site_role</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>site_role</code> attribute.
     */
    public Role getSite_role(Role defaultValue)
    {
        if(isDefined(site_roleDef))
        {
            return (Role)get(site_roleDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>site_role</code> attribute.
     *
     * @param value the value of the <code>site_role</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setSite_role(Role value)
    {
        try
        {
            if(value != null)
            {
                set(site_roleDef, value);
            }
            else
            {
                unset(site_roleDef);
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
	 * Checks if the value of the <code>site_role</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>site_role</code> attribute is defined.
	 */
    public boolean isSite_roleDefined()
	{
	    return isDefined(site_roleDef);
	}
 
    /**
     * Returns the value of the <code>team_member</code> attribute.
     *
     * @return the value of the <code>team_member</code> attribute.
     */
    public Role getTeam_member()
    {
        return (Role)get(team_memberDef);
    }
    
    /**
     * Returns the value of the <code>team_member</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>team_member</code> attribute.
     */
    public Role getTeam_member(Role defaultValue)
    {
        if(isDefined(team_memberDef))
        {
            return (Role)get(team_memberDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>team_member</code> attribute.
     *
     * @param value the value of the <code>team_member</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setTeam_member(Role value)
    {
        try
        {
            if(value != null)
            {
                set(team_memberDef, value);
            }
            else
            {
                unset(team_memberDef);
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
	 * Checks if the value of the <code>team_member</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>team_member</code> attribute is defined.
	 */
    public boolean isTeam_memberDefined()
	{
	    return isDefined(team_memberDef);
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
