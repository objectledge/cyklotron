package net.cyklotron.cms.modules.actions.aggregation;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Role;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.aggregation.AggregationService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteResourceImpl;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: ToggleImportRights.java,v 1.2 2005-01-24 10:27:45 pablo Exp $
 */
public class ToggleImportRights
    extends BaseAggregationAction
{
    
    
    public ToggleImportRights(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SiteService siteService,
        AggregationService aggregationService)
    {
        super(logger, structureService, cmsDataFactory, siteService, aggregationService);
        // TODO Auto-generated constructor stub
    }
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
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
            templatingContext.put("trace",new StackTrace(e));
            log.error("AggregationException: ",e);
            return;
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        return checkAdministrator(context);
    }

}
