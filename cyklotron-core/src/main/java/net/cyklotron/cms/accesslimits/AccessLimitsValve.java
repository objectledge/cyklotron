package net.cyklotron.cms.accesslimits;

import javax.servlet.http.HttpServletRequest;

import org.jcontainer.dna.Logger;
import org.objectledge.context.Context;
import org.objectledge.pipeline.ProcessingException;
import org.objectledge.pipeline.Valve;
import org.objectledge.web.HttpContext;
import org.objectledge.web.ratelimit.impl.RequestInfo;
import org.objectledge.web.ratelimit.impl.RuleEvaluator;

import com.google.common.base.Optional;

public class AccessLimitsValve
    implements Valve
{
    private final ProtectedItemRegistry protectedItemRegistry;

    private final ActionRegistry actionRegistry;

    private final RuleEvaluator evaluator;

    private Logger log;

    public AccessLimitsValve(ProtectedItemRegistry protectedItemRegistry,
        ActionRegistry actionRegistry, HitTableManager hitTableManger, Logger log)
    {
        this.protectedItemRegistry = protectedItemRegistry;
        this.actionRegistry = actionRegistry;
        this.log = log;
        this.evaluator = new RuleEvaluator(hitTableManger.getHitTable(), null);
    }

    @Override
    public void process(Context context)
        throws ProcessingException
    {
        HttpContext httpContext = context.getAttribute(HttpContext.class);
        HttpServletRequest request = httpContext.getRequest();
        RequestInfo requestInfo = RequestInfo.of(request);
        Optional<ProtectedItem> protRes = protectedItemRegistry.getProtectedItem(requestInfo);
        if(protRes.isPresent())
        {
            String actionName = evaluator.action(requestInfo, protRes.get().getRules());
            Optional<Action> action = actionRegistry.getAction(actionName);
            if(action.isPresent())
            {
                log.info("Applying action " + actionName + " to client "
                    + requestInfo.getAddress().toString() + " requesting " + requestInfo.getPath());
                action.get().apply(context);
            }
        }
    }
}
