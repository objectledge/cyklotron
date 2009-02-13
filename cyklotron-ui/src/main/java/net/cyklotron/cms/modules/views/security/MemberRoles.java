package net.cyklotron.cms.modules.views.security;

import static net.cyklotron.cms.modules.views.security.MemberAssignments.getAssignedRoles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.RoleImplication;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.table.comparator.NameComparator;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.security.BaseRoleScreen.PathTool;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.security.SecurityService;

public class MemberRoles
    extends BaseSecurityScreen
{

    public MemberRoles(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        SecurityService securityService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, securityService);
    }

    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext context,
        CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            long subjectId = parameters.getLong("subject_id");
            Subject subject = coralSession.getSecurity().getSubject(subjectId);
            templatingContext.put("roleInfo", getImpliedRoleInfo(subject));
            templatingContext.put("security", new SecurityServiceHelper(cmsSecurityService, coralSession));
            templatingContext.put("path_tool", new PathTool(getSite()));
        }
        catch(Exception e)
        {
            throw new ProcessingException("data access failed", e);
        }            
    }

    private Map<Role, List<List<Role>>> getImpliedRoleInfo(Subject subject)
    {
        // make key ordering stable, even if a bit arbitrary
        Map<Role, List<List<Role>>> info = new TreeMap<Role, List<List<Role>>>(
            new NameComparator<Role>(Locale.getDefault()));
        // start with direct assignments
        Set<Role> roles = getAssignedRoles(subject);
        for (Role role : roles)
        {
            registerRolePath(info, role, new LinkedList<Role>());
        }
        for (List<List<Role>> pathList : info.values())
        {
            // sort paths for a role by length
            Collections.sort(pathList, new Comparator<List<Role>>() 
                    {
                        public int compare(List<Role> l1, List<Role> l2)
                        {
                            return l1.size() - l2.size();
                        }
                    });
        }
        return info;
    }

    private void registerRolePath(Map<Role, List<List<Role>>> info, Role role, Deque<Role> path)
    {
        List<List<Role>> pathList = getPathList(info, role);
        // make an copy of the path (which is mutable)        
        ArrayList<Role> pathCopy = new ArrayList<Role>(path);
        Collections.reverse(pathCopy);
        pathList.add(pathCopy); 
        path.push(role);
        for (RoleImplication impl : role.getImplications())
        {
            if(impl.getSuperRole().equals(role))
            {
                registerRolePath(info, impl.getSubRole(), path);
            }
        }
        path.pop();
    }

    private List<List<Role>> getPathList(Map<Role, List<List<Role>>> info, Role role)
    {
        List<List<Role>> pathList = info.get(role);
        if(pathList == null)
        {
            pathList = new ArrayList<List<Role>>();
            info.put(role, pathList);
        }
        return pathList;
    }
}
