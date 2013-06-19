package net.cyklotron.cms.rewrite;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.cyklotron.cms.ProtectedResource;
import net.cyklotron.cms.structure.NavigationNodeResource;

public interface UrlRewriteParticipant
{
    String getName();

    boolean matches(String path);

    Set<String> getPaths();

    void drop(String path);

    RewriteTarget rewrite(String path);

    String path(Object object);

    ProtectedResource guard(String path);

    public class RewriteTarget
    {
        private final NavigationNodeResource node;

        private final Map<String, List<String>> parameters;

        public RewriteTarget(NavigationNodeResource node, Map<String, List<String>> parameters)
        {
            super();
            this.node = node;
            this.parameters = parameters;
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
}
