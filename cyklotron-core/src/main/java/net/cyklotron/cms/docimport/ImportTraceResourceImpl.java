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
 
package net.cyklotron.cms.docimport;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.objectledge.coral.BackendException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

import net.cyklotron.cms.CmsNodeResourceImpl;

/**
 * An implementation of <code>docimport.trace</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class ImportTraceResourceImpl
    extends CmsNodeResourceImpl
    implements ImportTraceResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>originalURL</code> attribute. */
    private static AttributeDefinition originalURLDef;

    /** The AttributeDefinition object for the <code>sourceModificationTime</code> attribute. */
    private static AttributeDefinition sourceModificationTimeDef;

    /** The AttributeDefinition object for the <code>targetUpdateTime</code> attribute. */
    private static AttributeDefinition targetUpdateTimeDef;

    /** The AttributeDefinition object for the <code>navigationNode</code> attribute. */
    private static AttributeDefinition navigationNodeDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>docimport.trace</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public ImportTraceResourceImpl()
    {
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>docimport.trace</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static ImportTraceResource getImportTraceResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof ImportTraceResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not docimport.trace");
        }
        return (ImportTraceResource)res;
    }

    /**
     * Creates a new <code>docimport.trace</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param originalURL the originalURL attribute
     * @param sourceModificationTime the sourceModificationTime attribute
     * @param targetUpdateTime the targetUpdateTime attribute
     * @param navigationNode the navigationNode attribute
     * @return a new ImportTraceResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static ImportTraceResource createImportTraceResource(CoralSession session, String
        name, Resource parent, String originalURL, Date sourceModificationTime, Date
        targetUpdateTime, Resource navigationNode)
        throws ValueRequiredException, InvalidResourceNameException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("docimport.trace");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("originalURL"), originalURL);
            attrs.put(rc.getAttribute("sourceModificationTime"), sourceModificationTime);
            attrs.put(rc.getAttribute("targetUpdateTime"), targetUpdateTime);
            attrs.put(rc.getAttribute("navigationNode"), navigationNode);
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof ImportTraceResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (ImportTraceResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>originalURL</code> attribute.
     *
     * @return the value of the <code>originalURL</code> attribute.
     */
    public String getOriginalURL()
    {
        return (String)getInternal(originalURLDef, null);
    }
 
    /**
     * Sets the value of the <code>originalURL</code> attribute.
     *
     * @param value the value of the <code>originalURL</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setOriginalURL(String value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(originalURLDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute originalURL "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>sourceModificationTime</code> attribute.
     *
     * @return the value of the <code>sourceModificationTime</code> attribute.
     */
    public Date getSourceModificationTime()
    {
        return (Date)getInternal(sourceModificationTimeDef, null);
    }
 
    /**
     * Sets the value of the <code>sourceModificationTime</code> attribute.
     *
     * @param value the value of the <code>sourceModificationTime</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setSourceModificationTime(Date value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(sourceModificationTimeDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute sourceModificationTime "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>targetUpdateTime</code> attribute.
     *
     * @return the value of the <code>targetUpdateTime</code> attribute.
     */
    public Date getTargetUpdateTime()
    {
        return (Date)getInternal(targetUpdateTimeDef, null);
    }
 
    /**
     * Sets the value of the <code>targetUpdateTime</code> attribute.
     *
     * @param value the value of the <code>targetUpdateTime</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setTargetUpdateTime(Date value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(targetUpdateTimeDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute targetUpdateTime "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>navigationNode</code> attribute.
     *
     * @return the value of the <code>navigationNode</code> attribute.
     */
    public Resource getNavigationNode()
    {
        return (Resource)getInternal(navigationNodeDef, null);
    }
 
    /**
     * Sets the value of the <code>navigationNode</code> attribute.
     *
     * @param value the value of the <code>navigationNode</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setNavigationNode(Resource value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(navigationNodeDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute navigationNode "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
     
    // @custom methods ///////////////////////////////////////////////////////

    // @order originalURL, sourceModificationTime, targetUpdateTime, navigationNode
}
