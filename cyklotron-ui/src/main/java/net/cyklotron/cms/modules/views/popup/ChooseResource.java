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
import net.cyklotron.cms.files.FilesService;
import net.cyklotron.cms.integration.IntegrationService;
import net.cyklotron.cms.integration.ResourceClassResource;
import net.cyklotron.cms.modules.views.BaseChooseResource;
import net.cyklotron.cms.preferences.PreferencesService;

public class ChooseResource
    extends BaseChooseResource
{
    public static String STATE_NAME = "cms:screens:popup,ChooseResource";
    
    protected String getStateName()
    {
        return STATE_NAME;
    }
    
    public ChooseResource(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager,
        IntegrationService integrationService, FilesService filesService)
    {

        super(context, logger, preferencesService, cmsDataFactory, tableStateManager,
                        integrationService, filesService);
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
