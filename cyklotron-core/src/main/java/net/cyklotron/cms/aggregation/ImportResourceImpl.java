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
 
package net.cyklotron.cms.aggregation;

import java.util.HashMap;
import java.util.Map;

import org.objectledge.coral.BackendException;
import org.objectledge.coral.datatypes.NodeImpl;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.CoralSchema;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.database.Database;

import net.cyklotron.cms.site.SiteResource;
import org.jcontainer.dna.Logger;

/**
 * An implementation of <code>cms.aggregation.import</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class ImportResourceImpl
    extends NodeImpl
    implements ImportResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>sourceSite</code> attribute. */
    private AttributeDefinition sourceSiteDef;

    /** The AttributeDefinition object for the <code>sourceId</code> attribute. */
    private AttributeDefinition sourceIdDef;

    /** The AttributeDefinition object for the <code>destination</code> attribute. */
    private AttributeDefinition destinationDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.aggregation.import</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public ImportResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("cms.aggregation.import");
            sourceSiteDef = rc.getAttribute("sourceSite");
            sourceIdDef = rc.getAttribute("sourceId");
            destinationDef = rc.getAttribute("destination");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.aggregation.import</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static ImportResource getImportResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof ImportResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.aggregation.import");
        }
        return (ImportResource)res;
    }

    /**
     * Creates a new <code>cms.aggregation.import</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param sourceSite the sourceSite attribute
     * @param sourceId the sourceId attribute
     * @param destination the destination attribute
     * @return a new ImportResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     */
    public static ImportResource createImportResource(CoralSession session, String name,
        Resource parent, SiteResource sourceSite, long sourceId, Resource destination)
        throws ValueRequiredException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("cms.aggregation.import");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("sourceSite"), sourceSite);
            attrs.put(rc.getAttribute("sourceId"), new Long(sourceId));
            attrs.put(rc.getAttribute("destination"), destination);
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof ImportResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (ImportResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>sourceSite</code> attribute.
     *
     * @return the value of the <code>sourceSite</code> attribute.
     */
    public SiteResource getSourceSite()
    {
        return (SiteResource)get(sourceSiteDef);
    }
 
    /**
     * Sets the value of the <code>sourceSite</code> attribute.
     *
     * @param value the value of the <code>sourceSite</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setSourceSite(SiteResource value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(sourceSiteDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute sourceSite "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>sourceId</code> attribute.
     *
     * @return the value of the <code>sourceId</code> attribute.
     */
    public long getSourceId()
    {
        if(isDefined(sourceIdDef))
        {
            return ((Long)get(sourceIdDef)).longValue();
        }
        else
        {
            throw new BackendException("incompatible schema change");
        }
    }    

    /**
     * Sets the value of the <code>sourceId</code> attribute.
     *
     * @param value the value of the <code>sourceId</code> attribute.
     */
    public void setSourceId(long value)
    {
        try
        {
            set(sourceIdDef, new Long(value));
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
     * Returns the value of the <code>destination</code> attribute.
     *
     * @return the value of the <code>destination</code> attribute.
     */
    public Resource getDestination()
    {
        return (Resource)get(destinationDef);
    }
 
    /**
     * Sets the value of the <code>destination</code> attribute.
     *
     * @param value the value of the <code>destination</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setDestination(Resource value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(destinationDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute destination "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
     
    // @custom methods ///////////////////////////////////////////////////////

    // @order sourceSite, sourceId, destination
    // @import org.objectledge.coral.session.CoralSession
    
    /**
     * Return the state of the import in question.
     * 
     * @return import state, see {@link AggretationConstants}
     */
    public int getState(CoralSession coralSession)
    {
        Resource sourceRes;
        Resource destinationRes = getDestination();
        try
        {
            sourceRes = coralSession.getStore().getResource(getSourceId());
        }
        catch(EntityDoesNotExistException e)
        {
            return AggregationConstants.IMPORT_DELETED;
        }
        if(sourceRes.getModificationTime().compareTo(destinationRes.getModificationTime()) < 0)
        {
            return AggregationConstants.IMPORT_UPTODATE;
        }
        else
        {
            return AggregationConstants.IMPORT_MODIFIED;            
        }
    }
    
    /**
     * Returns the source resource if available.
     * 
     * @return source resource, or null if deleted.
     */
    public Resource getSource(CoralSession coralSession)
    {
        try
        {
            return coralSession.getStore().getResource(getSourceId());
        }
        catch(EntityDoesNotExistException e)
        {
            return null;
        }
    }
    
    /**
     * Returns import's desitnation site.
     * 
     * @return import's desitnation site.
     */
    public SiteResource getDestinationSite()
    {
        return (SiteResource)getParent().getParent().getParent().getParent();
    }
}
