package net.cyklotron.cms.modules.views.appearance.skin;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Templating;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.style.StyleService;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: DownloadFile.java,v 1.2 2005-01-26 05:23:22 pablo Exp $
 */
public abstract class DownloadFile extends BaseAppearanceScreen
{
    
    
    public DownloadFile(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        StyleService styleService, SkinService skinService, IntegrationService integrationService,
        Templating templating)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, styleService,
                        skinService, integrationService, templating);
        // TODO Auto-generated constructor stub
    }
    /**
    public String build(RunData data) throws ProcessingException
    {
        String skin = parameters.get("skin");
        String path = parameters.get("path");
        SiteResource site = getSite();
        path = path.replace(',', '/');
        boolean asText =
            parameters.get("type","binary").equals("text");
        boolean asXML =
            parameters.get("type","binary").equals("xml");
        try
        {
            if(asText)
            {
                data.setContentType("text/plain");
                String contents =
                    skinService.getContentFileContents(
                        site,
                        skin,
                        path,
                        data.getEncoding());
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
                    skinService.getContentFileContents(
                        site,
                        skin,
                        path,
                        data.getEncoding());
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
                    (int)skinService.getContentFileLength(site, skin, path));
                skinService.getContentFileContents(
                    site,
                    skin,
                    path,
                    data.getOutputStream());
            }
        }
        catch (Exception e)
        {
            throw new ProcessingException("failed to send file", e);
        }
        return null;
    }
    */
}
