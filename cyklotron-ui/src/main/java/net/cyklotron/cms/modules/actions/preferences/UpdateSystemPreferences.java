package net.cyklotron.cms.modules.actions.preferences;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.utils.StackTrace;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.structure.StructureService;

import com.sun.org.apache.bcel.internal.verifier.exc.LoadingException;

/**
 * 
 * 
 * @author <a href="mailto:rafal@caltha.pl">Rafal Krzewski</a>
 * @version $Id: UpdateSystemPreferences.java,v 1.4 2005-02-09 22:22:58 rafal Exp $
 */
public class UpdateSystemPreferences 
    extends BasePreferencesAction
{
    
    public UpdateSystemPreferences(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, PreferencesService preferencesService)
    {
        super(logger, structureService, cmsDataFactory, preferencesService);
        // TODO Auto-generated constructor stub
    }
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Parameters conf = preferencesService.getSystemPreferences();
        String config = parameters.get("config","");
        try
        {
            conf.remove();
            conf.add(new DefaultParameters(config), true);
        }
        catch(LoadingException e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", new StackTrace(e));
        }
        if(templatingContext.containsKey("result"))
        {
            mvcContext.setView("preferences,SystemPrefernces");
        }
    }
}
