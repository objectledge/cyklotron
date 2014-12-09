package net.cyklotron.cms.rewrite;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.cyklotron.cms.structure.NavigationNodeResource;

public class RewriteTarget
{
    private final SitePath path;

    private final NavigationNodeResource node;

    private final Map<String, List<String>> parameters;

    public RewriteTarget(SitePath path, NavigationNodeResource node,
        Map<String, List<String>> parameters)
    {
        this.path = path;
        this.node = node;
        this.parameters = parameters;
    }

    public SitePath getPath()
    {
        return path;
    }

    public NavigationNodeResource getNode()
    {
        return node;
    }

    public Map<String, List<String>> getParameters()
    {
        return parameters;
    }
    
    public String getTargetUrl()
    {
        StringBuilder buff = new StringBuilder();
        buff.append("/ledge/x/").append(this.getNode().getIdString());
        if(this.getParameters().size() > 0)
        {
            buff.append('?');
            Iterator<Map.Entry<String, List<String>>> i = this.getParameters().entrySet()
                .iterator();
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