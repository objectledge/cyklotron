package net.cyklotron.cms.modules.actions.fixes;

import net.labeo.services.resource.Role;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;

/**
 * 
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawe� Potempski</a>
 * @version $Id: PrepareToAggregation.java,v 1.1 2005-01-24 04:34:29 pablo Exp $
 */
public class PrepareToAggregation
    extends BaseCMSAction
{
    /** site service */
    private SiteService siteService;

    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();
        try
        {
            siteService = (SiteService)broker.getService(SiteService.SERVICE_NAME);
            SiteResource[] sites = siteService.getSites();
            for(int i = 0; i < sites.length; i++)
            {
                fixSite(sites[i], subject, data);
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
        }
    }


    public void fixSite(SiteResource site, Subject subject, RunData data)
        throws Exception
    {
        Role siteRole = site.getSiteRole();
        if(siteRole == null)
        {
            siteRole = coralSession.getSecurity().createRole("cms.site.siterole."+site.getName());
            site.setSiteRole(siteRole);
            site.update(subject);
        }
    }
    
    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        Role role = coralSession.getSecurity().getUniqueRole("cms.administrator");
        return coralSession.getUserSubject().hasRole(role);
    }

}
