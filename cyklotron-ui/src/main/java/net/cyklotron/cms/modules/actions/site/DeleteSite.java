package net.cyklotron.cms.modules.actions.site;

import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeleteSite.java,v 1.1 2005-01-24 04:35:11 pablo Exp $
 */
public class DeleteSite
    extends BaseSiteAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        try
        {
            SiteResource site = getSite(context);
            ss.destroySite(site, coralSession.getUserSubject());
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            log.error("DeleteSite: ",e);
            templatingContext.put("trace", StringUtils.stackTrace(e));
            return;
        }
        templatingContext.put("result","deleted_successfully");
    }
}
