package net.cyklotron.cms.modules.actions.site;

import net.cyklotron.cms.site.SiteResource;

import org.objectledge.pipeline.ProcessingException;

/**
 *
 */
public class SetPrimaryVirtualSite
    extends BaseSiteAction
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        String domain = parameters.get("domain");

        try
        {
            SiteResource site = ss.getSiteByAlias(domain);
            ss.setPrimaryMapping(site, domain, coralSession.getUserSubject());
        }
        catch(Exception e)
        {
            if(e instanceof ProcessingException)
            {
                throw (ProcessingException)e;
            }
            throw new ProcessingException("failed to lookup site information",e);
        }
    }
}
