package net.cyklotron.cms.modules.actions.aggregation;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Permission;
import org.objectledge.coral.security.PermissionAssignment;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.session.CoralSessionFactory;
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
 * @version $Id: UpdateResourceSharing.java,v 1.5 2006-02-08 15:57:16 pablo Exp $
 */
public class UpdateResourceSharing extends BaseAggregationAction
{ 
    private CoralSessionFactory coralSessionFactory;
    
    public UpdateResourceSharing(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SiteService siteService,
        AggregationService aggregationService, CoralSessionFactory coralSessionFactory)
    {
        super(logger, structureService, cmsDataFactory, siteService, aggregationService);
        this.coralSessionFactory = coralSessionFactory;
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
        CoralSession rootSession = coralSessionFactory.getRootSession();
        try
        {
            Subject rootSubject = rootSession.getSecurity().getSubject(Subject.ROOT);

            Permission permission = rootSession.getSecurity().getUniquePermission("cms.aggregation.import");
            Resource resource = rootSession.getStore().getResource(resourceId);
            
            for(int i = 0; i < keys.length; i++)
            {
                if(keys[i].startsWith("share_"))
                {
                    long id = Long.parseLong(keys[i].substring(6));
                    Role role = rootSession.getSecurity().getRole(id);
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
                        rootSession.getSecurity().revoke(resource,role,permission);
                    }
                    if(value.equals("grant"))
                    {
                        if(assignment == null)
                        {
                            rootSession.getSecurity().grant(resource, role, permission, false);
                            continue;
                        }
                        if(assignment.isInherited())
                        {
                            rootSession.getSecurity().revoke(resource, role, permission);
                            rootSession.getSecurity().grant(resource, role, permission, false);
                            continue;
                        }
                    }
                    if(value.equals("recursive"))
                    {
                        if(assignment == null)
                        {
                            rootSession.getSecurity().grant(resource, role, permission, true);
                            continue;
                        }
                        if(!assignment.isInherited())
                        {
                            rootSession.getSecurity().revoke(resource, role, permission);
                            rootSession.getSecurity().grant(resource, role, permission, true);
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
        finally
        {
            rootSession.close();
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
