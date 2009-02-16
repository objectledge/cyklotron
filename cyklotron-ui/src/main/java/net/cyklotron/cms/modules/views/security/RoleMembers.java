package net.cyklotron.cms.modules.views.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.RoleAssignment;
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

public class RoleMembers extends BaseSecurityScreen
{
    public RoleMembers(Context context, Logger logger, PreferencesService preferencesService,
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
            long roleId = parameters.getLong("role_id");
            Role role = coralSession.getSecurity().getRole(roleId);
            Map<Subject, List<List<Role>>> impliedRoleInfo = getImpliedRoleInfo(role);
            filterImpliedRoleInfo(parameters, templatingContext, coralSession, impliedRoleInfo); 
            templatingContext.put("roleRes", cmsSecurityService.getRole(coralSession,  getSite(), role));
            templatingContext.put("roleInfo", impliedRoleInfo);
            templatingContext.put("security", new SecurityServiceHelper(cmsSecurityService, coralSession));
            templatingContext.put("path_tool", new PathTool(getSite()));
        }
        catch(Exception e)
        {
            throw new ProcessingException("data access failed", e);
        }  
    }

    private Map<Subject, List<List<Role>>> getImpliedRoleInfo(Role role)
    {
        // make key ordering stable, even if a bit arbitrary
        Map<Subject, List<List<Role>>> info = new TreeMap<Subject, List<List<Role>>>(
            new NameComparator<Subject>(Locale.getDefault()));
        // start with direct assignments of this role
        registerRolePath(info, role, new LinkedList<Role>());
        for (List<List<Role>> pathList : info.values())
        {
            // sort paths for a subject by length
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
    
    private void registerRolePath(Map<Subject, List<List<Role>>> info, Role role, Deque<Role> path)
    {
        path.push(role);
        // register the path from all subjects that are directly assigned this role
        for(RoleAssignment assignment: role.getRoleAssignments())
        {
            List<List<Role>> pathList = getPathList(info, assignment.getSubject());
            // make an copy of the path (which is mutable)        
            ArrayList<Role> pathCopy = new ArrayList<Role>(path);
            pathList.add(pathCopy);             
        }
        
        // iterate over direct super role of this role
        for (RoleImplication impl : role.getImplications())
        {
            if(impl.getSubRole().equals(role))
            {
                registerRolePath(info, impl.getSuperRole(), path);
            }
        }
        path.pop();
    }
    
    public static <K> List<List<Role>> getPathList(Map<K, List<List<Role>>> info, K key)
    {
        List<List<Role>> pathList = info.get(key);
        if(pathList == null)
        {
            pathList = new ArrayList<List<Role>>();
            info.put(key, pathList);
        }
        return pathList;
    }
    
    public static <K> void filterImpliedRoleInfo(Map<K, List<List<Role>>> info, Role filteredOut)
    {
        for(List<List<Role>> pathList : info.values())
        {
            for(Iterator<List<Role>> pathIterator = pathList.iterator(); pathIterator.hasNext();)
            {
                if(pathIterator.next().contains(filteredOut))
                {
                    pathIterator.remove();
                }
            }
        }
    }

    public static <K> void filterImpliedRoleInfo(Parameters parameters, TemplatingContext templatingContext,
        CoralSession coralSession, Map<K, List<List<Role>>> impliedRoleInfo)
    {
        boolean showRegistered = parameters.getBoolean("show_registered", false);
        boolean showEveryone = parameters.getBoolean("show_everyone", false);
        if(!showRegistered)
        {
            filterImpliedRoleInfo(impliedRoleInfo, coralSession.getSecurity().getUniqueRole("cms.registered"));
        }
        if(!showEveryone)
        {
            filterImpliedRoleInfo(impliedRoleInfo, coralSession.getSecurity().getUniqueRole("cms.everyone"));
        }
    }
}
