package net.cyklotron.cms.modules.actions.site;

import net.labeo.Labeo;
import net.labeo.services.resource.Resource;
import net.labeo.services.templating.Context;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdateVirtualSite.java,v 1.1 2005-01-24 04:35:11 pablo Exp $
 */
public class UpdateVirtualSite
    extends BaseSiteAction
{
    protected StructureService structureService;

    public UpdateVirtualSite()
    {
        structureService = (StructureService)Labeo.getBroker().
            getService(StructureService.SERVICE_NAME);
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException, NotFoundException
    {
        Context context = data.getContext();
        String domain = parameters.get("domain","");
        String defaultNodePath = parameters.get("default_node_path","");
        if(domain.equals(""))
        {
            templatingContext.put("result","domain_name_empty");
        }
        if(defaultNodePath.equals(""))
        {
            templatingContext.put("result","default_node_empty");
        }
        if(!context.containsKey("result"))
        {
            try
            {
                SiteResource site = getSite(context);
                Resource[] res = coralSession.getStore().getResource(site, "structure");
                if(res.length == 0)
                {
                    throw new ProcessingException("failed to lookup structure root node "+
                                                  "for site "+site.getName());
                }
                Resource structure = res[0];
                String fullNodePath = structure.getPath()+defaultNodePath;
                res = coralSession.getStore().getResourceByPath(fullNodePath);
                if(res.length != 1)
                {
                    templatingContext.put("result", "default_node_invalid");
                }
                else
                {
                    ss.updateMapping(site, domain, (NavigationNodeResource)res[0], coralSession.getUserSubject());
                }
            }
            catch(Exception e)
            {
                templatingContext.put("result","exception");
                log.error("AddVirtualSite:",e);
                templatingContext.put("trace", StringUtils.stackTrace(e));
            }
        }
        if(context.containsKey("result"))
        {
            data.setView("site,AddVirtualSite");
            templatingContext.put("domain", domain);
            templatingContext.put("default_node_path", defaultNodePath);
        }
        else
        {

            templatingContext.put("result","updated_successfully");
        }
    }
}
