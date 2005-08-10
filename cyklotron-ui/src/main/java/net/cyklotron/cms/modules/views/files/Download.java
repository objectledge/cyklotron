package net.cyklotron.cms.modules.views.files;

import java.io.IOException;
import java.io.InputStream;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.mail.MailSystem;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Template;
import org.objectledge.upload.FileDownload;
import org.objectledge.web.mvc.builders.AbstractBuilder;
import org.objectledge.web.mvc.builders.BuildException;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FileResourceImpl;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 * The screen for serving files.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Download.java,v 1.5 2005-08-10 05:31:11 rafal Exp $
 */
public class Download
    extends AbstractBuilder
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
        super(context);
        this.logger = logger;
        this.mailService = mailSystem;
        this.filesService = filesService;
        this.fileDownload = fileDownload;
    }
 
    public String build(Template template, String embeddedBuildResults)
        throws BuildException, ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
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
            logger.error("exception occured", e);
        }
        return "";
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
            return file.canView(coralSession, coralSession.getUserSubject());
        }
        catch(Exception e)
        {
            logger.error("Exception during access rights checking",e);
            return false;
        }
    }
}
