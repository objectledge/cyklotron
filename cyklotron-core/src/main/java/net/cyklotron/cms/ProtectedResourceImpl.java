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
import java.util.HashMap;
import java.util.Map;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.BackendException;
import org.objectledge.coral.datatypes.GenericResource;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.CoralSchema;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.database.Database;

/**
 * An implementation of <code>protected</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class ProtectedResourceImpl
    extends GenericResource
    implements ProtectedResource
{
    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>protected</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public ProtectedResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>protected</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static ProtectedResource getProtectedResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof ProtectedResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not protected");
        }
        return (ProtectedResource)res;
    }

    /**
     * Creates a new <code>protected</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new ProtectedResource instance.
     */
    public static ProtectedResource createProtectedResource(CoralSession session, String name,
        Resource parent)
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("protected");
            Map attrs = new HashMap();
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof ProtectedResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (ProtectedResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }
 
    // @custom methods ///////////////////////////////////////////////////////

    // @import java.util.Date
    // @import net.labeo.services.resource.Subject

    /**
     * Checks if this resource can be viewed at the given time.
     */
    public boolean isValid(Date time)
    {
        return true;
    }

    /**
     * Checks if a given subject can view this resource.
     */
    public boolean canView(Subject subject)
    {
        return true;
    }

    /**
     * Checks if the specified subject can modify this resource.
     */
    public boolean canModify(Subject subject)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Checks if the specified subject can remove this resource.
     */
    public boolean canRemove(Subject subject)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Checks if the specified subject can add children to this resource.
     */
    public boolean canAddChild(Subject subject)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if the specified subject can view this resource at the given time.
     */
    public boolean canView(Subject subject, Date time)
    {
        return true;
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
        else
        {
            return canView(subject, data.getDate());
        }
    }
}
