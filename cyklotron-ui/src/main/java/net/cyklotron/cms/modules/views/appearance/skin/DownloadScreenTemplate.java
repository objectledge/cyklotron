package net.cyklotron.cms.modules.views.appearance.skin;

import java.io.PrintWriter;

import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.ScreenResource;
import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.site.SiteResource;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DownloadScreenTemplate.java,v 1.1 2005-01-24 04:34:19 pablo Exp $
 */
public class DownloadScreenTemplate extends BaseAppearanceScreen
{
    public String build(RunData data)
        throws ProcessingException
    {
        SiteResource site = getSite();        
        String skin = parameters.get("skin");
        String app = parameters.get("appName");
        String screen = parameters.get("screenName");
        String variant = parameters.get("variant","Default");
        String state = parameters.get("state","Default");
        boolean asText =
            parameters.get("type","binary").equals("text");
        boolean asXML =
            parameters.get("type","binary").equals("xml");
        ApplicationResource appRes = integrationService.getApplication(app);
        ScreenResource screenRes = integrationService.getScreen(appRes, 
            screen);
        try
        {
            if(asText)
            {
                data.setContentType("text/plain");
                String contents =
                    skinService.getScreenTemplateContents(
                        site,
                        skin,
                        screenRes.getApplicationName(),
                        screenRes.getScreenName(),
                        variant,
                        state);
                data.getResponse().addIntHeader(
                    "Content-Length",
                    StringUtils.getByteCount(contents, data.getEncoding()));
                data.getPrintWriter().print(contents);
                data.getPrintWriter().flush();
            }
            else if(asXML)
            {
                data.setContentType("text/xml");
                String contents =
                    skinService.getScreenTemplateContents(
                        site,
                        skin,
                        screenRes.getApplicationName(),
                        screenRes.getScreenName(),
                        variant,
                        state);
                contents = 
                    "<?xml version=\"1.0\" encoding=\""+data.getEncoding()+"\"?>\n"+
                    "<contents>\n"+
                    "  <![CDATA["+contents+"]]>\n"+
                    "</contents>\n";
                data.getResponse().addIntHeader(
                    "Content-Length",
                    StringUtils.getByteCount(contents, data.getEncoding()));
                PrintWriter pw = data.getPrintWriter();
                pw.print(contents);
                pw.flush();
            }
            else
            {
                data.setContentType("application/octet-stream");
                data.getResponse().addIntHeader("Content-Length", 
                    (int)skinService.getScreenTemplateLength(site, skin, 
                        screenRes.getApplicationName(), screenRes.getScreenName(), 
                        variant, state));
                skinService.getScreenTemplateContents(site, skin, screenRes.getApplicationName(),
                    screenRes.getScreenName(), variant, state, data.getOutputStream());                
            }
        }
        catch(Exception e)
        {
            throw new ProcessingException("failed to send file", e);
        }
        return null;
    }
}
