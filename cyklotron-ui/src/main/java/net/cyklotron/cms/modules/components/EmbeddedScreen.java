package net.cyklotron.cms.modules.components;

import java.util.Iterator;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.i18n.I18nContext;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.templating.Templating;
import org.objectledge.templating.TemplatingContext;
import org.objectledge.web.HttpContext;
import org.objectledge.web.mvc.MVCContext;
import org.objectledge.web.mvc.finders.MVCFinder;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.skins.SkinService;
import net.cyklotron.cms.structure.NavigationNodeResource;

public class EmbeddedScreen extends SkinableCMSComponent
{
    public EmbeddedScreen(Context context, Logger logger, Templating templating,
        CmsDataFactory cmsDataFactory, SkinService skinService, MVCFinder mvcFinder)
    {
        super(context, logger, templating, cmsDataFactory, skinService, mvcFinder);
        // TODO Auto-generated constructor stub
    }
    public static String SCREEN_ERROR_MESSAGES_KEY = "screen_error_messages";
    
    public void process(Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, I18nContext i18nContext, CoralSession coralSession)
        throws ProcessingException
    {
        // TODO: Think of a better way of keeping the screen error messages
        List errors = (List)(templatingContext.get(SCREEN_ERROR_MESSAGES_KEY));
        
        if(errors != null && errors.size() > 0)
        {
            NavigationNodeResource currentNode = getNode();
            
            for(Iterator i=errors.iterator(); i.hasNext();)
            {
                componentError(context, (String)(i.next()));
            }
        }
    }
}
