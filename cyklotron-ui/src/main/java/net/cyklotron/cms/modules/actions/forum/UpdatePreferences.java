package net.cyklotron.cms.modules.actions.forum;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.web.HttpContext;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.structure.BaseUpdatePreferences;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

public class UpdatePreferences
    extends BaseUpdatePreferences
{
    
    
    public UpdatePreferences(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService,
        PreferencesService preferencesService, SiteService siteService)
    {
        super(logger, structureService, cmsDataFactory, styleService, preferencesService,
                        siteService);
        // TODO Auto-generated constructor stub
    }
    public void modifyNodePreferences(Context context, Parameters conf, Parameters parameters, CoralSession coralSession)
        throws ProcessingException
    {
        NavigationNodeResource node = getNode(context);
        String scope = parameters.get("scope",null);
        long did = parameters.getLong("did", -1);
        boolean statefull = parameters.getBoolean("statefull", true);
        if(did == -1)
        {
            conf.remove("did");
        }
        else
        {
            conf.set("did",did);
        }
        conf.set("statefull",statefull);
        if(node != null)
        {
            String sessionKey = "cms:component:forum:Forum:"+scope+":"+node.getIdString();
            HttpContext httpContext = HttpContext.getHttpContext(context);
            httpContext.removeSessionAttribute(sessionKey);
        }
    }
}
