package net.cyklotron.cms.modules.actions.site;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: AddVirtualSite.java,v 1.3 2005-01-25 07:48:02 pablo Exp $
 */
public class AddVirtualSite
    extends BaseSiteAction
{
    public AddVirtualSite(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, SiteService siteService)
    {
        super(logger, structureService, cmsDataFactory, siteService);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        
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
        if(!templatingContext.containsKey("result"))
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
                    ss.addMapping(coralSession, site, domain, (NavigationNodeResource)res[0]);
                }
            }
            catch(Exception e)
            {
                templatingContext.put("result","exception");
                logger.error("AddVirtualSite:",e);
                templatingContext.put("trace", new StackTrace(e));
            }
        }
        if(templatingContext.containsKey("result"))
        {
            mvcContext.setView("site,AddVirtualSite");
            templatingContext.put("domain", domain);
            templatingContext.put("default_node_path", defaultNodePath);
        }
        else
        {

            templatingContext.put("result","added_successfully");
        }
    }
}
