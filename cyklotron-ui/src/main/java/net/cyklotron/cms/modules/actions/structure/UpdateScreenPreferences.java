package net.cyklotron.cms.modules.actions.structure;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.ComponentDataCacheService;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

public class UpdateScreenPreferences extends BaseUpdatePreferences
{
    public UpdateScreenPreferences(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService,
        PreferencesService preferencesService, SiteService siteService,
        ComponentDataCacheService componentDataCacheService)
    {
        super(logger, structureService, cmsDataFactory, styleService, preferencesService,
                        siteService, componentDataCacheService);
        
    }
    
    public Parameters getScopedConfig(Parameters conf,
        NavigationNodeResource node, String scope, CoralSession coralSession)
    throws ProcessingException
    {
        // get screen app and class to create it's config scope
        Parameters combinedConf = preferencesService.getCombinedNodePreferences(coralSession, node);
        String app = combinedConf.get("screen.app");
        String screen = combinedConf.get("screen.class");

        return conf.getChild("screen.config."+app+"."+screen.replace(',','.')+".");
    }

    public void modifyNodePreferences(Context context, Parameters conf, Parameters parameters, CoralSession coralSession)
    throws ProcessingException
    {
        String config = parameters.get("config");
        try
        {
            conf.add(new DefaultParameters(config), true);
        }
        catch(Exception e)
        {
            throw new ProcessingException("Failed to parse provided configuration string", e);
        }
    }
}
