package net.cyklotron.cms.accesslimits;

import java.util.LinkedHashMap;
import java.util.Map;

import org.objectledge.context.Context;
import org.objectledge.parameters.RequestParameters;
import org.objectledge.web.mvc.MVCContext;

public class Action
{
    private String viewOverride;

    private Map<String, String> paramsOverride;

    public Action(ActionResource res)
    {
        if(res.isViewOverrideDefined() && res.getViewOverride().trim().length() > 0)
        {
            viewOverride = res.getViewOverride().trim();
        }
        else
        {
            viewOverride = null;
        }
        if(res.isParamsOverrideDefined() && res.getParamsOverride().trim().length() > 0)
        {
            paramsOverride = new LinkedHashMap<>();
            for(String expr : res.getParamsOverride().trim().split("&"))
            {
                String[] terms = expr.split("=");
                if(terms.length > 0)
                {
                    paramsOverride.put(terms[0], terms.length > 1 ? terms[1] : null);
                }
            }
        }
        else
        {
            paramsOverride = null;
        }
    }

    public void apply(Context context)
    {
        if(viewOverride != null)
        {
            MVCContext mvcContext = context.getAttribute(MVCContext.class);
            mvcContext.setView(viewOverride);
        }
        if(paramsOverride != null)
        {
            RequestParameters parameters = context.getAttribute(RequestParameters.class);
            for(Map.Entry<String, String> expr : paramsOverride.entrySet())
            {
                if(expr.getValue() == null)
                {
                    parameters.remove(expr.getKey());
                }
                else if(expr.getValue().startsWith("$"))
                {
                    String val = parameters.get(expr.getValue().substring(1));
                    parameters.set(expr.getKey(), val);
                }
                else
                {
                    parameters.set(expr.getKey(), expr.getValue());
                }
            }
        }
    }
}
