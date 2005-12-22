package net.cyklotron.cms.modules.views.appearance.skin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.upload.FileDownload;
import org.objectledge.utils.StackTrace;
import org.objectledge.utils.StringUtils;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.style.StyleService;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DownloadFile.java,v 1.4 2005-12-22 10:00:44 rafal Exp $
 */
public class DownloadFile extends BaseAppearanceScreen
{
    private FileDownload fileDownload;
    
    public DownloadFile(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        StyleService styleService, SkinService skinService, IntegrationService integrationService,
        Templating templating, FileDownload fileDownload)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, styleService,
                        skinService, integrationService, templating);
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
        String skin = parameters.get("skin");

        String path = parameters.get("path");
        SiteResource site = getSite();
        path = path.replace(',', '/');
        boolean asText = parameters.get("type","binary").equals("text");
        boolean asXML = parameters.get("type","binary").equals("xml");
        try
        {
            String contentType;
            String contents;                                                
            if(asText)
            {
                contentType = "text/plain";
                contents = skinService.getContentFileContents(site, skin, path, httpContext.getEncoding());
            }
            else if(asXML)
            {
                contentType = "text/xml";
                contents =
                    skinService.getContentFileContents(site, skin, path, httpContext.getEncoding());
                contents = 
                    "<?xml version=\"1.0\" encoding=\""+httpContext.getEncoding()+"\"?>\n"+
                    "<contents>\n"+
                    "  <![CDATA["+contents+"]]>\n"+
                    "</contents>\n";
            }
            else
            {
                contentType = "application/octet-stream";
                contents = skinService.getContentFileContents(site, skin, path, httpContext.getEncoding());
            }
            httpContext.disableCache();
            httpContext.setResponseLength(StringUtils.getByteCount(contents, httpContext
                .getEncoding()));
            fileDownload.dumpData(new ByteArrayInputStream(contents.getBytes(httpContext
                .getEncoding())), contentType, (new Date()).getTime());            
        }
        catch(IOException e)
        {
            logger.error("Couldn't write to output", e);
        }
        catch(Exception e)
        {
            templatingContext.put("errorResult", "result.exception");
            templatingContext.put("stackTrace", new StackTrace(e).toStringArray());
            logger.error("exception occured", e);
        }
    }
}
