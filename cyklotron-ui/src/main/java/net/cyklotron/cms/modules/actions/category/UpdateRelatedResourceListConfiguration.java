package net.cyklotron.cms.modules.actions.category;

import java.util.Iterator;
import java.util.Set;

import net.cyklotron.cms.category.CategoryResource;
import net.cyklotron.cms.category.components.RelatedResourceListConfiguration;
import net.cyklotron.cms.category.query.CategoryQueryService;
import net.cyklotron.cms.modules.actions.structure.UpdatePreferences;
import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Saves configuration for related resource list component configuration.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: UpdateRelatedResourceListConfiguration.java,v 1.1 2005-01-24 04:33:58 pablo Exp $
 */
public class UpdateRelatedResourceListConfiguration
extends UpdatePreferences
{
	/** category query service */
	protected CategoryQueryService categoryQueryService;

	public UpdateRelatedResourceListConfiguration()
	{
		categoryQueryService = (CategoryQueryService)broker.getService(CategoryQueryService.SERVICE_NAME);
	}
	
    public void modifyNodePreferences(RunData data, Parameters conf)
    throws ProcessingException
    {
        // get basic configuration
        super.modifyNodePreferences(data, conf);

        // get config
        RelatedResourceListConfiguration config = RelatedResourceListConfiguration.getConfig(data);
        config.update(data);
        // remove it from session
        RelatedResourceListConfiguration.removeConfig(data);

        // build paths list
		Set categories = config.getCategorySelectionState()
			.getResources(coralSession, "active").keySet();
		Parameter[] categoryPaths = new Parameter[categories.size()];
		StringBuffer buf = new StringBuffer(128);
		int j = 0;
		for(Iterator i=categories.iterator(); i.hasNext(); j++)
		{
			CategoryResource cat = (CategoryResource)(i.next());
			buf.setLength(0);
			buf.append('\'').append(cat.getPath()).append('\'');
			categoryPaths[j] = buf.toString();
		}

        conf.remove(RelatedResourceListConfiguration.ACTIVE_CATEGORIES_PARAM_KEY);
        conf.addAll(RelatedResourceListConfiguration.ACTIVE_CATEGORIES_PARAM_KEY, categoryPaths);
    }
}
