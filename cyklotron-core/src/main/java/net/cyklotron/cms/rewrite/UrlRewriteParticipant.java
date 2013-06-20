package net.cyklotron.cms.rewrite;

import java.util.Set;

import net.cyklotron.cms.ProtectedResource;

public interface UrlRewriteParticipant
{
    String getName();

    boolean matches(String path);

    Set<String> getPaths();

    void drop(String path);

    RewriteTarget rewrite(String path);

    String path(Object object);

    ProtectedResource guard(String path);
}
