package net.cyklotron.cms.modules.actions.appearance.layout;

import net.labeo.services.templating.Context;
import net.labeo.services.upload.UploadContainer;
import net.labeo.services.upload.UploadService;
import net.labeo.services.webcore.NotFoundException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.style.LayoutResource;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: AddLayout.java,v 1.1 2005-01-24 04:34:02 pablo Exp $
 */
public class AddLayout
    extends BaseAppearanceAction
{
    /** upload service */
    private UploadService uploadService;

    public AddLayout()
    {
        uploadService = (UploadService)broker.getService(UploadService.SERVICE_NAME);
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException, NotFoundException
    {
        Context context = data.getContext();
        String name = parameters.get("name","");
        String description = parameters.get("description","");
        if(name.equals(""))
        {
            templatingContext.put("result","required_field_missing");
            data.setView("layout,AddLayout");
            return;
        }
        SiteResource site = getSite(context);
        try
        {
            LayoutResource layout = styleService.
                addLayout(name, description, site, coralSession.getUserSubject());

            UploadContainer item = uploadService.getItem(data, "item1");
            if(item != null)
            {
                String[] sockets = styleService.findSockets(item.getString());
                for(int i=0; i<sockets.length; i++)
                {
                    styleService.addSocket(layout, sockets[i], coralSession.getUserSubject());
                }
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            log.error("failed to add layout", e);
            templatingContext.put("trace", StringUtils.stackTrace(e));
            return;
        }
        templatingContext.put("result","added_successfully");
    }
}
