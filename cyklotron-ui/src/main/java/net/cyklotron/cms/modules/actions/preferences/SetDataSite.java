/*
 */
package net.cyklotron.cms.modules.actions.preferences;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.structure.StructureService;

/**
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 */
public class SetDataSite extends BasePreferencesAction
{
    
    public SetDataSite(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, PreferencesService preferencesService)
    {
        super(logger, structureService, cmsDataFactory, preferencesService);
        // TODO Auto-generated constructor stub
    }
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException
    {
        Parameters sys = preferencesService.getSystemPreferences();
        sys.set("globalComponentsData", parameters.get("data_site"));
    }
}
