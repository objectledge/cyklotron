package net.cyklotron.cms.modules.actions.appearance.layout;

import java.util.HashSet;
import java.util.Iterator;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.security.Subject;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.upload.FileUpload;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.actions.appearance.BaseAppearanceAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.ComponentSocketResource;
import net.cyklotron.cms.style.LayoutResource;
import net.cyklotron.cms.style.LayoutResourceImpl;
import net.cyklotron.cms.style.StyleService;

/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdateLayout.java,v 1.2 2005-01-24 10:27:10 pablo Exp $
 */
public class UpdateLayout
    extends BaseAppearanceAction
{
    /** upload service */
    private FileUpload uploadService;

    
    
    public UpdateLayout(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService, FileSystem fileSystem,
        SkinService skinService, IntegrationService integrationService,
        FileUpload fileUpload)
    {
        super(logger, structureService, cmsDataFactory, styleService, fileSystem, skinService,
                        integrationService);
        uploadService = fileUpload;
    }

    /**
     * Performs the action.
     */
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
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
            if(!layout.getName().equals(name) && styleService.getLayout(coralSession, site, name) != null)
            {
                templatingContext.put("result","name_already_exists");
                return;
            }
            styleService.updateLayout(coralSession, layout, name, description);
            // sockets
            HashSet current = new HashSet();
            ComponentSocketResource[] sockets = styleService.getSockets(coralSession, layout);
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
                    styleService.addSocket(coralSession, layout, socket);
                }
            }
            i = current.iterator();
            while(i.hasNext())
            {
                String socket = (String)i.next();
                if(!updated.contains(socket))
                {
                    styleService.deleteSocket(coralSession, layout, socket);
                }
            }
        }
        catch(Exception e)
        {
            templatingContext.put("result","exception");
            log.error("failed to update layout", e);
            templatingContext.put("trace", new StackTrace(e));
            return;
        }
        templatingContext.put("result","updated_successfully");
    }
}
