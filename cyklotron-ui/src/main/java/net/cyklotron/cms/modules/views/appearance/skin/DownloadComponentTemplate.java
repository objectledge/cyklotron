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
import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.ComponentResource;
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
 * @version $Id: DownloadComponentTemplate.java,v 1.3 2005-02-03 01:46:25 pablo Exp $
 */
public class DownloadComponentTemplate extends BaseAppearanceScreen
{
    private FileDownload fileDownload;
    
    public DownloadComponentTemplate(Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StyleService styleService, SkinService skinService,
        IntegrationService integrationService, Templating templating,
        FileDownload fileDownload)
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
        SiteResource site = getSite();
        String skin = parameters.get("skin");
        String app = parameters.get("appName");
        String component = parameters.get("compName");
        String variant = parameters.get("variant","Default");
        String state = parameters.get("state","Default");
        boolean asText = parameters.get("type","binary").equals("text");
        boolean asXML = parameters.get("type","binary").equals("xml");
        ApplicationResource appRes = integrationService.getApplication(coralSession, app);
        ComponentResource compRes = integrationService.getComponent(coralSession, appRes, component);
        try
        {
            if(asText)
            {
                String contentType = "text/plain";
                String contents =
                    skinService.getComponentTemplateContents(
                        site,
                        skin,
                        compRes.getApplicationName(),
                        compRes.getComponentName(),
                        variant,
                        state);

            }
            else if(asXML)
            {
                String contentType = "text/xml";
                String contents =
                    skinService.getComponentTemplateContents(
                        site,
                        skin,
                        compRes.getApplicationName(),
                        compRes.getComponentName(),
                        variant,
                        state);
                contents = 
                    "<?xml version=\"1.0\" encoding=\""+httpContext.getEncoding()+"\"?>\n"+
                    "<contents>\n"+
                    "  <![CDATA["+contents+"]]>\n"+
                    "</contents>\n";
                httpContext.getResponse().addIntHeader(
                    "Content-Length", StringUtils.getByteCount(contents, httpContext.getEncoding()));
                fileDownload.dumpData(new ByteArrayInputStream(contents.getBytes(httpContext.getEncoding())), contentType, (new Date()).getTime());
            }
            else
            {
                String contentType = "application/octet-stream";
                String contents =
                    skinService.getComponentTemplateContents(
                        site,
                        skin,
                        compRes.getApplicationName(),
                        compRes.getComponentName(),
                        variant,
                        state);
                httpContext.getResponse().addIntHeader(
                    "Content-Length", StringUtils.getByteCount(contents, httpContext.getEncoding()));
                fileDownload.dumpData(new ByteArrayInputStream(contents.getBytes(httpContext.getEncoding())), contentType, (new Date()).getTime());
            }
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
