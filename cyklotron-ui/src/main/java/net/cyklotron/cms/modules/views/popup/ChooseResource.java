package net.cyklotron.cms.modules.views.popup;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.table.TableStateManager;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.modules.views.BaseChooseResource;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.related.RelatedConstants;

public class ChooseResource
    extends BaseChooseResource
{
    public static String SELECTION_STATE = "cms:popup:resources.selection.state";
    
    public static String STATE_NAME = "cms:screens:popup,ChooseResource";
    
    public ChooseResource(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        IntegrationService integrationService)
    {

        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        integrationService);
    }

    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        super.process(parameters, mvcContext, templatingContext, httpContext, i18nContext,
            coralSession);
    }
    
    protected boolean isResourceClassSupported(ResourceClassResource rClass)
    {
        return rClass.getPickerSupported(false);
    }

}
