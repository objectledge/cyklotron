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
 
package net.cyklotron.cms.accesslimits;

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
 * An implementation of <code>cms.accesslimits.notifications_config</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class NotificationsConfigResourceImpl
    extends CmsNodeResourceImpl
    implements NotificationsConfigResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
	@SuppressWarnings("unused")
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>baseURL</code> attribute. */
	private static AttributeDefinition<String> baseURLDef;

    /** The AttributeDefinition object for the <code>locale</code> attribute. */
	private static AttributeDefinition<String> localeDef;

    /** The AttributeDefinition object for the <code>recipient</code> attribute. */
	private static AttributeDefinition<String> recipientDef;

    /** The AttributeDefinition object for the <code>threshold</code> attribute. */
    private static AttributeDefinition<Integer> thresholdDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.accesslimits.notifications_config</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public NotificationsConfigResourceImpl()
    {
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.accesslimits.notifications_config</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static NotificationsConfigResource getNotificationsConfigResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof NotificationsConfigResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.accesslimits.notifications_config");
        }
        return (NotificationsConfigResource)res;
    }

    /**
     * Creates a new <code>cms.accesslimits.notifications_config</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param baseURL the baseURL attribute
     * @param locale the locale attribute
     * @param recipient the recipient attribute
     * @param threshold the threshold attribute
     * @return a new NotificationsConfigResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static NotificationsConfigResource createNotificationsConfigResource(CoralSession
        session, String name, Resource parent, String baseURL, String locale, String recipient, int
        threshold)
        throws ValueRequiredException, InvalidResourceNameException
    {
        try
        {
            ResourceClass<NotificationsConfigResource> rc = session.getSchema().getResourceClass("cms.accesslimits.notifications_config", NotificationsConfigResource.class);
			Map<AttributeDefinition<?>, Object> attrs = new HashMap<AttributeDefinition<?>, Object>();
            attrs.put(rc.getAttribute("baseURL"), baseURL);
            attrs.put(rc.getAttribute("locale"), locale);
            attrs.put(rc.getAttribute("recipient"), recipient);
            attrs.put(rc.getAttribute("threshold"), Integer.valueOf(threshold));
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof NotificationsConfigResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (NotificationsConfigResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>baseURL</code> attribute.
     *
     * @return the value of the <code>baseURL</code> attribute.
     */
    public String getBaseURL()
    {
        return get(baseURLDef);
    }
 
    /**
     * Sets the value of the <code>baseURL</code> attribute.
     *
     * @param value the value of the <code>baseURL</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setBaseURL(String value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(baseURLDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute baseURL "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>locale</code> attribute.
     *
     * @return the value of the <code>locale</code> attribute.
     */
    public String getLocale()
    {
        return get(localeDef);
    }
 
    /**
     * Sets the value of the <code>locale</code> attribute.
     *
     * @param value the value of the <code>locale</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setLocale(String value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(localeDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute locale "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>recipient</code> attribute.
     *
     * @return the value of the <code>recipient</code> attribute.
     */
    public String getRecipient()
    {
        return get(recipientDef);
    }
 
    /**
     * Sets the value of the <code>recipient</code> attribute.
     *
     * @param value the value of the <code>recipient</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setRecipient(String value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(recipientDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute recipient "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>threshold</code> attribute.
     *
     * @return the value of the <code>threshold</code> attribute.
     */
    public int getThreshold()
    {
		return get(thresholdDef).intValue();
    }    

    /**
     * Sets the value of the <code>threshold</code> attribute.
     *
     * @param value the value of the <code>threshold</code> attribute.
     */
    public void setThreshold(int value)
    {
        try
        {
            set(thresholdDef, Integer.valueOf(value));
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
