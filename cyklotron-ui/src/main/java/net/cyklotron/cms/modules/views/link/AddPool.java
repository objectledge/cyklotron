package net.cyklotron.cms.modules.views.link;

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
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.link.LinkService;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.structure.StructureService;

/**
 *
 */
public class AddPool
    extends BaseLinkScreen
{
    
    public AddPool(org.objectledge.context.Context context, Logger logger,
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

        Boolean fromComponent = (Boolean)httpContext.getSessionAttribute(CmsConstants.FROM_COMPONENT);
        if(fromComponent != null && fromComponent.booleanValue())
        {
            templatingContext.put("from_component",fromComponent);
            templatingContext.put("component_node",(Long)httpContext.getSessionAttribute(CmsConstants.COMPONENT_NODE));
            templatingContext.put("component_instance",(String)httpContext.getSessionAttribute(CmsConstants.COMPONENT_INSTANCE));
        }

    }    
}

