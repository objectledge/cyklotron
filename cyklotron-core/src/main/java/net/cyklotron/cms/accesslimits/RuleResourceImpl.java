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
 * An implementation of <code>cms.accesslimits.rule</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class RuleResourceImpl
    extends CmsNodeResourceImpl
    implements RuleResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
	@SuppressWarnings("unused")
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>priority</code> attribute. */
    private static AttributeDefinition<Integer> priorityDef;

    /** The AttributeDefinition object for the <code>ruleDefinition</code> attribute. */
	private static AttributeDefinition<String> ruleDefinitionDef;

    /** The AttributeDefinition object for the <code>urlPattern</code> attribute. */
	private static AttributeDefinition<String> urlPatternDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.accesslimits.rule</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     */
    public RuleResourceImpl()
    {
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.accesslimits.rule</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static RuleResource getRuleResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof RuleResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.accesslimits.rule");
        }
        return (RuleResource)res;
    }

    /**
     * Creates a new <code>cms.accesslimits.rule</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param priority the priority attribute
     * @param ruleDefinition the ruleDefinition attribute
     * @param urlPattern the urlPattern attribute
     * @return a new RuleResource instance.
     * @throws ValueRequiredException if one of the required attribues is undefined.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static RuleResource createRuleResource(CoralSession session, String name, Resource
        parent, int priority, String ruleDefinition, String urlPattern)
        throws ValueRequiredException, InvalidResourceNameException
    {
        try
        {
            ResourceClass<RuleResource> rc = session.getSchema().getResourceClass("cms.accesslimits.rule", RuleResource.class);
			Map<AttributeDefinition<?>, Object> attrs = new HashMap<AttributeDefinition<?>, Object>();
            attrs.put(rc.getAttribute("priority"), Integer.valueOf(priority));
            attrs.put(rc.getAttribute("ruleDefinition"), ruleDefinition);
            attrs.put(rc.getAttribute("urlPattern"), urlPattern);
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof RuleResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (RuleResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>priority</code> attribute.
     *
     * @return the value of the <code>priority</code> attribute.
     */
    public int getPriority()
    {
		return get(priorityDef).intValue();
    }    

    /**
     * Sets the value of the <code>priority</code> attribute.
     *
     * @param value the value of the <code>priority</code> attribute.
     */
    public void setPriority(int value)
    {
        try
        {
            set(priorityDef, Integer.valueOf(value));
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
     * Returns the value of the <code>ruleDefinition</code> attribute.
     *
     * @return the value of the <code>ruleDefinition</code> attribute.
     */
    public String getRuleDefinition()
    {
        return get(ruleDefinitionDef);
    }
 
    /**
     * Sets the value of the <code>ruleDefinition</code> attribute.
     *
     * @param value the value of the <code>ruleDefinition</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setRuleDefinition(String value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(ruleDefinitionDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute ruleDefinition "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
    
    /**
     * Returns the value of the <code>urlPattern</code> attribute.
     *
     * @return the value of the <code>urlPattern</code> attribute.
     */
    public String getUrlPattern()
    {
        return get(urlPatternDef);
    }
 
    /**
     * Sets the value of the <code>urlPattern</code> attribute.
     *
     * @param value the value of the <code>urlPattern</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setUrlPattern(String value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(urlPatternDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute urlPattern "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
     
    // @custom methods ///////////////////////////////////////////////////////
}
