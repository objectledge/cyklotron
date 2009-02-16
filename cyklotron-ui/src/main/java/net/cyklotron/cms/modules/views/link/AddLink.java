package net.cyklotron.cms.modules.views.link;

import java.util.Calendar;

import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsConstants;
import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.link.LinkService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.StructureException;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 */
public class AddLink
    extends BaseLinkScreen
{
    
    public AddLink(org.objectledge.context.Context context, Logger logger,
        PreferencesService preferencesService, CmsDataFactory cmsDataFactory,
        TableStateManager tableStateManager, LinkService linkService,
        StructureService structureService)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager, linkService,
                        structureService);
        
    }
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
        Boolean fromComponent = (Boolean)httpContext.getSessionAttribute(CmsConstants.FROM_COMPONENT);
        if(fromComponent != null && fromComponent.booleanValue())
        {
            templatingContext.put("from_component",fromComponent);
            templatingContext.put("component_node",(Long)httpContext.getSessionAttribute(CmsConstants.COMPONENT_NODE));
            templatingContext.put("component_instance",(String)httpContext.getSessionAttribute(CmsConstants.COMPONENT_INSTANCE));
        }
        templatingContext.put("data_site", site);
        try
        {
            templatingContext.put("data_site_root", structureService.getRootNode(coralSession, site));
        }
        catch(StructureException e)
        {
            throw new ProcessingException("failed to lookup site root node", e);
        }

        Calendar calendar = Calendar.getInstance(i18nContext.getLocale());
        templatingContext.put("calendar",calendar);
        Calendar twoWeeksLater = Calendar.getInstance(i18nContext.getLocale());
        twoWeeksLater.add(Calendar.DAY_OF_MONTH,14);
        templatingContext.put("two_weeks_later",twoWeeksLater);
    }    
}
