package net.cyklotron.cms.modules.actions.poll;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;

import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.structure.BaseUpdatePreferences;

public class UpdatePreferences
    extends BaseUpdatePreferences
{    
    public void modifyNodePreferences(Context context, Parameters conf, Parameters parameters, CoralSession coralSession)
        throws ProcessingException
    {
        String dir = parameters.get("pool_id","");
        conf.set("pool_id",dir);     
    }
}
