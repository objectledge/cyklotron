package net.cyklotron.cms.modules.views.appearance.skin;

import java.io.PrintWriter;

import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.site.SiteResource;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DownloadLayout.java,v 1.1 2005-01-24 04:34:19 pablo Exp $
 */
public class DownloadLayout extends BaseAppearanceScreen
{
    public String build(RunData data) throws ProcessingException
    {
        String skin = parameters.get("skin");
        String layout = parameters.get("layout");
        boolean asText =
            parameters.get("type","binary").equals("text");
        boolean asXML =
            parameters.get("type","binary").equals("xml");
        SiteResource site = getSite();
        try
        {
            if(asText)
            {
                data.setContentType("text/plain");
                String contents =
                    skinService.getLayoutTemplateContents(site, skin, layout);
                data.getResponse().addIntHeader(
                    "Content-Length",
                    StringUtils.getByteCount(contents, data.getEncoding()));
                PrintWriter pw = data.getPrintWriter();
                pw.print(contents);
                pw.flush();
            }
            else if(asXML)
            {
                data.setContentType("text/xml");
                String contents =
                    skinService.getLayoutTemplateContents(site, skin, layout);
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
                data.getResponse().addIntHeader(
                    "Content-Length",
                    (int)skinService.getLayoutTemplateLength(
                        site,
                        skin,
                        layout));
                skinService.getLayoutTemplateContents(
                    site,
                    skin,
                    layout,
                    data.getOutputStream());
            }
        }
        catch (Exception e)
        {
            throw new ProcessingException("failed to send file", e);
        }
        return null;
    }
}
