package net.cyklotron.cms.modules.actions.fixes;

import java.util.ArrayList;

import net.cyklotron.cms.modules.actions.BaseCMSAction;
import net.cyklotron.cms.site.SiteResource;
import net.cyklotron.cms.structure.NavigationNodeResource;
import net.labeo.services.resource.Resource;
import net.labeo.services.resource.Subject;
import net.labeo.services.templating.Context;
import net.labeo.util.StringUtils;
import net.labeo.webcore.ProcessingException;
import net.labeo.webcore.RunData;

/**
 * Traverses all navigation nodes in the site.
 *
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: BaseNavigationNodeFix.java,v 1.1 2005-01-24 04:34:29 pablo Exp $
 */
public abstract class BaseNavigationNodeFix extends BaseCMSAction
{
    public void execute(Context context, Parameters parameters, MVCContext mvcContext, TemplatingContext templatingContext, HttpContext httpContext, CoralSession coralSession)
        throws ProcessingException
    {
        Context context = data.getContext();
        Subject subject = coralSession.getUserSubject();
        try
        {
            SiteResource site = getSite(context);
            ArrayList stack = new ArrayList();
            stack.add(structureService.getRootNode(site));
            while(stack.size() > 0)
            {
                NavigationNodeResource node = (NavigationNodeResource)stack.remove(stack.size()-1);
                Resource[] children = coralSession.getStore().getResource(node);
                for(int i=0; i<children.length; i++)
                {
                    stack.add(children[i]);
                }
                if(fixNode(data, node))
                {
                    node.update(subject);
                }
            }
            templatingContext.put("result", "success");
        }
        catch(Exception e)
        {
            templatingContext.put("result", "exception");
            templatingContext.put("trace", StringUtils.stackTrace(e));
        }
    }

	/** Returns true if node update is needed */
    public abstract boolean fixNode(RunData data, NavigationNodeResource node)
        throws ProcessingException;
}
