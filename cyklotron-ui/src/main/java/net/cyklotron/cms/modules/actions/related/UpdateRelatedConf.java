package net.cyklotron.cms.modules.actions.related;

import java.util.Map;

import net.labeo.services.resource.EntityDoesNotExistException;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.resource.util.ResourceSelectionState;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.DefaultParameters;
import net.labeo.util.configuration.Parameter;
import net.labeo.util.configuration.Parameters;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.structure.BaseUpdatePreferences;

/**
 * Update related component configuration.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: UpdateRelatedConf.java,v 1.1 2005-01-24 04:34:41 pablo Exp $
 */
public class UpdateRelatedConf
    extends BaseUpdatePreferences
{
    public void modifyNodePreferences(RunData data, Parameters conf)
        throws ProcessingException
    {
        Parameters pc = new DefaultParameters();
        String[] keys = parameters.getKeys();
        for(int i = 0; i < keys.length; i++)
        {
            if(keys[i].startsWith("resource-"))
            {
                pc.add("related_classes",keys[i].substring(9,keys[i].length()));
            }
        }
        conf.remove("related_classes");
        conf.addAll(pc, true);
    }
}

