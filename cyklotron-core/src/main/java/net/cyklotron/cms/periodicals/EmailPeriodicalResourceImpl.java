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
 
package net.cyklotron.cms.periodicals;

import java.util.HashMap;
import java.util.Map;

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

import org.jcontainer.dna.Logger;

/**
 * An implementation of <code>cms.periodicals.email_periodical</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class EmailPeriodicalResourceImpl
    extends PeriodicalResourceImpl
    implements EmailPeriodicalResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>addresses</code> attribute. */
    private AttributeDefinition addressesDef;

    /** The AttributeDefinition object for the <code>from_header</code> attribute. */
    private AttributeDefinition from_headerDef;

    /** The AttributeDefinition object for the <code>full_content</code> attribute. */
    private AttributeDefinition full_contentDef;

    /** The AttributeDefinition object for the <code>notification_renderer</code> attribute. */
    private AttributeDefinition notification_rendererDef;

    /** The AttributeDefinition object for the <code>notification_template</code> attribute. */
    private AttributeDefinition notification_templateDef;

    /** The AttributeDefinition object for the <code>subject</code> attribute. */
    private AttributeDefinition subjectDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.periodicals.email_periodical</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public EmailPeriodicalResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("cms.periodicals.email_periodical");
            addressesDef = rc.getAttribute("addresses");
            from_headerDef = rc.getAttribute("from_header");
            full_contentDef = rc.getAttribute("full_content");
            notification_rendererDef = rc.getAttribute("notification_renderer");
            notification_templateDef = rc.getAttribute("notification_template");
            subjectDef = rc.getAttribute("subject");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.periodicals.email_periodical</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static EmailPeriodicalResource getEmailPeriodicalResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof EmailPeriodicalResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.periodicals.email_periodical");
        }
        return (EmailPeriodicalResource)res;
    }

    /**
     * Creates a new <code>cms.periodicals.email_periodical</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new EmailPeriodicalResource instance.
     */
    public static EmailPeriodicalResource createEmailPeriodicalResource(CoralSession session,
        String name, Resource parent)
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("cms.periodicals.email_periodical");
            Map attrs = new HashMap();
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof EmailPeriodicalResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (EmailPeriodicalResource)res;
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

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>addresses</code> attribute.
     *
     * @return the value of the <code>addresses</code> attribute.
     */
    public String getAddresses()
    {
        return (String)get(addressesDef);
    }
    
    /**
     * Returns the value of the <code>addresses</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>addresses</code> attribute.
     */
    public String getAddresses(String defaultValue)
    {
        if(isDefined(addressesDef))
        {
            return (String)get(addressesDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>addresses</code> attribute.
     *
     * @param value the value of the <code>addresses</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAddresses(String value)
    {
        try
        {
            if(value != null)
            {
                set(addressesDef, value);
            }
            else
            {
                unset(addressesDef);
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
	 * Checks if the value of the <code>addresses</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>addresses</code> attribute is defined.
	 */
    public boolean isAddressesDefined()
	{
	    return isDefined(addressesDef);
	}
 
    /**
     * Returns the value of the <code>from_header</code> attribute.
     *
     * @return the value of the <code>from_header</code> attribute.
     */
    public String getFrom_header()
    {
        return (String)get(from_headerDef);
    }
    
    /**
     * Returns the value of the <code>from_header</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>from_header</code> attribute.
     */
    public String getFrom_header(String defaultValue)
    {
        if(isDefined(from_headerDef))
        {
            return (String)get(from_headerDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>from_header</code> attribute.
     *
     * @param value the value of the <code>from_header</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setFrom_header(String value)
    {
        try
        {
            if(value != null)
            {
                set(from_headerDef, value);
            }
            else
            {
                unset(from_headerDef);
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
	 * Checks if the value of the <code>from_header</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>from_header</code> attribute is defined.
	 */
    public boolean isFrom_headerDefined()
	{
	    return isDefined(from_headerDef);
	}

    /**
     * Returns the value of the <code>full_content</code> attribute.
     *
     * @return the value of the <code>full_content</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getFull_content()
        throws IllegalStateException
    {
        if(isDefined(full_contentDef))
        {
            return ((Boolean)get(full_contentDef)).booleanValue();
        }
        else
        {
            throw new IllegalStateException("attribute value is undefined");
        }
    }

    /**
     * Returns the value of the <code>full_content</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>full_content</code> attribute.
     */
    public boolean getFull_content(boolean defaultValue)
    {
        if(isDefined(full_contentDef))
        {
            return ((Boolean)get(full_contentDef)).booleanValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>full_content</code> attribute.
     *
     * @param value the value of the <code>full_content</code> attribute.
     */
    public void setFull_content(boolean value)
    {
        try
        {
            set(full_contentDef, new Boolean(value));
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
     * Removes the value of the <code>full_content</code> attribute.
     */
    public void unsetFull_content()
    {
        try
        {
            unset(full_contentDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>full_content</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>full_content</code> attribute is defined.
	 */
    public boolean isFull_contentDefined()
	{
	    return isDefined(full_contentDef);
	}
 
    /**
     * Returns the value of the <code>notification_renderer</code> attribute.
     *
     * @return the value of the <code>notification_renderer</code> attribute.
     */
    public String getNotification_renderer()
    {
        return (String)get(notification_rendererDef);
    }
    
    /**
     * Returns the value of the <code>notification_renderer</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>notification_renderer</code> attribute.
     */
    public String getNotification_renderer(String defaultValue)
    {
        if(isDefined(notification_rendererDef))
        {
            return (String)get(notification_rendererDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>notification_renderer</code> attribute.
     *
     * @param value the value of the <code>notification_renderer</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setNotification_renderer(String value)
    {
        try
        {
            if(value != null)
            {
                set(notification_rendererDef, value);
            }
            else
            {
                unset(notification_rendererDef);
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
	 * Checks if the value of the <code>notification_renderer</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>notification_renderer</code> attribute is defined.
	 */
    public boolean isNotification_rendererDefined()
	{
	    return isDefined(notification_rendererDef);
	}
 
    /**
     * Returns the value of the <code>notification_template</code> attribute.
     *
     * @return the value of the <code>notification_template</code> attribute.
     */
    public String getNotification_template()
    {
        return (String)get(notification_templateDef);
    }
    
    /**
     * Returns the value of the <code>notification_template</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>notification_template</code> attribute.
     */
    public String getNotification_template(String defaultValue)
    {
        if(isDefined(notification_templateDef))
        {
            return (String)get(notification_templateDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>notification_template</code> attribute.
     *
     * @param value the value of the <code>notification_template</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setNotification_template(String value)
    {
        try
        {
            if(value != null)
            {
                set(notification_templateDef, value);
            }
            else
            {
                unset(notification_templateDef);
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
	 * Checks if the value of the <code>notification_template</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>notification_template</code> attribute is defined.
	 */
    public boolean isNotification_templateDefined()
	{
	    return isDefined(notification_templateDef);
	}
 
    /**
     * Returns the value of the <code>subject</code> attribute.
     *
     * @return the value of the <code>subject</code> attribute.
     */
    public String getSubject()
    {
        return (String)get(subjectDef);
    }
    
    /**
     * Returns the value of the <code>subject</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>subject</code> attribute.
     */
    public String getSubject(String defaultValue)
    {
        if(isDefined(subjectDef))
        {
            return (String)get(subjectDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>subject</code> attribute.
     *
     * @param value the value of the <code>subject</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setSubject(String value)
    {
        try
        {
            if(value != null)
            {
                set(subjectDef, value);
            }
            else
            {
                unset(subjectDef);
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
	 * Checks if the value of the <code>subject</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>subject</code> attribute is defined.
	 */
    public boolean isSubjectDefined()
	{
	    return isDefined(subjectDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
}
