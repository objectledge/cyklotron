package net.cyklotron.cms.modules.actions.aggregation;

import net.labeo.Labeo;
import net.labeo.services.resource.EntityDoesNotExistException;
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
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: ToggleImportRights.java,v 1.1 2005-01-24 04:35:18 pablo Exp $
 */
public class ToggleImportRights
    extends BaseAggregationAction
{
    
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        long siteId = parameters.getLong("site", -1);
        try
        {
            Subject subject = coralSession.getSecurity().getSubject(Subject.ROOT);
            Role importerRole = coralSession.getSecurity().getUniqueRole("cms.aggregation.importer");
            SiteResource siteResource = SiteResourceImpl.getSiteResource(coralSession, siteId);
            if(siteResource.getTeamMember().isSubRole(importerRole))
            {
                coralSession.getSecurity().deleteSubRole(siteResource.getTeamMember(), importerRole);
                templatingContext.put("result","revoked_successfully");
            }
            else
            {
                coralSession.getSecurity().addSubRole(siteResource.getTeamMember(), importerRole);
                templatingContext.put("result","granted_successfully");
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("AggregationException: ",e);
            return;
        }
    }

    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        Role role = coralSession.getSecurity().getUniqueRole("cms.administrator");
        return coralSession.getUserSubject().hasRole(role);
    }

}
