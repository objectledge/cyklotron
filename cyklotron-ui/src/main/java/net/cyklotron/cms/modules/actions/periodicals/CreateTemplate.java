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
import org.objectledge.utils.StackTrace;
import org.objectledge.utils.StringUtils;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.periodicals.PeriodicalsService;
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
    
    public CreateTemplate(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, PeriodicalsService periodicalsService,
        SiteService siteService, FileUpload fileUpload)
    {
        super(logger, structureService, cmsDataFactory, periodicalsService, siteService);
        this.fileUpload = fileUpload;
        // TODO Auto-generated constructor stub
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException
    {
        SiteResource site = getSite(context);

        String renderer = parameters.get("renderer");
        String name = parameters.get("name");
        if(name.length() == 0)
        {
            templatingContext.put("result", "name_empty");
        }
        if(periodicalsService.hasTemplateVariant(site, renderer, name))
        {
            templatingContext.put("result", "name_in_use");
        }
    
        if(!templatingContext.containsKey("result"))
        {
            String source = parameters.get("source","app");
            UploadContainer file = fileUpload.getContainer("file");
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
                    contents = periodicalsService.getDefaultTemplateContents(renderer, locale);
                }
                else if(source.equals("variant"))
                {
                    String variant = parameters.get("variant");
                    contents = periodicalsService.getTemplateVariantContents(site, renderer, variant);
                }
                else
                {
                    contents = "";
                }
                periodicalsService.createTemplateVariant(site, renderer, name, contents);                
            }
            catch(Exception e)
            {
                templatingContext.put("result", "exception");
                templatingContext.put("trace", new StackTrace(e));
            }
        }
        if(templatingContext.containsKey("result"))
        {
            mvcContext.setView("periodicals,CreateTemplate");
        }
        else
        {
            mvcContext.setView("periodicals,EditTemplate");
            templatingContext.put("result","added_successfully");
        }
    }
}
