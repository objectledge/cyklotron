package net.cyklotron.cms.modules.actions.fixes;

import java.util.Arrays;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.SecurityException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.structure.BaseStructureAction;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailto:zwierzem@ngo.pl">Damian Gajda</a>
 * @version $Id$
 */
public class CYKLO435 extends BaseStructureAction
{
    public CYKLO435(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService)
    {
        super(logger, structureService, cmsDataFactory, styleService);
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        // add permission if missing
        Permission permission;
        try
        {
            permission = coralSession.getSecurity().getUniquePermission(
                "cms.structure.prioritize_any");            
        }
        catch(IllegalStateException e)
        {
            permission = coralSession.getSecurity()
                .createPermission("cms.structure.prioritize_any");
        }        
        // add permission to resource class if missing
        ResourceClass rClass;
        try
        {
            rClass = coralSession.getSchema().getResourceClass(
                "structure.navigation_node");
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("data integrity error", e);
        }
        List l = Arrays.asList(rClass.getPermissions());
        if(!l.contains(permission))
        {
            coralSession.getSecurity().addPermission(rClass, permission);
        }
        Role[] roles = coralSession.getSecurity().getRole();
        loop: for(Role role : roles)
        {
            String name = role.getName();
            int pos = name.lastIndexOf('.');
            if(pos < 0)
            {
                continue loop;
            }
            String prefix = name.substring(0, pos);
            if(!prefix.matches("cms\\.structure\\.(administrator|editor)"))
            {
                continue loop;
            }
            String suffix = name.substring(pos + 1);
            if(!suffix.matches("\\d+"))
            {
                continue loop;
            }
            long id = Long.parseLong(suffix);
            try
            {
                Resource subtreeRoot = coralSession.getStore().getResource(id);
                coralSession.getSecurity().grant(subtreeRoot, role, permission, true);
            }
            catch(EntityDoesNotExistException e)
            {
                // resource is missing - ignore
                continue loop;
            }
            catch(SecurityException e)
            {
                throw new ProcessingException("could not grant permission", e);
            }
        }
        templatingContext.put("result", "success");
    }
    
    /**
     * @{inheritDoc}
     */
    public boolean checkAccessRights(Context context)
        throws Exception
    {
        CoralSession coralSession = context.getAttribute(CoralSession.class);
        return (coralSession.getUserSubject().getId() == Subject.ROOT);
    }
}
