package net.cyklotron.cms.modules.actions.forum;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;

import net.labeo.util.configuration.DefaultParameters;
import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameter;
import net.labeo.util.configuration.Parameters;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.structure.BaseUpdatePreferences;


/**
 * Update last added.
 * 
 * @author <a href="mailo:pablo@caltha.pl">Pawel Potempski</a>
 * @version $Id: UpdateLastAddedConf.java,v 1.2 2005-01-24 10:27:03 pablo Exp $
 */
public class UpdateLastAddedConf
    extends BaseUpdatePreferences
{
    public void modifyNodePreferences(Context context, Parameters conf, org.objectledge.parameters.Parameters parameters, CoralSession coralSession)
        throws ProcessingException
    {
        Parameters pc = new DefaultParameters();
        String forumNode = parameters.get("forum_node","no_selection");
        conf.set("forum_node", forumNode);
    }
}

