package net.cyklotron.cms.modules.actions.forum;

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
 * Update last added.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: UpdateLastAddedConf.java,v 1.4 2005-03-08 10:52:12 pablo Exp $
 */
public class UpdateLastAddedConf
    extends BaseUpdatePreferences
{
    
    public UpdateLastAddedConf(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService,
        PreferencesService preferencesService, SiteService siteService)
    {
        super(logger, structureService, cmsDataFactory, styleService, preferencesService,
                        siteService);
        
    }
    public void modifyNodePreferences(Context context, Parameters conf, org.objectledge.parameters.Parameters parameters, CoralSession coralSession)
        throws ProcessingException
    {
        Parameters pc = new DefaultParameters();
        String forumNode = parameters.get("forum_node","no_selection");
        conf.set("forum_node", forumNode);
    }
}

