package net.cyklotron.cms.rewrite;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.objectledge.web.rewrite.RewriteInfo;
import org.objectledge.web.rewrite.RewriteInfoBuilder;

import net.cyklotron.cms.structure.NavigationNodeResource;

public class RewriteTarget
{
    private final SitePath path;

    private final NavigationNodeResource targetNode;

    private final String targetView;

    private final Map<String, List<String>> parameters;

    public RewriteTarget(SitePath path, String targetView, NavigationNodeResource targetNode,
        Map<String, List<String>> parameters)
    {
        if(targetView == null && targetNode == null)
        {
            throw new IllegalArgumentException("either targetView or targetNode must be non-null");
        }
        this.path = path;
        this.targetView = targetView;
        this.targetNode = targetNode;
        this.parameters = parameters;
    }

    public SitePath getPath()
    {
        return path;
    }

    public RewriteInfo rewrite(RewriteInfo request, String remainingPathInfo)
    {
        RewriteInfoBuilder builder = RewriteInfoBuilder.fromRewriteInfo(request);
        builder.withServletPath("/");
        if(targetView != null)
        {
            builder.withPathInfo("view/" + targetView + remainingPathInfo);
        }
        else
        {
            builder.withPathInfo("x/" + targetNode.getIdString() + remainingPathInfo);
        }

        for(Map.Entry<String, List<String>> entry : parameters.entrySet())
        {
            builder.withFormParameter(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    public String getTargetUrl()
    {
        StringBuilder buff = new StringBuilder();
        if(targetView != null)
        {
            buff.append("/view/").append(targetView);
        }
        else
        {
            buff.append("/x/").append(targetNode.getIdString());            
        }
        if(parameters.size() > 0)
        {
            buff.append('?');
            Iterator<Map.Entry<String, List<String>>> i = parameters.entrySet().iterator();
            while(i.hasNext())
            {
                Map.Entry<String, List<String>> e = i.next();
                Iterator<String> j = e.getValue().iterator();
                while(j.hasNext())
                {
                    buff.append(e.getKey()).append('=').append(j.next());
                    if(i.hasNext() || j.hasNext())
                    {
                        buff.append('&');
                    }
                }
            }
        }
        return buff.toString();
    }
}
