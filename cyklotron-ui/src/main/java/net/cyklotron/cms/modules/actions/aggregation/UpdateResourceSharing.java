package net.cyklotron.cms.modules.actions.aggregation;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.PermissionAssignment;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: UpdateResourceSharing.java,v 1.4 2005-05-20 05:32:45 pablo Exp $
 */
public class UpdateResourceSharing extends BaseAggregationAction
{ 

    
    public UpdateResourceSharing(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SiteService siteService,
        AggregationService aggregationService)
    {
        super(logger, structureService, cmsDataFactory, siteService, aggregationService);
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Subject subject = coralSession.getUserSubject();
        long resourceId = parameters.getLong("res_id", -1);
        String[] keys = parameters.getParameterNames();
        
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
                        coralSession.getSecurity().revoke(resource,role,permission);
                    }
                    if(value.equals("grant"))
                    {
                        if(assignment == null)
                        {
                            coralSession.getSecurity().grant(resource, role, permission, false);
                            continue;
                        }
                        if(assignment.isInherited())
                        {
                            coralSession.getSecurity().revoke(resource, role, permission);
                            coralSession.getSecurity().grant(resource, role, permission, false);
                            continue;
                        }
                    }
                    if(value.equals("recursive"))
                    {
                        if(assignment == null)
                        {
                            coralSession.getSecurity().grant(resource, role, permission, true);
                            continue;
                        }
                        if(!assignment.isInherited())
                        {
                            coralSession.getSecurity().revoke(resource, role, permission);
                            coralSession.getSecurity().grant(resource, role, permission, true);
                            continue;
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            logger.error("AggregationException: ",e);
            return;
        }
        templatingContext.put("result","updated_successfully");
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
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
