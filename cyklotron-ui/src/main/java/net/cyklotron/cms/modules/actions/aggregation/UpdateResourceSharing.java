package net.cyklotron.cms.modules.actions.aggregation;

import net.labeo.Labeo;
import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Permission;
import net.labeo.services.resource.PermissionAssignment;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.SecurityException;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteResourceImpl;

/**
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: UpdateResourceSharing.java,v 1.1 2005-01-24 04:35:18 pablo Exp $
 */
public class UpdateResourceSharing extends BaseAggregationAction
{ 
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();
        long resourceId = parameters.getLong("res_id", -1);
        String[] keys = parameters.getKeys();
        
        try
        {
            Subject rootSubject = coralSession.getSecurity().getSubject(Subject.ROOT);

            Permission permission = coralSession.getSecurity().getUniquePermission("cms.aggregation.import");
            Resource resource = coralSession.getStore().getResource(resourceId);
            
            for(int i = 0; i < keys.length; i++)
            {
                if(keys[i].startsWith("share_"))
                {
                    long id = Long.parseLong(keys[i].substring(6));
                    Role role = coralSession.getSecurity().getRole(id);
                    PermissionAssignment[] assignments = role.getPermissionAssignments(resource);
                    PermissionAssignment assignment = null;
                    for(int j = 0; j < assignments.length; j++)
                    {
                        if(assignments[j].getPermission().equals(permission))
                        {
                            assignment = assignments[j];
                            break;
                        }
                    }
                    String value = parameters.get("share_"+id,"");
                    if(value.equals("revoke") && assignment != null)
                    {
                        coralSession.getSecurity().revoke(resource,role,permission, rootSubject);
                    }
                    if(value.equals("grant"))
                    {
                        if(assignment == null)
                        {
                            coralSession.getSecurity().grant(resource, role, permission, false, rootSubject);
                            continue;
                        }
                        if(assignment.isInherited())
                        {
                            coralSession.getSecurity().revoke(resource, role, permission, rootSubject);
                            coralSession.getSecurity().grant(resource, role, permission, false, rootSubject);
                            continue;
                        }
                    }
                    if(value.equals("recursive"))
                    {
                        if(assignment == null)
                        {
                            coralSession.getSecurity().grant(resource, role, permission, true, rootSubject);
                            continue;
                        }
                        if(!assignment.isInherited())
                        {
                            coralSession.getSecurity().revoke(resource, role, permission, rootSubject);
                            coralSession.getSecurity().grant(resource, role, permission, true, rootSubject);
                            continue;
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("AggregationException: ",e);
            return;
        }
        templatingContext.put("result","updated_successfully");
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        try
        {
            SiteResource node = getSite(context);
            Permission permission = coralSession.getSecurity()
                .getUniquePermission("cms.aggregation.export");
            return coralSession.getUserSubject().hasPermission(node, permission);
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to check security", e);
        }
    }
}
