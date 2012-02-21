package net.cyklotron.cms.modules.actions.search;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.modules.actions.structure.BaseUpdatePreferences;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.ComponentDataCacheService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdatePreferences.java,v 1.4 2005-03-08 10:53:37 pablo Exp $
 */
public class UpdatePreferences
    extends BaseUpdatePreferences
{
    
    public UpdatePreferences(Logger logger, StructureService structureService,
        CmsDataFactory cmsDataFactory, StyleService styleService,
        PreferencesService preferencesService, SiteService siteService,
        ComponentDataCacheService componentDataCacheService)
    {
        super(logger, structureService, cmsDataFactory, styleService, preferencesService,
                        siteService, componentDataCacheService);
        
    }
    public void modifyNodePreferences(Context context, Parameters conf, Parameters parameters, CoralSession coralSession)
        throws ProcessingException
    {
        // no preferences ATM
    }
}
