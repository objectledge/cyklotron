package net.cyklotron.cms.modules.actions.structure;

import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

import com.sun.org.apache.bcel.internal.verifier.exc.LoadingException;

public class UpdatePreferences
    extends BaseUpdatePreferences
{
    public void modifyNodePreferences(RunData data, Parameters conf)
    throws ProcessingException
    {
        String config = parameters.get("config");
        try
        {
            conf.addAll(new DefaultParameters(config), true);
        }
        catch(LoadingException e)
        {
            throw new ProcessingException("Failed to parse provided configuration string", e);
        }
    }
}
