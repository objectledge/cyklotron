package net.cyklotron.cms.modules.actions.search;

import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

import net.cyklotron.cms.modules.actions.structure.BaseUpdatePreferences;


/**
 *
 * @author <a href="mailo:pablo@ngo.pl">Pawel Potempski</a>
 * @version $Id: UpdatePreferences.java,v 1.1 2005-01-24 04:34:07 pablo Exp $
 */
public class UpdatePreferences
    extends BaseUpdatePreferences
{
    public void modifyNodePreferences(RunData data, Parameters conf)
        throws ProcessingException
    {
        // no preferences ATM
    }
}
