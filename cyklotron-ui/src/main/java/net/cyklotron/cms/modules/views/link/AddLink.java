package net.cyklotron.cms.modules.views.link;

import java.util.Calendar;

import net.labeo.services.templating.Context;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureException;

/**
 *
 */
public class AddLink
    extends BaseLinkScreen
{
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        CmsData cmsData = getCmsData();
        SiteResource site = cmsData.getSite();
        if(site == null)
        {
            site = cmsData.getGlobalComponentsDataSite();
        }
        if(site == null)
        {
            throw new ProcessingException("no site selected");
        }
        Boolean fromComponent = (Boolean)httpContext.getSessionAttribute(FROM_COMPONENT);
        if(fromComponent != null && fromComponent.booleanValue())
        {
            templatingContext.put("from_component",fromComponent);
            templatingContext.put("component_node",(Long)httpContext.getSessionAttribute(COMPONENT_NODE));
            templatingContext.put("component_instance",(String)httpContext.getSessionAttribute(COMPONENT_INSTANCE));
        }
        templatingContext.put("data_site", site);
        try
        {
            templatingContext.put("data_site_root", structureService.getRootNode(site));
        }
        catch(StructureException e)
        {
            throw new ProcessingException("failed to lookup site root node", e);
        }

        Calendar calendar = Calendar.getInstance(i18nContext.getLocale()());
        templatingContext.put("calendar",calendar);
        Calendar twoWeeksLater = Calendar.getInstance(i18nContext.getLocale()());
        twoWeeksLater.add(Calendar.DAY_OF_MONTH,14);
        templatingContext.put("two_weeks_later",twoWeeksLater);
    }    
}
