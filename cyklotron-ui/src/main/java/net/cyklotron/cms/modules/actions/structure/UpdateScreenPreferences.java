package net.cyklotron.cms.modules.actions.structure;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;
import org.objectledge.pipeline.ProcessingException;

import com.sun.org.apache.bcel.internal.verifier.exc.LoadingException;

import net.cyklotron.cms.structure.NavigationNodeResource;

public class UpdateScreenPreferences extends BaseUpdatePreferences
{
    public Parameters getScopedConfig(RunData data, Parameters conf,
        NavigationNodeResource node, String scope)
    throws ProcessingException
    {
        // get screen app and class to create it's config scope
        Parameters combinedConf = preferencesService.getCombinedNodePreferences(node);
        String app = combinedConf.get("screen.app");
        String screen = combinedConf.get("screen.class");

        return conf.getSubset("screen.config."+app+"."+screen.replace(',','.')+".");
    }

    public void modifyNodePreferences(Context context, Parameters conf, Parameters parameters, CoralSession coralSession)
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
