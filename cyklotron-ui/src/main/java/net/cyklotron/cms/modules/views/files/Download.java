package net.cyklotron.cms.modules.views.files;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FileResourceImpl;
import net.cyklotron.cms.files.FilesService;

import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.pipeline.ProcessingException;

/**
 * The screen for serving files.
 *
 * @author <a href="mailto:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: Download.java,v 1.1 2005-01-24 04:34:12 pablo Exp $
 */
public class Download
    extends BaseARLScreen
{
    /** The logging service. */
    Logger log;
    
    /** Mail service for checking MIME types */
    MailService mailService;
    
    /** The files service. */
    FilesService filesService;
    
    /**
     * Constructs the screen.
     */
    public Download() 
    {
        log = ((LoggingService)broker.getService(LoggingService.SERVICE_NAME)).getFacility(FilesService.SERVICE_NAME);
        mailService = (MailService)broker.getService(MailService.SERVICE_NAME);
        filesService = (FilesService)broker.getService(FilesService.SERVICE_NAME);
    }

    /**
     * Builds the screen contents.
     *
     * <p>File data, prepended with apropriate headers will be sent directly
     * to the browser. This method returns null to supress markup output in
     * layout and page classes.</p>
     */
    public String build(RunData data)
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
            data.setContentType(contentType);
            data.getResponse().addDateHeader("Last-Modified",filesService.lastModified(file));
            InputStream is = filesService.getInputStream(file);
            OutputStream os = data.getOutputStream();
            byte[] buffer = new byte[is.available() > 0 ? is.available() : 32];
            int count = 0;
            while(count >= 0)
            {
                count = is.read(buffer,0,buffer.length);
                if(count > 0)
                {
                    os.write(buffer, 0, count);
                }
            }
            is.close();
            return null;
        }
        catch(EntityDoesNotExistException e)
        {
            throw new ProcessingException("Resource not found", e);
        }
        catch(IOException e)
        {
            log.error("Couldn't write to output", e);
        }
        return null;
    }
    
    
    public boolean checkAccess(RunData data)
        throws ProcessingException
    {
        try
        {
            long fileId = parameters.getLong("file_id", -1);
            if(fileId == -1)
            {
                log.error("Couldn't find the file id");
                return false;
            }
            FileResource file = FileResourceImpl.getFileResource(coralSession, fileId);
            return file.canView(coralSession.getUserSubject());
        }
        catch(Exception e)
        {
            log.error("Exception during access rights checking",e);
            return false;
        }
    }
    

}
