package net.cyklotron.cms.modules.actions.related;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.DefaultParameters;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.structure.BaseUpdatePreferences;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * Update related component configuration.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: UpdateRelatedConf.java,v 1.4 2005-03-08 10:53:25 pablo Exp $
 */
public class UpdateRelatedConf
    extends BaseUpdatePreferences
{
    
    public UpdateRelatedConf(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService,
        PreferencesService preferencesService, SiteService siteService)
    {
        super(logger, structureService, cmsDataFactory, styleService, preferencesService,
                        siteService);
        
    }
    
    public void modifyNodePreferences(Context context, Parameters conf, Parameters parameters, CoralSession coralSession)
        throws ProcessingException
    {
        Parameters pc = new DefaultParameters();
        String[] keys = parameters.getParameterNames();
        for(int i = 0; i < keys.length; i++)
        {
            if(keys[i].startsWith("resource-"))
            {
                pc.add("related_classes",keys[i].substring(9,keys[i].length()));
            }
        }
        conf.remove("related_classes");
        conf.add(pc, true);
    }
}

