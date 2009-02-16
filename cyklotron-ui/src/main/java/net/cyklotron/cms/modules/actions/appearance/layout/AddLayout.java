package net.cyklotron.cms.modules.actions.appearance.layout;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.upload.FileUpload;
import org.objectledge.upload.UploadContainer;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.LayoutResource;
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: AddLayout.java,v 1.3 2005-03-09 09:58:34 pablo Exp $
 */
public class AddLayout
    extends BaseAppearanceAction
{
    /** upload service */
    private FileUpload uploadService;

    public AddLayout(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FileSystem fileSystem,
        SkinService skinService, IntegrationService integrationService,
        FileUpload fileUpload)
    {
        super(logger, structureService, cmsDataFactory, styleService, fileSystem, skinService,
                        integrationService);
        this.uploadService = fileUpload;
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        String name = parameters.get("name","");
        String description = parameters.get("description","");
        if(name.equals(""))
        {
            templatingContext.put("result","required_field_missing");
            mvcContext.setView("layout.AddLayout");
            return;
        }
        SiteResource site = getSite(context);
        try
        {
            LayoutResource layout = styleService.
                addLayout(coralSession, name, description, site);

            UploadContainer item = uploadService.getContainer("item1");
            if(item != null)
            {
                String[] sockets = styleService.findSockets(item.getString());
                for(int i=0; i<sockets.length; i++)
                {
                    styleService.addSocket(coralSession, layout, sockets[i]);
                }
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            log.error("failed to add layout", e);
            templatingContext.put("trace", new StackTrace(e));
            return;
        }
        templatingContext.put("result","added_successfully");
    }
}
