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
 
package net.cyklotron.cms.preferences;

import java.util.HashMap;
import java.util.Map;

import org.objectledge.coral.BackendException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.CoralSchema;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.database.Database;
import org.objectledge.parameters.Parameters;

import net.cyklotron.cms.CmsNodeResourceImpl;
import org.jcontainer.dna.Logger;

/**
 * An implementation of <code>preferences.preferences</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class PreferencesResourceImpl
    extends CmsNodeResourceImpl
    implements PreferencesResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>preferences</code> attribute. */
    private AttributeDefinition preferencesDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>preferences.preferences</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public PreferencesResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("preferences.preferences");
            preferencesDef = rc.getAttribute("preferences");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>preferences.preferences</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static PreferencesResource getPreferencesResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof PreferencesResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not preferences.preferences");
        }
        return (PreferencesResource)res;
    }

    /**
     * Creates a new <code>preferences.preferences</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param preferences the preferences attribute
     * @return a new PreferencesResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     */
    public static PreferencesResource createPreferencesResource(CoralSession session, String
        name, Resource parent, Parameters preferences)
        throws ValueRequiredException
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("preferences.preferences");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("preferences"), preferences);
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof PreferencesResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (PreferencesResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>preferences</code> attribute.
     *
     * @return the value of the <code>preferences</code> attribute.
     */
    public Parameters getPreferences()
    {
        return (Parameters)get(preferencesDef);
    }
    
    // @custom methods ///////////////////////////////////////////////////////
}
