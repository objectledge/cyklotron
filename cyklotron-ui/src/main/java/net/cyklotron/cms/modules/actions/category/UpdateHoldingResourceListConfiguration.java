package net.cyklotron.cms.modules.actions.category;

import net.cyklotron.cms.CmsData;
import net.cyklotron.cms.category.components.HoldingResourceListConfiguration;
import net.cyklotron.cms.modules.actions.structure.UpdatePreferences;
import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Saves configuration for holding resource list component configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdateHoldingResourceListConfiguration.java,v 1.1 2005-01-24 04:33:58 pablo Exp $
 */
public class UpdateHoldingResourceListConfiguration
extends UpdatePreferences
{
    public void modifyNodePreferences(RunData data, Parameters conf)
    throws ProcessingException
    {
        // get basic configuration
        super.modifyNodePreferences(data, conf);

        // get config
        HoldingResourceListConfiguration config =
        	(HoldingResourceListConfiguration) HoldingResourceListConfiguration.getConfig(data);
        config.update(data);
        // remove it from session
		HoldingResourceListConfiguration.removeConfig(data);

		config.removeHeldResources(cmsDataFactory.getCmsData(context).getDate());
		String[] heldResStrs = config.getHeldResources();

		Parameter[] heldResourcesParams = new Parameter[heldResStrs.length];
		for (int i = 0; i < heldResStrs.length; i++)
        {
			heldResourcesParams[i] = heldResStrs[i];
        }

        conf.remove(HoldingResourceListConfiguration.HELD_RESOURCES_PARAM_KEY);
        conf.addAll(HoldingResourceListConfiguration.HELD_RESOURCES_PARAM_KEY,
        	heldResourcesParams);
    }
}
