package net.cyklotron.cms.modules.actions.appearance.layout;

import java.util.HashSet;
import java.util.Iterator;

import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.services.upload.UploadService;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.style.ComponentSocketResource;
import net.cyklotron.cms.style.LayoutResource;
import net.cyklotron.cms.style.LayoutResourceImpl;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdateLayout.java,v 1.1 2005-01-24 04:34:02 pablo Exp $
 */
public class UpdateLayout
    extends BaseAppearanceAction
{
    /** upload service */
    private UploadService uploadService;

    public UpdateLayout()
    {
        uploadService = (UploadService)broker.getService(UploadService.SERVICE_NAME);
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();
        String name = parameters.get("name","");
        String description = parameters.get("description","");
        if(name.equals(""))
        {
            templatingContext.put("result","navi_name_empty");
            return;
        }
        long layoutId = parameters.getLong("layout_id", -1);
        if (layoutId == -1)
        {
            throw new ProcessingException("layout id could not be found");
        }
        LayoutResource layout = null;
        try
        {
            SiteResource site = getSite(context);

            layout= LayoutResourceImpl.getLayoutResource(coralSession,layoutId);
            if(!layout.getName().equals(name) && styleService.getLayout(site, name) != null)
            {
                templatingContext.put("result","name_already_exists");
                return;
            }
            styleService.updateLayout(layout, name, description, subject);
            // sockets
            HashSet current = new HashSet();
            ComponentSocketResource[] sockets = styleService.getSockets(layout);
            for(int i=0; i<sockets.length; i++)
            {
                current.add(sockets[i].getName());
            }
            HashSet updated = new HashSet();
            int count = parameters.getInt("socket_count");
            for(int i=1; i<=count; i++)
            {
                String socket = parameters.get("socket_"+i,"");
                if(socket.length() > 0)
                {
                    updated.add(socket);
                }
            }
            Iterator i = updated.iterator();
            while(i.hasNext())
            {
                String socket = (String)i.next();
                if(!current.contains(socket))
                {
                    styleService.addSocket(layout, socket, coralSession.getUserSubject());
                }
            }
            i = current.iterator();
            while(i.hasNext())
            {
                String socket = (String)i.next();
                if(!updated.contains(socket))
                {
                    styleService.deleteSocket(layout, socket, coralSession.getUserSubject());
                }
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            log.error("failed to update layout", e);
            templatingContext.put("trace", StringUtils.stackTrace(e));
            return;
        }
        templatingContext.put("result","updated_successfully");
    }
}
