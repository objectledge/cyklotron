package net.cyklotron.cms.modules.views.appearance.skin;

import java.io.StringReader;
import java.io.StringWriter;

import net.labeo.services.templating.Context;
import net.labeo.services.templating.MergingException;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.integration.ApplicationResource;
import net.cyklotron.cms.integration.ComponentResource;
import net.cyklotron.cms.modules.views.appearance.BaseAppearanceScreen;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.skins.ComponentVariantResource;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: EditComponentTemplate.java,v 1.3 2005-01-25 11:23:41 pablo Exp $
 */
public class EditComponentTemplate extends BaseAppearanceScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        try
        {
            SiteResource site = getSite();
            String skin = parameters.get("skin");
            String app = parameters.get("appName");
            String component = parameters.get("compName");
            String variant =
                parameters.get("variant","Default");
            String state =
                parameters.get("state","Default");
            templatingContext.put("skin", skin);
            templatingContext.put("appName", app);
            templatingContext.put("compName", component);
            templatingContext.put("variant", variant);
            if (parameters.isDefined("state"))
            {
                templatingContext.put("state", state);
            }
            ApplicationResource appRes = integrationService.getApplication(app);
            ComponentResource compRes =
                integrationService.getComponent(appRes, component);
            ComponentVariantResource variantRes =
                skinService.getComponentVariant(
                    site,
                    skin,
                    compRes.getApplicationName(),
                    compRes.getComponentName(),
                    variant);
            templatingContext.put("variant_description", variantRes.getDescription());
            String fileName =
                skinService.getTemplateFilename(
                    compRes.getComponentName(),
                    variant,
                    state);
            templatingContext.put("filename", fileName);
            try
            {
                String contents =
                    skinService
                        .getComponentTemplateContents(
                            site,
                            skin,
                            compRes.getApplicationName(),
                            compRes.getComponentName(),
                            variant,
                            state);
                            
                templatingContext.put("contents", contents);

                if(!templatingContext.containsKey("result"))
                {
                    Context blankContext =
                        templatingService.createContext();
                    StringReader in = new StringReader(contents);
                    StringWriter out = new StringWriter();
                    try
                    {
                        templatingService.merge(
                            "",
                            blankContext,
                            in,
                            out,
                            "<component template>");
                    }
                    catch (MergingException e)
                    {
                        templatingContext.put("result", "template_parse_error");
                        templatingContext.put(
                            "parse_trace",
                            StringUtils.stackTrace(e.getRootCause()));
                    }
                }
            }
            catch (Exception e)
            {
                throw new ProcessingException(
                    "failed to load file contents",
                    e);
            }
        }
        catch (Exception e)
        {
            throw new ProcessingException("failed to retrieve information", e);
        }
    }
}
