package net.cyklotron.cms.poll;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.labeo.Labeo;
import net.labeo.services.resource.AttributeDefinition;
import net.labeo.services.resource.BackendException;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.ModificationNotPermitedException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceClass;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.resource.generic.NodeResource;
import net.labeo.services.resource.generic.NodeResourceImpl;

/**
 * An implementation of <code>cms.poll.result</code> ARL resource class.
 *
 * @author MakeWrappers tool
 */
public class ResultResourceImpl
    extends NodeResourceImpl
    implements ResultResource
{
    /** The AttributeDefinition object for the <code>answers</code> attribute. */
    private AttributeDefinition answersDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>cms.poll.result</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param rs the ResourceService.
     */
    public ResultResourceImpl()
    {
        super();
        try
        {
            ResourceClass rc = rs.getSchema().getResourceClass("cms.poll.result");
            answersDef = rc.getAttribute("answers");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>cms.poll.result</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param rs the ResourceService
     * @param id the id of the object to be retrieved
     */
    public static ResultResource getResultResource(ResourceService rs, long id)
        throws EntityDoesNotExistException
    {
        Resource res = rs.getStore().getResource(id);
        if(!(res instanceof ResultResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not cms.poll.result");
        }
        return (ResultResource)res;
    }

    /**
     * Creates a new <code>cms.poll.result</code> resource instance.
     *
     * @param rs the ResourceService
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param subject the subject that creates the resource
     * @return a new ResultResource instance.
     */
    public static ResultResource createResultResource(ResourceService rs, String name, Resource parent, Subject subject)
        throws ValueRequiredException
    {
        try
        {
            ResourceClass rc = rs.getSchema().getResourceClass("cms.poll.result");
            Map attrs = new HashMap();
            Resource res = rs.getStore().createResource(name, parent, rc, attrs, subject);
            if(!(res instanceof ResultResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (ResultResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>answers</code> attribute.
     *
     * @return the value of the <code>answers</code> attribute.
     */
    public List getAnswers()
    {
        return (List)get(answersDef);
    }

    /**
     * Sets the value of the <code>answers</code> attribute.
     *
     * @param value the value of the <code>answers</code> attribute,
     *        or <code>null</code> to remove value.
     */
    public void setAnswers(List value)
    {
        try
        {
            if(value != null)
            {
                set(answersDef, value);
            }
            else
            {
                unset(answersDef);
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
     
    // @custom methods ///////////////////////////////////////////////////////
}
