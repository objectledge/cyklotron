package net.cyklotron.cms.modules.actions.security;

import java.util.HashSet;

import org.jcontainer.dna.Logger;
import org.objectledge.authentication.UserManager;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.entity.EntityExistsException;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.SecurityException;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.security.RoleResource;
import net.cyklotron.cms.security.RoleResourceImpl;
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureService;

public class AddGroupMember
    extends BaseSecurityAction
{

    private final CoralSessionFactory sessionFactory;

    public AddGroupMember(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SecurityService cmsSecurityService, UserManager userManager,
        CoralSessionFactory sessionFactory)
    {
        super(logger, structureService, cmsDataFactory, cmsSecurityService, userManager);
        this.sessionFactory = sessionFactory;
    }

    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        String login = parameters.get("login", "");
        String dn = null;
        if(login.length() == 0)
        {
            templatingContext.put("result", "member_name_empty");
        }
        else
        {
            try
            {
                dn = userManager.getUserByLogin(login).getName();
            }
            catch(Exception e)
            {
                templatingContext.put("result", "member_name_invalid");
            }
        }
        if(dn != null)
        {
            try
            {
                Subject subject = getSubject(coralSession, dn);
                long groupId = parameters.getLong("group_id");
                RoleResource group = RoleResourceImpl.getRoleResource(coralSession, groupId);
                if(subject.hasRole(group.getRole()))
                {
                    templatingContext.put("result", "already_member");
                }
                else
                {
                    CoralSession rootSession = sessionFactory.getRootSession();
                    try
                    {
                        rootSession.getSecurity().grant(group.getRole(), subject, false);
                    }
                    finally
                    {
                        rootSession.close();
                    }
                }
            }
            catch(Exception e)
            {
                // log.error("AddMember", e);
                templatingContext.put("result", "exception");
                templatingContext.put("trace", new StackTrace(e));
                return;
            }
        }
        if(!templatingContext.containsKey("result"))
        {
            templatingContext.put("result", "added_successfully");
        }
        else
        {
            mvcContext.setView("security.AddGroupMember");
            templatingContext.put("login", login);
        }
    }

    /**
     * Return the Subject entry for the named user, or create it if necessary.
     * 
     * @param coralSession the coral session.
     * @param dn user's Distinguished Name.
     * @return Subject object.
     */
    private Subject getSubject(CoralSession coralSession, String dn)
    {
        try
        {
            return coralSession.getSecurity().getSubject(dn);
        }
        catch(EntityDoesNotExistException e)
        {
            // dn value came from UserManager.getUserBySubject(), so apparently the user is
            // present in the directory, but is missing a Coral Subject entry. Let's create it.
            try
            {
                Subject subject = coralSession.getSecurity().createSubject(dn);
                Role role = coralSession.getSecurity().getUniqueRole("cms.registered");
                coralSession.getSecurity().grant(role, subject, false);
                return subject;
            }
            catch(EntityExistsException ee)
            {
                throw new IllegalArgumentException("internal error", ee);
            }
            catch(SecurityException ee)
            {
                throw new IllegalArgumentException("internal error", ee);
            }
        }
    }
}
