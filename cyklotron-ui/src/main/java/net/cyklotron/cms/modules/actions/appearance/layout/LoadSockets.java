package net.cyklotron.cms.modules.actions.appearance.layout;

import java.util.Arrays;

import net.labeo.services.upload.UploadContainer;
import net.labeo.services.upload.UploadService;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;

public class LoadSockets
    extends BaseAppearanceAction
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        int socketCount = parameters.getInt("socket_count");
        for(int i=1; i<=socketCount; i++)
        {
            parameters.remove("socket_"+i);
        }

        UploadService uploadService = (UploadService)broker.
            getService(UploadService.SERVICE_NAME);
        UploadContainer item = uploadService.getItem(data, "item1");

        try
        {
            String[] sockets = styleService.findSockets(item.getString());

            Arrays.sort(sockets);
            for(int i=0; i<sockets.length; i++)
            {
                parameters.set("socket_"+(i+1), sockets[i]);
            }
            parameters.set("socket_count", sockets.length);
        }
        catch(Exception e)
        {
            data.getContext().put("result", "exception");
            data.getContext().put("trace", StringUtils.stackTrace(e));
        }
    }
}
