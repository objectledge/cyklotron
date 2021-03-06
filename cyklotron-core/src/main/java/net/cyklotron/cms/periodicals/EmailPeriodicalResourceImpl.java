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

import org.objectledge.coral.BackendException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.AttributeDefinition;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.ModificationNotPermitedException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;

/**
 * An implementation of <code>cms.periodicals.email_periodical</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class EmailPeriodicalResourceImpl
    extends PeriodicalResourceImpl
    implements EmailPeriodicalResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
	@SuppressWarnings("unused")
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>addresses</code> attribute. */
	private static AttributeDefinition<String> addressesDef;

    /** The AttributeDefinition object for the <code>fromHeader</code> attribute. */
	private static AttributeDefinition<String> fromHeaderDef;

    /** The AttributeDefinition object for the <code>fullContent</code> attribute. */
    private static AttributeDefinition<Boolean> fullContentDef;

    /** The AttributeDefinition object for the <code>notificationRenderer</code> attribute. */
	private static AttributeDefinition<String> notificationRendererDef;

    /** The AttributeDefinition object for the <code>notificationTemplate</code> attribute. */
	private static AttributeDefinition<String> notificationTemplateDef;

    /** The AttributeDefinition object for the <code>replyToHeader</code> attribute. */
	private static AttributeDefinition<String> replyToHeaderDef;

    /** The AttributeDefinition object for the <code>sendEmpty</code> attribute. */
    private static AttributeDefinition<Boolean> sendEmptyDef;

    /** The AttributeDefinition object for the <code>subject</code> attribute. */
	private static AttributeDefinition<String> subjectDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.periodicals.email_periodical</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public EmailPeriodicalResourceImpl()
    {
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
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static EmailPeriodicalResource createEmailPeriodicalResource(CoralSession session,
        String name, Resource parent)
        throws InvalidResourceNameException
    {
        try
        {
            ResourceClass<EmailPeriodicalResource> rc = session.getSchema().getResourceClass("cms.periodicals.email_periodical", EmailPeriodicalResource.class);
		    Resource res = session.getStore().createResource(name, parent, rc,
                java.util.Collections.<AttributeDefinition<?>, Object> emptyMap());			
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
        return get(addressesDef);
    }
    
    /**
     * Returns the value of the <code>addresses</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>addresses</code> attribute.
     */
    public String getAddresses(String defaultValue)
    {
        return get(addressesDef, defaultValue);
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
     * Returns the value of the <code>fromHeader</code> attribute.
     *
     * @return the value of the <code>fromHeader</code> attribute.
     */
    public String getFromHeader()
    {
        return get(fromHeaderDef);
    }
    
    /**
     * Returns the value of the <code>fromHeader</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>fromHeader</code> attribute.
     */
    public String getFromHeader(String defaultValue)
    {
        return get(fromHeaderDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>fromHeader</code> attribute.
     *
     * @param value the value of the <code>fromHeader</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setFromHeader(String value)
    {
        try
        {
            if(value != null)
            {
                set(fromHeaderDef, value);
            }
            else
            {
                unset(fromHeaderDef);
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
	 * Checks if the value of the <code>fromHeader</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>fromHeader</code> attribute is defined.
	 */
    public boolean isFromHeaderDefined()
	{
	    return isDefined(fromHeaderDef);
	}

    /**
     * Returns the value of the <code>fullContent</code> attribute.
     *
     * @return the value of the <code>fullContent</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getFullContent()
        throws IllegalStateException
    {
	    Boolean value = get(fullContentDef);
        if(value != null)
        {
            return value.booleanValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute fullContent is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>fullContent</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>fullContent</code> attribute.
     */
    public boolean getFullContent(boolean defaultValue)
    {
		return get(fullContentDef, Boolean.valueOf(defaultValue)).booleanValue();
	}

    /**
     * Sets the value of the <code>fullContent</code> attribute.
     *
     * @param value the value of the <code>fullContent</code> attribute.
     */
    public void setFullContent(boolean value)
    {
        try
        {
            set(fullContentDef, Boolean.valueOf(value));
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
     * Removes the value of the <code>fullContent</code> attribute.
     */
    public void unsetFullContent()
    {
        try
        {
            unset(fullContentDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>fullContent</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>fullContent</code> attribute is defined.
	 */
    public boolean isFullContentDefined()
	{
	    return isDefined(fullContentDef);
	}
 
    /**
     * Returns the value of the <code>notificationRenderer</code> attribute.
     *
     * @return the value of the <code>notificationRenderer</code> attribute.
     */
    public String getNotificationRenderer()
    {
        return get(notificationRendererDef);
    }
    
    /**
     * Returns the value of the <code>notificationRenderer</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>notificationRenderer</code> attribute.
     */
    public String getNotificationRenderer(String defaultValue)
    {
        return get(notificationRendererDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>notificationRenderer</code> attribute.
     *
     * @param value the value of the <code>notificationRenderer</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setNotificationRenderer(String value)
    {
        try
        {
            if(value != null)
            {
                set(notificationRendererDef, value);
            }
            else
            {
                unset(notificationRendererDef);
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
	 * Checks if the value of the <code>notificationRenderer</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>notificationRenderer</code> attribute is defined.
	 */
    public boolean isNotificationRendererDefined()
	{
	    return isDefined(notificationRendererDef);
	}
 
    /**
     * Returns the value of the <code>notificationTemplate</code> attribute.
     *
     * @return the value of the <code>notificationTemplate</code> attribute.
     */
    public String getNotificationTemplate()
    {
        return get(notificationTemplateDef);
    }
    
    /**
     * Returns the value of the <code>notificationTemplate</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>notificationTemplate</code> attribute.
     */
    public String getNotificationTemplate(String defaultValue)
    {
        return get(notificationTemplateDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>notificationTemplate</code> attribute.
     *
     * @param value the value of the <code>notificationTemplate</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setNotificationTemplate(String value)
    {
        try
        {
            if(value != null)
            {
                set(notificationTemplateDef, value);
            }
            else
            {
                unset(notificationTemplateDef);
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
	 * Checks if the value of the <code>notificationTemplate</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>notificationTemplate</code> attribute is defined.
	 */
    public boolean isNotificationTemplateDefined()
	{
	    return isDefined(notificationTemplateDef);
	}
 
    /**
     * Returns the value of the <code>replyToHeader</code> attribute.
     *
     * @return the value of the <code>replyToHeader</code> attribute.
     */
    public String getReplyToHeader()
    {
        return get(replyToHeaderDef);
    }
    
    /**
     * Returns the value of the <code>replyToHeader</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>replyToHeader</code> attribute.
     */
    public String getReplyToHeader(String defaultValue)
    {
        return get(replyToHeaderDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>replyToHeader</code> attribute.
     *
     * @param value the value of the <code>replyToHeader</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setReplyToHeader(String value)
    {
        try
        {
            if(value != null)
            {
                set(replyToHeaderDef, value);
            }
            else
            {
                unset(replyToHeaderDef);
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
	 * Checks if the value of the <code>replyToHeader</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>replyToHeader</code> attribute is defined.
	 */
    public boolean isReplyToHeaderDefined()
	{
	    return isDefined(replyToHeaderDef);
	}

    /**
     * Returns the value of the <code>sendEmpty</code> attribute.
     *
     * @return the value of the <code>sendEmpty</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getSendEmpty()
        throws IllegalStateException
    {
	    Boolean value = get(sendEmptyDef);
        if(value != null)
        {
            return value.booleanValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute sendEmpty is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>sendEmpty</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>sendEmpty</code> attribute.
     */
    public boolean getSendEmpty(boolean defaultValue)
    {
		return get(sendEmptyDef, Boolean.valueOf(defaultValue)).booleanValue();
	}

    /**
     * Sets the value of the <code>sendEmpty</code> attribute.
     *
     * @param value the value of the <code>sendEmpty</code> attribute.
     */
    public void setSendEmpty(boolean value)
    {
        try
        {
            set(sendEmptyDef, Boolean.valueOf(value));
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
     * Removes the value of the <code>sendEmpty</code> attribute.
     */
    public void unsetSendEmpty()
    {
        try
        {
            unset(sendEmptyDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>sendEmpty</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>sendEmpty</code> attribute is defined.
	 */
    public boolean isSendEmptyDefined()
	{
	    return isDefined(sendEmptyDef);
	}
 
    /**
     * Returns the value of the <code>subject</code> attribute.
     *
     * @return the value of the <code>subject</code> attribute.
     */
    public String getSubject()
    {
        return get(subjectDef);
    }
    
    /**
     * Returns the value of the <code>subject</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>subject</code> attribute.
     */
    public String getSubject(String defaultValue)
    {
        return get(subjectDef, defaultValue);
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
