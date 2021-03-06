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
 
package net.cyklotron.cms.files;

import java.io.IOException;
import java.io.InputStream;

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
 * An implementation of <code>cms.files.file</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class FileResourceImpl
    extends ItemResourceImpl
    implements FileResource
{
    // class variables /////////////////////////////////////////////////////////

    /** Class variables initialization status. */
	@SuppressWarnings("unused")
    private static boolean definitionsInitialized;
	
    /** The AttributeDefinition object for the <code>encoding</code> attribute. */
	private static AttributeDefinition<String> encodingDef;

    /** The AttributeDefinition object for the <code>locale</code> attribute. */
	private static AttributeDefinition<String> localeDef;

    /** The AttributeDefinition object for the <code>mimetype</code> attribute. */
	private static AttributeDefinition<String> mimetypeDef;

    /** The AttributeDefinition object for the <code>size</code> attribute. */
    private static AttributeDefinition<Long> sizeDef;

	// custom injected fields /////////////////////////////////////////////////
	
    /** The FilesService. */
    protected FilesService filesService;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.files.file</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param filesService the FilesService.
     */
    public FileResourceImpl(FilesService filesService)
    {
        this.filesService = filesService;
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.files.file</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param session the CoralSession
     * @param id the id of the object to be retrieved
     * @return a resource instance.
     * @throws EntityDoesNotExistException if the resource with the given id does not exist.
     */
    public static FileResource getFileResource(CoralSession session, long id)
        throws EntityDoesNotExistException
    {
        Resource res = session.getStore().getResource(id);
        if(!(res instanceof FileResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.files.file");
        }
        return (FileResource)res;
    }

    /**
     * Creates a new <code>cms.files.file</code> resource instance.
     *
     * @param session the CoralSession
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @return a new FileResource instance.
     * @throws InvalidResourceNameException if the name argument contains illegal characters.
     */
    public static FileResource createFileResource(CoralSession session, String name, Resource
        parent)
        throws InvalidResourceNameException
    {
        try
        {
            ResourceClass<FileResource> rc = session.getSchema().getResourceClass("cms.files.file", FileResource.class);
		    Resource res = session.getStore().createResource(name, parent, rc,
                java.util.Collections.<AttributeDefinition<?>, Object> emptyMap());			
            if(!(res instanceof FileResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (FileResource)res;
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
     * Returns the value of the <code>encoding</code> attribute.
     *
     * @return the value of the <code>encoding</code> attribute.
     */
    public String getEncoding()
    {
        return get(encodingDef);
    }
    
    /**
     * Returns the value of the <code>encoding</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>encoding</code> attribute.
     */
    public String getEncoding(String defaultValue)
    {
        return get(encodingDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>encoding</code> attribute.
     *
     * @param value the value of the <code>encoding</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setEncoding(String value)
    {
        try
        {
            if(value != null)
            {
                set(encodingDef, value);
            }
            else
            {
                unset(encodingDef);
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
	 * Checks if the value of the <code>encoding</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>encoding</code> attribute is defined.
	 */
    public boolean isEncodingDefined()
	{
	    return isDefined(encodingDef);
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
     * Returns the value of the <code>locale</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>locale</code> attribute.
     */
    public String getLocale(String defaultValue)
    {
        return get(localeDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>locale</code> attribute.
     *
     * @param value the value of the <code>locale</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setLocale(String value)
    {
        try
        {
            if(value != null)
            {
                set(localeDef, value);
            }
            else
            {
                unset(localeDef);
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
	 * Checks if the value of the <code>locale</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>locale</code> attribute is defined.
	 */
    public boolean isLocaleDefined()
	{
	    return isDefined(localeDef);
	}
 
    /**
     * Returns the value of the <code>mimetype</code> attribute.
     *
     * @return the value of the <code>mimetype</code> attribute.
     */
    public String getMimetype()
    {
        return get(mimetypeDef);
    }
    
    /**
     * Returns the value of the <code>mimetype</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>mimetype</code> attribute.
     */
    public String getMimetype(String defaultValue)
    {
        return get(mimetypeDef, defaultValue);
    }    

    /**
     * Sets the value of the <code>mimetype</code> attribute.
     *
     * @param value the value of the <code>mimetype</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setMimetype(String value)
    {
        try
        {
            if(value != null)
            {
                set(mimetypeDef, value);
            }
            else
            {
                unset(mimetypeDef);
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
	 * Checks if the value of the <code>mimetype</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>mimetype</code> attribute is defined.
	 */
    public boolean isMimetypeDefined()
	{
	    return isDefined(mimetypeDef);
	}

    /**
     * Returns the value of the <code>size</code> attribute.
     *
     * @return the value of the <code>size</code> attribute.
     * @throws IllegalStateException if the value of the attribute is 
     *         undefined.
     */
    public long getSize()
        throws IllegalStateException
    {
	    Long value = get(sizeDef);
        if(value != null)
        {
            return value.longValue();
        }
        else
        {
            throw new IllegalStateException("value of attribute size is undefined"+
			    " for resource #"+getId());
        }
    }

    /**
     * Returns the value of the <code>size</code> attribute.
     *
     * @param defaultValue the value to return if the attribute is undefined.
     * @return the value of the <code>size</code> attribute.
     */
    public long getSize(long defaultValue)
    {
		return get(sizeDef, Long.valueOf(defaultValue)).longValue();
	}

    /**
     * Sets the value of the <code>size</code> attribute.
     *
     * @param value the value of the <code>size</code> attribute.
     */
    public void setSize(long value)
    {
        try
        {
            set(sizeDef, Long.valueOf(value));
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
     * Removes the value of the <code>size</code> attribute.
     */
    public void unsetSize()
    {
        try
        {
            unset(sizeDef);
        }
        catch(ValueRequiredException e)
        {
            throw new BackendException("incompatible schema change",e);
        }     
    } 
   
	/**
	 * Checks if the value of the <code>size</code> attribute is defined.
	 *
	 * @return <code>true</code> if the value of the <code>size</code> attribute is defined.
	 */
    public boolean isSizeDefined()
	{
	    return isDefined(sizeDef);
	}
  
    // @custom methods ///////////////////////////////////////////////////////
    // @extends cms.files.item
    // @import java.io.InputStream
    // @import java.io.IOException
    // @import net.cyklotron.cms.files.FilesService
    // @field FilesService filesService
    
    public InputStream getInputStream()
    {
        return filesService.getInputStream(this);
    }
    
    public String getIndexContent()
    {
        return filesService.extractContent(this);
    }
}
