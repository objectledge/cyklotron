package net.cyklotron.cms.modules.actions.site;

import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteException;
/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: DeleteVirtualSite.java,v 1.2 2005-01-24 10:27:50 pablo Exp $
 */
public class DeleteVirtualSite
    extends BaseSiteAction
{
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException, NotFoundException
    {
        Context context = data.getContext();
        String domain = parameters.get("domain","");
        if(domain.equals(""))
        {
            templatingContext.put("result","domain_name_empty");
        }
        try
        {
            ss.removeMapping(domain, coralSession.getUserSubject());
        }
        catch(SiteException e)
        {
            templatingContext.put("result","exception");
            log.error("DeleteDomain",e);
            templatingContext.put("trace", new StackTrace(e));
        }
        if(templatingContext.containsKey("result"))
        {
            mvcContext.setView("site,EditVirtualSite");
            String defaultNodePath = parameters.get("default_node_path","");
            if(defaultNodePath.length() > 0)
            {
                templatingContext.put("default_node_path", defaultNodePath);
            }
        }
        else
        {
            templatingContext.put("result","deleted_successfully");
        }
    }
}
