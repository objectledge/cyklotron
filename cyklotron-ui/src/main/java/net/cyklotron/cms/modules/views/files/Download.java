package net.cyklotron.cms.modules.views.files;

import java.io.IOException;
import java.io.InputStream;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.mail.MailSystem;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.upload.FileDownload;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FileResourceImpl;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 * The screen for serving files.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Download.java,v 1.3 2005-01-26 23:36:20 pablo Exp $
 */
public class Download
    extends BaseCMSScreen
{
    /** The logging service. */
    Logger logger;
    
    /** Mail service for checking MIME types */
    MailSystem mailService;
    
    /** The files service. */
    FilesService filesService;
    
    /** file download */
    private FileDownload fileDownload;
 
    public Download(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        MailSystem mailSystem, FilesService filesService, FileDownload fileDownload)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
        this.mailService = mailSystem;
        this.filesService = filesService;
        this.fileDownload = fileDownload;
    }
 
    /**
     * {@inheritDoc}
     */
    public void process(Parameters parameters, MVCContext mvcContext, 
        TemplatingContext templatingContext, HttpContext httpContext,
        I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try 
        {
            long fileId = parameters.getLong("file_id", -1);
            if(fileId == -1)
            {
                throw new ProcessingException("No item was selected");
            }
            FileResource file = FileResourceImpl.getFileResource(coralSession, fileId);
            String contentType = file.getMimetype();
            
            long lastModified = filesService.lastModified(file);
            InputStream is = filesService.getInputStream(file);
            fileDownload.dumpData(is, contentType, lastModified);
        }
        catch(IOException e)
        {
            logger.error("Couldn't write to output", e);
        }
        catch(Exception e)
        {
            templatingContext.put("errorResult", "magpie.result.exception");
            templatingContext.put("stackTrace", new StackTrace(e).toStringArray());
            logger.error("exception occured", e);
        }
    }

    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        try
        {
            long fileId = parameters.getLong("file_id", -1);
            if(fileId == -1)
            {
                logger.error("Couldn't find the file id");
                return false;
            }
            FileResource file = FileResourceImpl.getFileResource(coralSession, fileId);
            return file.canView(context, coralSession.getUserSubject());
        }
        catch(Exception e)
        {
            logger.error("Exception during access rights checking",e);
            return false;
        }
    }
    

}
