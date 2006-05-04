/*
 * Created on Oct 29, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.actions.periodicals;

import java.util.Locale;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.upload.FileUpload;
import org.objectledge.upload.UploadContainer;
import org.objectledge.upload.UploadLimitExceededException;
import org.objectledge.utils.StackTrace;
import org.objectledge.utils.StringUtils;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.periodicals.PeriodicalRenderer;
import net.cyklotron.cms.periodicals.PeriodicalsService;
import net.cyklotron.cms.periodicals.PeriodicalsTemplatingService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;

/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CreateTemplate 
    extends BasePeriodicalsAction
{
    private FileUpload fileUpload;
    
    private final PeriodicalsTemplatingService periodicalsTemplatingService;
    
    public CreateTemplate(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, PeriodicalsService periodicalsService,
        PeriodicalsTemplatingService periodicalsTemplatingService,
        SiteService siteService, FileUpload fileUpload)
    {
        super(logger, structureService, cmsDataFactory, periodicalsService, siteService);
        this.periodicalsTemplatingService = periodicalsTemplatingService;
        this.fileUpload = fileUpload;
        
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException
    {
        SiteResource site = getSite(context);

        String rendererName = parameters.get("renderer");
        PeriodicalRenderer renderer = periodicalsService.getRenderer(rendererName);
        String name = parameters.get("name");
        if(name.length() == 0)
        {
            templatingContext.put("result", "name_empty");
        }
        if(periodicalsTemplatingService.hasTemplateVariant(site, rendererName, name))
        {
            templatingContext.put("result", "name_in_use");
        }
    
        if(!templatingContext.containsKey("result"))
        {
            String source = parameters.get("source","app");
            UploadContainer file;
            try
            {
                file = fileUpload.getContainer("file");
            }
            catch(UploadLimitExceededException e)
            {
                templatingContext.put("result", "file_size_exceeded");
                periodicalsService.releaseRenderer(renderer);
                return;
            }
            try
            {
                String contents = null;
                if(source.equals("file"))
                {
                    if(file == null)
                    {
                        templatingContext.put("result","file_not_selected");
                    }
                    else
                    {
                        contents = file.getString();
                    }
                }
                else if(source.equals("app"))
                {
                    Locale locale = StringUtils.getLocale(parameters.
                        get("locale"));
                    contents = periodicalsTemplatingService.getDefaultTemplateContents(renderer, locale);
                }
                else if(source.equals("variant"))
                {
                    String variant = parameters.get("variant");
                    contents = periodicalsTemplatingService.getTemplateVariantContents(site, rendererName, variant);
                }
                else
                {
                    contents = "";
                }
                periodicalsTemplatingService.createTemplateVariant(site, rendererName, name, contents);                
            }
            catch(Exception e)
            {
                templatingContext.put("result", "exception");
                templatingContext.put("trace", new StackTrace(e));
            }
            finally
            {
                periodicalsService.releaseRenderer(renderer);
            }
        }
        if(templatingContext.containsKey("result"))
        {
            mvcContext.setView("periodicals.CreateTemplate");
        }
        else
        {
            mvcContext.setView("periodicals.EditTemplate");
            templatingContext.put("result","added_successfully");
        }
    }
}
