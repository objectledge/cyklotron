package net.cyklotron.cms.modules.views.banner;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsConstants;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.banner.BannerService;
import net.cyklotron.cms.banner.PoolResource;
import net.cyklotron.cms.banner.PoolResourceImpl;
import net.cyklotron.cms.preferences.PreferencesService;



/**
 *
 */
public class PoolProperties
    extends BaseBannerScreen
{
    
    public PoolProperties(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, BannerService bannerService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, bannerService);
        
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {

        Boolean fromComponent = (Boolean)httpContext.getSessionAttribute(CmsConstants.FROM_COMPONENT);
        if(fromComponent != null && fromComponent.booleanValue())
        {
            templatingContext.put("from_component",fromComponent);
            templatingContext.put("component_node",(Long)httpContext.getSessionAttribute(CmsConstants.COMPONENT_NODE));
            templatingContext.put("component_instance",(String)httpContext.getSessionAttribute(CmsConstants.COMPONENT_INSTANCE));
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
            logger.error("BannerException: ",e);
            return;
        }
    }
}
