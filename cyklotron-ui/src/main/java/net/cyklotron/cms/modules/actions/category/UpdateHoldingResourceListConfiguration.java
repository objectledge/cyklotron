package net.cyklotron.cms.modules.actions.category;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.CmsDataFactory;
import net.cyklotron.cms.category.components.HoldingResourceListConfiguration;
import net.cyklotron.cms.modules.actions.structure.UpdatePreferences;
import net.cyklotron.cms.preferences.PreferencesService;
import net.cyklotron.cms.site.SiteService;
import net.cyklotron.cms.structure.ComponentDataCacheService;
import net.cyklotron.cms.structure.StructureService;
import net.cyklotron.cms.style.StyleService;

/**
 * Saves configuration for holding resource list component configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdateHoldingResourceListConfiguration.java,v 1.2 2005-01-24 10:27:04 pablo Exp $
 */
public class UpdateHoldingResourceListConfiguration
extends UpdatePreferences
{
    public UpdateHoldingResourceListConfiguration(Logger logger, StructureService structureService,
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
        // get basic configuration
        super.modifyNodePreferences(context, conf, parameters, coralSession);

        // get config
        HoldingResourceListConfiguration config =
        	(HoldingResourceListConfiguration) HoldingResourceListConfiguration.getConfig(context);
        CmsData cmsData = cmsDataFactory.getCmsData(context);
        config.update(cmsData, parameters);
        // remove it from session
		HoldingResourceListConfiguration.removeConfig(context);

		config.removeHeldResources(cmsDataFactory.getCmsData(context).getDate());
		String[] heldResStrs = config.getHeldResources();

		String[] heldResourcesParams = new String[heldResStrs.length];
		for (int i = 0; i < heldResStrs.length; i++)
        {
			heldResourcesParams[i] = heldResStrs[i];
        }
        conf.remove(HoldingResourceListConfiguration.HELD_RESOURCES_PARAM_KEY);
        conf.add(HoldingResourceListConfiguration.HELD_RESOURCES_PARAM_KEY,
        	heldResourcesParams);
    }
}
