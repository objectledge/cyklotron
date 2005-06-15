package net.cyklotron.cms.modules.views.forum;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.views.BaseCMSScreen;
import net.cyklotron.cms.preferences.PreferencesService;

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

/**
 * Configurator screen for NodeComments component.
 */
public class NodeCommentsConf
    extends BaseCMSScreen
{
    /**
     * Craetes a new CodeComments component configuration view insntace.
     * 
     * @param context the Context component.
     * @param logger the Logger.
     * @param preferencesService the PreferencesService component.
     * @param cmsDataFactory the CmsDataFactory component.
     * @param tableStateManager the TableStateManager component.
     */
    public NodeCommentsConf(Context context, Logger logger, PreferencesService preferencesService,
        CmsDataFactory cmsDataFactory, TableStateManager tableStateManager)
    {
        super(context, logger, preferencesService, cmsDataFactory, tableStateManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(Parameters parameters, MVCContext mvcContext,
        TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext,
        CoralSession coralSession)
        throws ProcessingException
    {
        templatingContext.put("componentConfig", prepareComponentConfig(parameters,
            templatingContext));
    }
}
