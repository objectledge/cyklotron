package net.cyklotron.cms.modules.views.files;

import java.io.IOException;
import java.io.InputStream;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.entity.EntityDoesNotExistException;
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
import org.objectledge.web.mvc.security.SecurityChecking;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FileResourceImpl;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.preferences.PreferencesService;

/**
 * The screen for serving files.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Download.java,v 1.6 2006-01-02 11:42:17 rafal Exp $
 */
public class Download
    extends AbstractBuilder
    implements SecurityChecking
{
    /** The logging service. */
    protected Logger logger;
    
    /** Mail service for checking MIME types */
    protected MailSystem mailService;
    
    /** The files service. */
    protected FilesService filesService;
    
    /** file download */
    protected FileDownload fileDownload;
 
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
        try 
        {
            FileResource file = getFile(context);
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

    /**
     * {@inheritDoc}
     */
    public boolean requiresAuthenticatedUser(Context context)
        throws Exception
    {
        // anonymous users are okay - access control hinges on checkAccessRights method
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean requiresSecureChannel(Context context)
        throws Exception
    {
        FileResource file = getFile(context);
        return filesService.getSite(file).getRequiresSecureChannel();
    }

    /**
     * {@inheritDoc}
     */
    public boolean checkAccessRights(Context context)
        throws ProcessingException
    {
        FileResource file = getFile(context);
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        return file.canView(coralSession, coralSession.getUserSubject());
    }
    
    // impl /////////////////////////////////////////////////////////////////////////////////////
    
    protected FileResource getFile(Context context)
        throws ProcessingException
    {
        CoralSession coralSession = (CoralSession)context.getAttribute(CoralSession.class);
        Parameters parameters = RequestParameters.getRequestParameters(context);
        long fileId = parameters.getLong("file_id", -1);
        if(fileId == -1)
        {
            throw new ProcessingException("file_id parameter is missing");
        }
        try
        {
            return FileResourceImpl.getFileResource(coralSession, fileId);
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("could not retrieve file resource", e);
        }        
    }
}
