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
import net.cyklotron.cms.security.SecurityService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureService;

public class AddMember
    extends BaseSecurityAction
{

    private final CoralSessionFactory sessionFactory;    
    
    public AddMember(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SecurityService cmsSecurityService, UserManager userManager,
        CoralSessionFactory sessionFactory)
    {
        super(logger, structureService, cmsDataFactory, cmsSecurityService, userManager);
        this.sessionFactory = sessionFactory; 
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        String login = parameters.get("login","");
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
                Subject subject = cmsSecurityService.getSubject(coralSession, dn);
                SiteResource site = getSite(context);
                Role teamMember = site.getTeamMember();
                if(subject.hasRole(teamMember))
                {
                    templatingContext.put("result","already_member");
                }
                else
                {
                    CoralSession rootSession = sessionFactory.getRootSession();
                    try {
                        rootSession.getSecurity().grant(teamMember, subject, false);
                        long[] selectedRoleIds = parameters.getLongs("selected_role_id");
                        for(int i=0; i<selectedRoleIds.length; i++)
                        {
                            Role role = rootSession.getSecurity().getRole(selectedRoleIds[i]);
                            rootSession.getSecurity().grant(role, subject, false);
                        }
                        long[] selectedGroupIds = parameters.getLongs("selected_group_id");
                        for(int i=0; i<selectedGroupIds.length; i++)
                        {
                            Role groupRole = rootSession.getSecurity().getRole(selectedGroupIds[i]);
                            rootSession.getSecurity().grant(groupRole, subject, false);
                        }
                    } finally {
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
            mvcContext.setView("security.AddMember");
            templatingContext.put("login", login);
            try
            {
                HashSet selected = new HashSet();
                long[] selectedRoleIds = parameters.getLongs("selected_role_id");
                for(int i=0; i<selectedRoleIds.length; i++)
                {
                    selected.add(coralSession.getSecurity().getRole(selectedRoleIds[i]));
                }
                templatingContext.put("selected", selected);
            }
            catch(Exception e)
            {
                // log.error("AddMember: bad role ids", e)
            }
        }
    }
}
