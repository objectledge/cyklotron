package net.cyklotron.cms.structure;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.style.StyleResource;
import net.cyklotron.services.workflow.StateResource;
import net.labeo.Labeo;
import net.labeo.services.resource.AttributeDefinition;
import net.labeo.services.resource.BackendException;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.ModificationNotPermitedException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.ResourceClass;
import net.labeo.services.resource.ResourceService;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.util.configuration.ParameterContainer;

/**
 * An implementation of <code>structure.link_node</code> ARL resource class.
 *
 * @author MakeWrappers tool
 */
public class LinkNodeResourceImpl
    extends NavigationNodeResourceImpl
    implements LinkNodeResource
{
    /** The AttributeDefinition object for the <code>target</code> attribute. */
    private AttributeDefinition targetDef;

    // initialization /////////////////////////////////////////////////////////

    /**
     * Creates a blank <code>structure.link_node</code> resource wrapper.
     *
     * <p>This constructor should be used by the handler class only. Use 
     * <code>load()</code> and <code>create()</code> methods to create
     * instances of the wrapper in your application code.</p>
     *
     * @param rs the ResourceService.
     */
    public LinkNodeResourceImpl()
    {
        super();
        try
        {
            ResourceClass rc = rs.getSchema().getResourceClass("structure.link_node");
            targetDef = rc.getAttribute("target");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // static methods ////////////////////////////////////////////////////////

    /**
     * Retrieves a <code>structure.link_node</code> resource instance from the store.
     *
     * <p>This is a simple wrapper of StoreService.getResource() method plus
     * the typecast.</p>
     *
     * @param rs the ResourceService
     * @param id the id of the object to be retrieved
     */
    public static LinkNodeResource getLinkNodeResource(ResourceService rs, long id)
        throws EntityDoesNotExistException
    {
        Resource res = rs.getStore().getResource(id);
        if(!(res instanceof LinkNodeResource))
        {
            throw new IllegalArgumentException("resource #"+id+" is "+
                                               res.getResourceClass().getName()+
                                               " not structure.link_node");
        }
        return (LinkNodeResource)res;
    }

    /**
     * Creates a new <code>structure.link_node</code> resource instance.
     *
     * @param rs the ResourceService
     * @param name the name of the new resource
     * @param parent the parent resource.
     * @param title the title attribute
     * @param target the target attribute
     * @param preferences the preferences attribute
     * @param site the site attribute
     * @param subject the subject that creates the resource
     * @return a new LinkNodeResource instance.
     */
    public static LinkNodeResource createLinkNodeResource(ResourceService rs, String name, Resource parent, String title, String target, ParameterContainer preferences, SiteResource site, Subject subject)
        throws ValueRequiredException
    {
        try
        {
            ResourceClass rc = rs.getSchema().getResourceClass("structure.link_node");
            Map attrs = new HashMap();
            attrs.put(rc.getAttribute("title"), title);
            attrs.put(rc.getAttribute("target"), target);
            attrs.put(rc.getAttribute("preferences"), preferences);
            attrs.put(rc.getAttribute("site"), site);
            Resource res = rs.getStore().createResource(name, parent, rc, attrs, subject);
            if(!(res instanceof LinkNodeResource))
            {
                throw new BackendException("incosistent schema: created object is "+
                                           res.getClass().getName());
            }
            return (LinkNodeResource)res;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new BackendException("incompatible schema change", e);
        }
    }

    // public interface //////////////////////////////////////////////////////
 
    /**
     * Returns the value of the <code>target</code> attribute.
     *
     * @return the value of the <code>target</code> attribute.
     */
    public String getTarget()
    {
        return (String)get(targetDef);
    }
 
    /**
     * Sets the value of the <code>target</code> attribute.
     *
     * @param value the value of the <code>target</code> attribute.
     * @throws ValueRequiredException if you attempt to set a <code>null</code> 
     *         value.
     */
    public void setTarget(String value)
        throws ValueRequiredException
    {
        try
        {
            if(value != null)
            {
                set(targetDef, value);
            }
            else
            {
                throw new ValueRequiredException("attribute target "+
                                                 "is declared as REQUIRED");
            }
        }
        catch(ModificationNotPermitedException e)
        {
            throw new BackendException("incompatible schema change",e);
        }
    }
     
    // @custom methods ///////////////////////////////////////////////////////

    // @order title, target, preferences, site
}
