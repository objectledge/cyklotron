package net.cyklotron.cms.modules.components.files;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.Resource;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.modules.components.SkinableCMSComponent;
import net.cyklotron.cms.skins.SkinService;

/**
 * Files component.
 * 
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Files.java,v 1.7 2007-11-18 21:25:53 rafal Exp $
 */

public class PublicUpload
    extends SkinableCMSComponent
{

    public PublicUpload(org.objectledge.context.Context context, Logger logger,
        Templating templating, CmsDataFactory cmsDataFactory, SkinService skinService,
        MVCFinder mvcFinder, FilesService fileService, TableStateManager tableStateManager)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder);
    }

    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            if(getSite(context) != null)
            {
                Parameters componentConfig = getConfiguration();
                Resource directory = null;
                long dir_id = componentConfig.getLong("dir", -1L);
                if(dir_id == -1L)
                {
                    componentError(context, "No upload directory defined");
                    return;
                }
                templatingContext.put("header", componentConfig.get("header", ""));
            }
            else
            {
                componentError(context, "No site selected");
            }
        }
        catch(Exception e)
        {
            componentError(context, "Exception", e);
        }
    }
}
