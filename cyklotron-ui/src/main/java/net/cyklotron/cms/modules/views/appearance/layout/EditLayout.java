package net.cyklotron.cms.modules.views.appearance.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.style.ComponentSocketResource;
import net.cyklotron.cms.style.LayoutResource;
import net.cyklotron.cms.style.LayoutResourceImpl;

/**
 *
 */
public class EditLayout
    extends BaseAppearanceScreen
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, HttpContext httpContext, TemplatingContext templatingContext, CoralSession coralSession)
        throws ProcessingException
    {
        long layoutId = parameters.getLong("layout_id", -1);
        if(layoutId == -1)
        {
            throw new ProcessingException("layout id couldn't be found");
        }
        LayoutResource layout = null;
        try 
        {
            layout = LayoutResourceImpl.getLayoutResource(coralSession,layoutId);
            templatingContext.put("layout",layout);
            if(parameters.isDefined("socket_count"))
            {
                int count = parameters.getInt("socket_count");
                ArrayList sockets = new ArrayList(count);
                for(int i=1; i<=count; i++)
                {
                    sockets.add(parameters.get("socket_"+i,""));
                }
                templatingContext.put("sockets", sockets);
            }
            else
            {
                ComponentSocketResource[] sockets = styleService.getSockets(layout);
                List socketList = new ArrayList();
                for(int i=0; i<sockets.length; i++)
                {
                    socketList.add(sockets[i].getName());
                }
                Collections.sort(socketList);
                templatingContext.put("sockets", socketList);
            }
        }
        catch (Exception e)
        {
            throw new ProcessingException("failed to load layout information",e);
        }
    }
}

