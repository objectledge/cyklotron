package net.cyklotron.cms.modules.actions.forum;

import org.objectledge.context.Context;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.parameters.Parameters;

import net.labeo.util.configuration.Configuration;
import net.labeo.util.configuration.Parameter;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

import net.cyklotron.cms.modules.actions.structure.BaseUpdatePreferences;
import net.cyklotron.cms.structure.NavigationNodeResource;

public class UpdatePreferences
    extends BaseUpdatePreferences
{
    public void modifyNodePreferences(Context context, Parameters conf, Parameters parameters, CoralSession coralSession)
        throws ProcessingException
    {
        NavigationNodeResource node = getNode(context);
        String scope = parameters.get("scope",null);
        long did = parameters.getLong("did", -1);
        boolean statefull = parameters.getBoolean("statefull", true);
        if(did == -1)
        {
            conf.remove("did");
        }
        else
        {
            conf.set("did",did);
        }
        conf.set("statefull",statefull);
        if(node != null)
        {
            String sessionKey = "cms:component:forum:Forum:"+scope+":"+node.getIdString();
            data.getLocalContext().removeAttribute(sessionKey);
        }
    }
}
