package net.cyklotron.cms.modules.actions.security;

import java.util.HashSet;

import net.labeo.services.authentication.UnknownUserException;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.Subject;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;

public class AddMember
    extends BaseSecurityAction
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException, NotFoundException
    {
        String login = parameters.get("login","");
        String dn = null;
        if(login.length() == 0)
        {
            data.getContext().put("result", "member_name_empty");
        }
        else
        {
            try
            {
                dn = authenticationService.getUserByLogin(login).getName();
            }
            catch(UnknownUserException e)
            {
                data.getContext().put("result", "member_name_invalid");
            }
        }
        if(dn != null)
        {
            try
            {
                Subject subject = coralSession.getSecurity().getSubject(dn);
                SiteResource site = getSite(context);
                Role teamMember = site.getTeamMember();
                if(subject.hasRole(teamMember))
                {
                    data.getContext().put("result","already_member");
                }
                else
                {
                    Subject root = coralSession.getSecurity().getSubject(Subject.ROOT);
                    coralSession.getSecurity().grant(teamMember, subject, false, root);
                    Parameter[] selectedRoleIds = parameters.getArray("selected_role_id");
                    for(int i=0; i<selectedRoleIds.length; i++)
                    {
                        Role role = coralSession.getSecurity().getRole(selectedRoleIds[i].asLong());
                        coralSession.getSecurity().grant(role, subject, false, root);
                    }
                }
            }
            catch(Exception e)
            {
                // log.error("AddMember", e);
                data.getContext().put("result", "exception");
                data.getContext().put("trace", new StackTrace(e));
                return;
            }
        }
        if(!data.getContext().containsKey("result"))
        {
            data.getContext().put("result", "added_successfully");
        }
        else
        {
            mvcContext.setView("security,AddMember");
            data.getContext().put("login", login);
            try
            {
                HashSet selected = new HashSet();
                Parameter[] selectedRoleIds = parameters.getArray("selected_role_id");
                for(int i=0; i<selectedRoleIds.length; i++)
                {
                    selected.add(coralSession.getSecurity().getRole(selectedRoleIds[i].asLong()));
                }
                data.getContext().put("selected", selected);
            }
            catch(Exception e)
            {
                // log.error("AddMember: bad role ids", e)
            }
        }
    }
}
