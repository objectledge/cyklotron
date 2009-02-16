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
public class AddComponent extends BasePreferencesAction
{
    
    public AddComponent(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, PreferencesService preferencesService)
    {
        super(logger, structureService, cmsDataFactory, preferencesService);
        
    }
    
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession) throws ProcessingException
    {
        Parameters sys = preferencesService.getSystemPreferences(coralSession);
        String newComponent = parameters.get("new_component");    
        sys.add("globalComponents", newComponent);
    }
}
