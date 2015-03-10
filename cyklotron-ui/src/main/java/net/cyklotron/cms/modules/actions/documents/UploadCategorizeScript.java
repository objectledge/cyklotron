package net.cyklotron.cms.modules.actions.documents;

import java.io.IOException;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.structure.StructureService;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.upload.FileUpload;
import org.objectledge.upload.UploadContainer;
import org.objectledge.upload.UploadLimitExceededException;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.security.PolicySystem;

/**
 * Upload script.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: UploadScript.java,v 1.7 2005-05-30 09:44:25 zwierzem Exp $
 */
public class UploadCategorizeScript extends BaseCMSAction
{
	/** file upload */
	private FileUpload fileUpload;
	
    /**
     * Action constructor.
     * 
     * @param logger the logger.
     * @param fileUpload the file upload manager.
     */
    public UploadCategorizeScript(Logger logger, StructureService structureService, CmsDataFactory cmsDataFactory, FileUpload fileUpload)
    {
        super(logger, structureService, cmsDataFactory);
    	this.fileUpload = fileUpload;        
    }
    
    @Override
    public void execute(Context context, Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        UploadContainer script;
        try
        {
            script = fileUpload.getContainer("script");
            if(script != null)
            {
                templatingContext.put("uploaded", new String(script.getBytes()));
            }
        }
        catch(UploadLimitExceededException | IOException e)
        {
            throw new ProcessingException(e);
        }
    }
}
