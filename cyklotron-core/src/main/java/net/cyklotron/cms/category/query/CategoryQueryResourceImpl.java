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
 
package net.cyklotron.cms.category.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

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
import org.jcontainer.dna.Logger;

/**
 * An implementation of <code>category.query</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class CategoryQueryResourceImpl
    extends CmsNodeResourceImpl
    implements CategoryQueryResource
{
    // instance variables ////////////////////////////////////////////////////

    /** The AttributeDefinition object for the <code>acceptedResourceClasses</code> attribute. */
    private AttributeDefinition acceptedResourceClassesDef;

    /** The AttributeDefinition object for the <code>acceptedSites</code> attribute. */
    private AttributeDefinition acceptedSitesDef;

    /** The AttributeDefinition object for the <code>longQuery</code> attribute. */
    private AttributeDefinition longQueryDef;

    /** The AttributeDefinition object for the <code>optionalCategoryIdentifiers</code> attribute. */
    private AttributeDefinition optionalCategoryIdentifiersDef;

    /** The AttributeDefinition object for the <code>optionalCategoryPaths</code> attribute. */
    private AttributeDefinition optionalCategoryPathsDef;

    /** The AttributeDefinition object for the <code>query</code> attribute. */
    private AttributeDefinition queryDef;

    /** The AttributeDefinition object for the <code>requiredCategoryIdentifiers</code> attribute. */
    private AttributeDefinition requiredCategoryIdentifiersDef;

    /** The AttributeDefinition object for the <code>requiredCategoryPaths</code> attribute. */
    private AttributeDefinition requiredCategoryPathsDef;

    /** The AttributeDefinition object for the <code>simpleQuery</code> attribute. */
    private AttributeDefinition simpleQueryDef;

    /** The AttributeDefinition object for the <code>useIdsAsIdentifiers</code> attribute. */
    private AttributeDefinition useIdsAsIdentifiersDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>category.query</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param schema the CoralSchema.
     * @param database the Database.
     * @param logger the Logger.
     */
    public CategoryQueryResourceImpl(CoralSchema schema, Database database, Logger logger)
    {
        super(schema, database, logger);
        try
        {
            ResourceClass rc = schema.getResourceClass("category.query");
            acceptedResourceClassesDef = rc.getAttribute("acceptedResourceClasses");
            acceptedSitesDef = rc.getAttribute("acceptedSites");
            longQueryDef = rc.getAttribute("longQuery");
            optionalCategoryIdentifiersDef = rc.getAttribute("optionalCategoryIdentifiers");
            optionalCategoryPathsDef = rc.getAttribute("optionalCategoryPaths");
            queryDef = rc.getAttribute("query");
            requiredCategoryIdentifiersDef = rc.getAttribute("requiredCategoryIdentifiers");
            requiredCategoryPathsDef = rc.getAttribute("requiredCategoryPaths");
            simpleQueryDef = rc.getAttribute("simpleQuery");
            useIdsAsIdentifiersDef = rc.getAttribute("useIdsAsIdentifiers");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>category.query</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static CategoryQueryResource getCategoryQueryResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof CategoryQueryResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not category.query");
        }
        return (CategoryQueryResource)res;
    }

    /**
     * Creates a new <code>category.query</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new CategoryQueryResource instance.
     */
    public static CategoryQueryResource createCategoryQueryResource(CoralSession session, String
        name, Resource parent)
    {
        try
        {
            ResourceClass rc = session.getSchema().getResourceClass("category.query");
            Map attrs = new HashMap();
            Resource res = session.getStore().createResource(name, parent, rc, attrs);
            if(!(res instanceof CategoryQueryResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (CategoryQueryResource)res;
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
     * Returns the value of the <code>acceptedResourceClasses</code> attribute.
     *
     * @return the value of the <code>acceptedResourceClasses</code> attribute.
     */
    public String getAcceptedResourceClasses()
    {
        return (String)get(acceptedResourceClassesDef);
    }
    
    /**
     * Returns the value of the <code>acceptedResourceClasses</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>acceptedResourceClasses</code> attribute.
     */
    public String getAcceptedResourceClasses(String defaultValue)
    {
        if(isDefined(acceptedResourceClassesDef))
        {
            return (String)get(acceptedResourceClassesDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>acceptedResourceClasses</code> attribute.
     *
     * @param value the value of the <code>acceptedResourceClasses</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAcceptedResourceClasses(String value)
    {
        try
        {
            if(value != null)
            {
                set(acceptedResourceClassesDef, value);
            }
            else
            {
                unset(acceptedResourceClassesDef);
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
	 * Checks if the value of the <code>acceptedResourceClasses</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>acceptedResourceClasses</code> attribute is defined.
	 */
    public boolean isAcceptedResourceClassesDefined()
	{
	    return isDefined(acceptedResourceClassesDef);
	}
 
    /**
     * Returns the value of the <code>acceptedSites</code> attribute.
     *
     * @return the value of the <code>acceptedSites</code> attribute.
     */
    public String getAcceptedSites()
    {
        return (String)get(acceptedSitesDef);
    }
    
    /**
     * Returns the value of the <code>acceptedSites</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>acceptedSites</code> attribute.
     */
    public String getAcceptedSites(String defaultValue)
    {
        if(isDefined(acceptedSitesDef))
        {
            return (String)get(acceptedSitesDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>acceptedSites</code> attribute.
     *
     * @param value the value of the <code>acceptedSites</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAcceptedSites(String value)
    {
        try
        {
            if(value != null)
            {
                set(acceptedSitesDef, value);
            }
            else
            {
                unset(acceptedSitesDef);
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
	 * Checks if the value of the <code>acceptedSites</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>acceptedSites</code> attribute is defined.
	 */
    public boolean isAcceptedSitesDefined()
	{
	    return isDefined(acceptedSitesDef);
	}
 
    /**
     * Returns the value of the <code>longQuery</code> attribute.
     *
     * @return the value of the <code>longQuery</code> attribute.
     */
    public String getLongQuery()
    {
        return (String)get(longQueryDef);
    }
    
    /**
     * Returns the value of the <code>longQuery</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>longQuery</code> attribute.
     */
    public String getLongQuery(String defaultValue)
    {
        if(isDefined(longQueryDef))
        {
            return (String)get(longQueryDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>longQuery</code> attribute.
     *
     * @param value the value of the <code>longQuery</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLongQuery(String value)
    {
        try
        {
            if(value != null)
            {
                set(longQueryDef, value);
            }
            else
            {
                unset(longQueryDef);
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
	 * Checks if the value of the <code>longQuery</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>longQuery</code> attribute is defined.
	 */
    public boolean isLongQueryDefined()
	{
	    return isDefined(longQueryDef);
	}
 
    /**
     * Returns the value of the <code>optionalCategoryIdentifiers</code> attribute.
     *
     * @return the value of the <code>optionalCategoryIdentifiers</code> attribute.
     */
    public String getOptionalCategoryIdentifiers()
    {
        return (String)get(optionalCategoryIdentifiersDef);
    }
    
    /**
     * Returns the value of the <code>optionalCategoryIdentifiers</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>optionalCategoryIdentifiers</code> attribute.
     */
    public String getOptionalCategoryIdentifiers(String defaultValue)
    {
        if(isDefined(optionalCategoryIdentifiersDef))
        {
            return (String)get(optionalCategoryIdentifiersDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>optionalCategoryIdentifiers</code> attribute.
     *
     * @param value the value of the <code>optionalCategoryIdentifiers</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setOptionalCategoryIdentifiers(String value)
    {
        try
        {
            if(value != null)
            {
                set(optionalCategoryIdentifiersDef, value);
            }
            else
            {
                unset(optionalCategoryIdentifiersDef);
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
	 * Checks if the value of the <code>optionalCategoryIdentifiers</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>optionalCategoryIdentifiers</code> attribute is defined.
	 */
    public boolean isOptionalCategoryIdentifiersDefined()
	{
	    return isDefined(optionalCategoryIdentifiersDef);
	}
 
    /**
     * Returns the value of the <code>optionalCategoryPaths</code> attribute.
     *
     * @return the value of the <code>optionalCategoryPaths</code> attribute.
     */
    public String getOptionalCategoryPaths()
    {
        return (String)get(optionalCategoryPathsDef);
    }
    
    /**
     * Returns the value of the <code>optionalCategoryPaths</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>optionalCategoryPaths</code> attribute.
     */
    public String getOptionalCategoryPaths(String defaultValue)
    {
        if(isDefined(optionalCategoryPathsDef))
        {
            return (String)get(optionalCategoryPathsDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>optionalCategoryPaths</code> attribute.
     *
     * @param value the value of the <code>optionalCategoryPaths</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setOptionalCategoryPaths(String value)
    {
        try
        {
            if(value != null)
            {
                set(optionalCategoryPathsDef, value);
            }
            else
            {
                unset(optionalCategoryPathsDef);
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
	 * Checks if the value of the <code>optionalCategoryPaths</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>optionalCategoryPaths</code> attribute is defined.
	 */
    public boolean isOptionalCategoryPathsDefined()
	{
	    return isDefined(optionalCategoryPathsDef);
	}
 
    /**
     * Returns the value of the <code>query</code> attribute.
     *
     * @return the value of the <code>query</code> attribute.
     */
    public String getQuery()
    {
        return (String)get(queryDef);
    }
    
    /**
     * Returns the value of the <code>query</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>query</code> attribute.
     */
    public String getQuery(String defaultValue)
    {
        if(isDefined(queryDef))
        {
            return (String)get(queryDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>query</code> attribute.
     *
     * @param value the value of the <code>query</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setQuery(String value)
    {
        try
        {
            if(value != null)
            {
                set(queryDef, value);
            }
            else
            {
                unset(queryDef);
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
	 * Checks if the value of the <code>query</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>query</code> attribute is defined.
	 */
    public boolean isQueryDefined()
	{
	    return isDefined(queryDef);
	}
 
    /**
     * Returns the value of the <code>requiredCategoryIdentifiers</code> attribute.
     *
     * @return the value of the <code>requiredCategoryIdentifiers</code> attribute.
     */
    public String getRequiredCategoryIdentifiers()
    {
        return (String)get(requiredCategoryIdentifiersDef);
    }
    
    /**
     * Returns the value of the <code>requiredCategoryIdentifiers</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>requiredCategoryIdentifiers</code> attribute.
     */
    public String getRequiredCategoryIdentifiers(String defaultValue)
    {
        if(isDefined(requiredCategoryIdentifiersDef))
        {
            return (String)get(requiredCategoryIdentifiersDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>requiredCategoryIdentifiers</code> attribute.
     *
     * @param value the value of the <code>requiredCategoryIdentifiers</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRequiredCategoryIdentifiers(String value)
    {
        try
        {
            if(value != null)
            {
                set(requiredCategoryIdentifiersDef, value);
            }
            else
            {
                unset(requiredCategoryIdentifiersDef);
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
	 * Checks if the value of the <code>requiredCategoryIdentifiers</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>requiredCategoryIdentifiers</code> attribute is defined.
	 */
    public boolean isRequiredCategoryIdentifiersDefined()
	{
	    return isDefined(requiredCategoryIdentifiersDef);
	}
 
    /**
     * Returns the value of the <code>requiredCategoryPaths</code> attribute.
     *
     * @return the value of the <code>requiredCategoryPaths</code> attribute.
     */
    public String getRequiredCategoryPaths()
    {
        return (String)get(requiredCategoryPathsDef);
    }
    
    /**
     * Returns the value of the <code>requiredCategoryPaths</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>requiredCategoryPaths</code> attribute.
     */
    public String getRequiredCategoryPaths(String defaultValue)
    {
        if(isDefined(requiredCategoryPathsDef))
        {
            return (String)get(requiredCategoryPathsDef);
        }
        else
        {
            return defaultValue;
        }
    }    

    /**
     * Sets the value of the <code>requiredCategoryPaths</code> attribute.
     *
     * @param value the value of the <code>requiredCategoryPaths</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setRequiredCategoryPaths(String value)
    {
        try
        {
            if(value != null)
            {
                set(requiredCategoryPathsDef, value);
            }
            else
            {
                unset(requiredCategoryPathsDef);
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
	 * Checks if the value of the <code>requiredCategoryPaths</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>requiredCategoryPaths</code> attribute is defined.
	 */
    public boolean isRequiredCategoryPathsDefined()
	{
	    return isDefined(requiredCategoryPathsDef);
	}

    /**
     * Returns the value of the <code>simpleQuery</code> attribute.
     *
     * @return the value of the <code>simpleQuery</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getSimpleQuery()
        throws IllegalStateException
    {
        if(isDefined(simpleQueryDef))
        {
            return ((Boolean)get(simpleQueryDef)).booleanValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute simpleQuery is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>simpleQuery</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>simpleQuery</code> attribute.
     */
    public boolean getSimpleQuery(boolean defaultValue)
    {
        if(isDefined(simpleQueryDef))
        {
            return ((Boolean)get(simpleQueryDef)).booleanValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>simpleQuery</code> attribute.
     *
     * @param value the value of the <code>simpleQuery</code> attribute.
     */
    public void setSimpleQuery(boolean value)
    {
        try
        {
            set(simpleQueryDef, new Boolean(value));
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
     * Removes the value of the <code>simpleQuery</code> attribute.
     */
    public void unsetSimpleQuery()
    {
        try
        {
            unset(simpleQueryDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>simpleQuery</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>simpleQuery</code> attribute is defined.
	 */
    public boolean isSimpleQueryDefined()
	{
	    return isDefined(simpleQueryDef);
	}

    /**
     * Returns the value of the <code>useIdsAsIdentifiers</code> attribute.
     *
     * @return the value of the <code>useIdsAsIdentifiers</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public boolean getUseIdsAsIdentifiers()
        throws IllegalStateException
    {
        if(isDefined(useIdsAsIdentifiersDef))
        {
            return ((Boolean)get(useIdsAsIdentifiersDef)).booleanValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute useIdsAsIdentifiers is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>useIdsAsIdentifiers</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>useIdsAsIdentifiers</code> attribute.
     */
    public boolean getUseIdsAsIdentifiers(boolean defaultValue)
    {
        if(isDefined(useIdsAsIdentifiersDef))
        {
            return ((Boolean)get(useIdsAsIdentifiersDef)).booleanValue();
        }
        else
        {
            return defaultValue;
        }
    }

    /**
     * Sets the value of the <code>useIdsAsIdentifiers</code> attribute.
     *
     * @param value the value of the <code>useIdsAsIdentifiers</code> attribute.
     */
    public void setUseIdsAsIdentifiers(boolean value)
    {
        try
        {
            set(useIdsAsIdentifiersDef, new Boolean(value));
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
     * Removes the value of the <code>useIdsAsIdentifiers</code> attribute.
     */
    public void unsetUseIdsAsIdentifiers()
    {
        try
        {
            unset(useIdsAsIdentifiersDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>useIdsAsIdentifiers</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>useIdsAsIdentifiers</code> attribute is defined.
	 */
    public boolean isUseIdsAsIdentifiersDefined()
	{
	    return isDefined(useIdsAsIdentifiersDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////

    // @import java.util.StringTokenizer
    // @import java.util.ArrayList

	public String[] getAcceptedResourceClassNames()
	{
		return tokenize(getAcceptedResourceClasses(), " ");
	}

	public String[] getAcceptedSiteNames()
	{
		return tokenize(getAcceptedSites(), ",");
	}

	public void setAcceptedSiteNames(String[] names)
	{
		setAcceptedSites(concat(names, ","));
	}

	private String concat(String[] tokens, String sep)
	{
		StringBuilder buffer = new StringBuilder(64);
		for (int j = 0; j < tokens.length; j++)
		{
			if(j > 0)
			{
				buffer.append(sep);
			}
			buffer.append(tokens[j]);
		}
		return buffer.toString();
	}

	private String[] tokenize(String str, String sep)
	{
		if(str != null && str.length() > 0)
		{
			StringTokenizer tokenizer = new StringTokenizer(str, sep);
			ArrayList list = new ArrayList(8);
			while(tokenizer.hasMoreTokens())
			{
				list.add(tokenizer.nextToken());
			}
			return (String[]) list.toArray(new String[list.size()]);
		}
		return new String[0];
	}
}
