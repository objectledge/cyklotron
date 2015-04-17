package net.cyklotron.cms.modules.views.files;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.mail.MailSystem;
import org.objectledge.parameters.Parameters;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Template;
import org.objectledge.upload.FileDownload;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.builders.BuildException;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.preferences.PreferencesService;

public class Resized
    extends Download
{
    private final FileSystem fileSystem;

    public Resized(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager, MailSystem mailSystem,
        FileSystem fileSystem, FilesService filesService, FileDownload fileDownload)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, mailSystem,
                        filesService, fileDownload);
        this.fileSystem = fileSystem;
    }

    @Override
    public String build(Template template, String embeddedBuildResults)
        throws BuildException, ProcessingException
    {
        try
        {
            Parameters parameters = context.getAttribute(RequestParameters.class);
            FileResource file = getFile(context);
            HttpContext httpContext = context.getAttribute(HttpContext.class);
            String path = "";
            if(parameters.isDefined("rm") || parameters.isDefined("c")
                || parameters.isDefined("c_x") || parameters.isDefined("c_y"))
            {
                path = filesService.resizeImage(file, parameters.getInt("w", -1),
                    parameters.getInt("h", -1), parameters.get("rm", "f"),
                    parameters.getBoolean("c", false), parameters.getInt("c_x", -1),
                    parameters.getInt("c_y", -1), parameters.get("f_ext", "jpg"));
            }
            else
            {
                path = filesService.resizeImage(file, parameters.getInt("w", -1),
                    parameters.getInt("h", -1), parameters.get("f_ext", "jpg"));
            }
            long lastModified = fileSystem.lastModified(path);
            long ims = httpContext.getRequest().getDateHeader("If-Modified-Since");
            if(ims > 0 && lastModified <= ims)
            {
                httpContext.getResponse().setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                httpContext.setDirectResponse(true);
            }
            else
            {
                InputStream is = fileSystem.getInputStream(path);
                long size = fileSystem.length(path);
                String contentType = "image/jpg";
                if(Arrays.asList(filesService.IMAGE_EXTENSIONS).contains(
                    parameters.get("f_ext", "jpg").toLowerCase()))
                {
                    contentType = "image/" + parameters.get("f_ext", "jpg").toLowerCase();
                }
                fileDownload.dumpData(is, contentType, lastModified, (int)size, null);
            }
            return "";
        }
        catch(IOException e)
        {
            throw new ProcessingException(e);
        }
    }
}
