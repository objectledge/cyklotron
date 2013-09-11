package net.cyklotron.cms.rewrite;

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
}