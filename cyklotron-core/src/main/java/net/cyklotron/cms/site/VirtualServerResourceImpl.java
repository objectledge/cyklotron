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

import org.jcontainer.dna.Logger;
import org.objectledge.coral.BackendException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.CoralSchema;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.database.Database;

import net.cyklotron.cms.CmsNodeResourceImpl;
import net.cyklotron.cms.structure.NavigationNodeResource;

/**
 * An implementation of <code>site.virtual_server</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class VirtualServerResourceImpl
    extends CmsNodeResourceImpl
    implements VirtualServerResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>site</code> attribute. */
    private AttributeDefinition siteDef;

    /** The AttributeDefinition object for the <code>node</code> attribute. */
    private AttributeDefinition nodeDef;

    /** The AttributeDefinition object for the <code>primary</code> attribute. */
    private AttributeDefinition primaryDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>site.virtual_server</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public VirtualServerResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("site.virtual_server");
            siteDef = rc.getAttribute("site");
            nodeDef = rc.getAttribute("node");
            primaryDef = rc.getAttribute("primary");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>site.virtual_server</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static VirtualServerResource getVirtualServerResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof VirtualServerResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not site.virtual_server");
        }
        return (VirtualServerResource)res;
    }

    /**
     * Creates a new <code>site.virtual_server</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param site the site attribute
     * @param node the node attribute
     * @param primary the primary attribute
     * @return a new VirtualServerResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     */
    public static VirtualServerResource createVirtualServerResource(CoralSession session, String
        name, Resource parent, SiteResource site, NavigationNodeResource node, boolean primary)
        throws ValueRequiredException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("site.virtual_server");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("site"), site);
            attrs.put(rc.getAttribute("node"), node);
            attrs.put(rc.getAttribute("primary"), new Boolean(primary));
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof VirtualServerResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (VirtualServerResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
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
     * Returns the value of the <code>node</code> attribute.
     *
     * @return the value of the <code>node</code> attribute.
     */
    public NavigationNodeResource getNode()
    {
        return (NavigationNodeResource)get(nodeDef);
    }
 
    /**
     * Sets the value of the <code>node</code> attribute.
     *
     * @param value the value of the <code>node</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setNode(NavigationNodeResource value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(nodeDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute node "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>primary</code> attribute.
     *
     * @return the value of the <code>primary</code> attribute.
     */
    public boolean getPrimary()
    {
        if(isDefined(primaryDef))
        {
            return ((Boolean)get(primaryDef)).booleanValue();
        }
        else
        {
            throw new BackendException("incompatible schema change");
        }
    }    

    /**
     * Sets the value of the <code>primary</code> attribute.
     *
     * @param value the value of the <code>primary</code> attribute.
     */
    public void setPrimary(boolean value)
    {
        try
        {
            set(primaryDef, new Boolean(value));
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

    // @order site, node
}
