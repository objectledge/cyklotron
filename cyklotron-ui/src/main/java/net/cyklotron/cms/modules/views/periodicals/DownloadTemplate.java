/*
 * Created on Oct 29, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package net.cyklotron.cms.modules.views.periodicals;

import java.io.PrintWriter;

import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.site.SiteResource;

/**
 * @author fil
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DownloadTemplate
    extends BasePeriodicalsScreen
{
    public String build(RunData data) 
        throws ProcessingException
    {
        SiteResource site = getSite();
        String renderer = parameters.get("renderer");
        String name = parameters.get("name");
        boolean asText =
            parameters.get("type","binary").equals("text");
        boolean asXML =
            parameters.get("type","binary").equals("xml");
        try
        {
            if (asText)
            {
                data.setContentType("text/plain");
                String contents = periodicalsService.getTemplateVariantContents(site, renderer, name);
                data.getResponse().addIntHeader(
                    "Content-Length",
                    StringUtils.getByteCount(contents, data.getEncoding()));
                data.getPrintWriter().print(contents);
                data.getPrintWriter().flush();
            }
            else if(asXML)
            {
                data.setContentType("text/xml");
                String contents = periodicalsService.getTemplateVariantContents(site, renderer, name);
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
                    (int)periodicalsService.getTemplateVariantLength(site, renderer, name));
                periodicalsService.getTemplateVariantContents(
                    site,
                    renderer,
                    name,
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
