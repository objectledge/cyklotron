package net.cyklotron.cms.modules.views.link;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.link.PoolResource;
import net.cyklotron.cms.link.PoolResourceImpl;
import net.cyklotron.cms.site.SiteResource;



/**
 *
 */
public class EditPool
    extends BaseLinkScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        CmsData cmsData = getCmsData();
        Boolean fromComponent = (Boolean)httpContext.getSessionAttribute(FROM_COMPONENT);
        if(fromComponent != null && fromComponent.booleanValue())
        {
            templatingContext.put("from_component",fromComponent);
            templatingContext.put("component_node",(Long)httpContext.getSessionAttribute(COMPONENT_NODE));
            templatingContext.put("component_instance",(String)httpContext.getSessionAttribute(COMPONENT_INSTANCE));
            SiteResource site = cmsData.getSite();
            if(site == null)
            {
                site = cmsData.getGlobalComponentsDataSite();
            }
            if(site == null)
            {
                throw new ProcessingException("no site selected");
            }
            templatingContext.put("data_site", site);
        }
        else
        {
            templatingContext.put("data_site", cmsData.getSite());
        }

        int pid = parameters.getInt("pid", -1);
        if(pid == -1)
        {
            throw new ProcessingException("Pool id not found");
        }
        try
        {
            PoolResource pool = PoolResourceImpl.getPoolResource(coralSession, pid);
            templatingContext.put("pool",pool);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",new StackTrace(e));
            log.error("LinkException: ",e);
            return;
        }
    }
}
