package net.cyklotron.cms.modules.views.appearance.skin;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
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
 * @version $Id: ViewFile.java,v 1.3 2005-01-26 05:23:22 pablo Exp $
 */
public class ViewFile extends BaseAppearanceScreen
{
    
    public ViewFile(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, StyleService styleService, SkinService skinService,
        IntegrationService integrationService, Templating templating)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, styleService,
                        skinService, integrationService, templating);
        // TODO Auto-generated constructor stub
    }
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        String skin = parameters.get("skin");
        String path = parameters.get("path");
        SiteResource site = getSite();
        templatingContext.put("skin", skin);
        templatingContext.put("path", path);
        path = path.replace(',', '/');
        templatingContext.put("path_slashes", path);
        int last = path.lastIndexOf('/');
        String fileName = last >= 0 ? path.substring(last + 1) : path;
        templatingContext.put("file_name", fileName);
        templatingContext.put(
            "path_link",
            "/sites/" + site.getName() + "/" + skin + "/" + path);
        try
        {
            String type = skinService.getContentFileType(site, skin, path);
            templatingContext.put("mime_type", type);
            if (type.startsWith("text/"))
            {
                String contents =
                    skinService.getContentFileContents(
                        site,
                        skin,
                        path,
                        httpContext.getEncoding());
                contents = StringUtils.escapeXMLCharacters(contents);
                templatingContext.put("contents", contents);
            }
        }
        catch (Exception e)
        {
            throw new ProcessingException("failed to load file contents", e);
        }
    }
}
