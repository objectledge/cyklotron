package net.cyklotron.cms.modules.actions.aggregation;

import net.labeo.services.resource.Permission;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Role;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteResourceImpl;

/**
 * Send the resource recommendation to the chosen sites.
 *
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: RecommendResource.java,v 1.1 2005-01-24 04:35:18 pablo Exp $
 */
public class RecommendResource
    extends BaseAggregationAction
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
        String comment = parameters.get("comment","");
        String[] keys = parameters.getKeys();
        boolean done = false;
        try
        {
            Resource resource = coralSession.getStore().getResource(resourceId);
            for(int i = 0; i < keys.length; i++)
            {
                if(keys[i].startsWith("recommend_"))
                {
                    long id = Long.parseLong(keys[i].substring(10));
                    SiteResource site = SiteResourceImpl.getSiteResource(coralSession, id);
                    aggregationService.submitRecommendation(resource, site, comment, subject);
                    done = true;
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
        if(done)
        {
            templatingContext.put("result","recommended_successfully");
        }
        else
        {
            templatingContext.put("result","no_site_chosen");
        }
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
