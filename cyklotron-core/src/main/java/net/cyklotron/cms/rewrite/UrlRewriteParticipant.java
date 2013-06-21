package net.cyklotron.cms.rewrite;

import java.util.Set;

import net.cyklotron.cms.ProtectedResource;

public interface UrlRewriteParticipant
{
    String getName();

    boolean matches(SitePath path);

    Set<SitePath> getPaths();

    void drop(SitePath path);

    RewriteTarget rewrite(SitePath path);

    SitePath path(Object object);

    ProtectedResource guard(SitePath path);
}
