package net.cyklotron.cms.modules.actions.banner;

import java.util.ArrayList;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.ValueRequiredException;
import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.banner.BannersResource;
import net.cyklotron.cms.banner.BannersResourceImpl;
import net.cyklotron.cms.banner.PoolResource;
import net.cyklotron.cms.banner.PoolResourceImpl;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: AddPool.java,v 1.1 2005-01-24 04:34:40 pablo Exp $
 */
public class AddPool
    extends BaseBannerAction
{

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();

        String title = parameters.get("title","");
        String description = parameters.get("description","");
        if(title.length() < 1 || title.length() > 32)
        {
            templatingContext.put("result","invalid_title");
            return;
        }
        if(description.length() > 256)
        {
            templatingContext.put("result","invalid_description");
            return;
        }

        int bsid = parameters.getInt("bsid", -1);
        if(bsid == -1)
        {
            throw new ProcessingException("Banners root id not found");
        }

        try
        {
            BannersResource bannersRoot = BannersResourceImpl.getBannersResource(coralSession, bsid);
            PoolResource poolResource = PoolResourceImpl.createPoolResource(coralSession, title, bannersRoot, subject);
            poolResource.setDescription(description);
            poolResource.setBanners(new ArrayList());
            poolResource.update(subject);
        }
        catch(EntityDoesNotExistException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("BannerException: ",e);
            return;
        }
        catch(ValueRequiredException e)
        {
            templatingContext.put("result","exception");
            templatingContext.put("trace",net.labeo.util.StringUtils.stackTrace(e));
            log.error("BannerException: ",e);
            return;
        }
        templatingContext.put("result","added_successfully");
    }
}


