package net.cyklotron.cms.modules.actions.search;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.modules.actions.structure.BaseUpdatePreferences;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdatePreferences.java,v 1.2 2005-01-24 10:27:13 pablo Exp $
 */
public class UpdatePreferences
    extends BaseUpdatePreferences
{
    public void modifyNodePreferences(Context context, Parameters conf, Parameters parameters, CoralSession coralSession)
        throws ProcessingException
    {
        // no preferences ATM
    }
}
