package net.cyklotron.cms.accesslimits;

import org.objectledge.web.ratelimit.impl.RequestInfo;

import com.google.common.base.Optional;

public interface ProtectedItemRegistry
{
    Optional<ProtectedItem> getProtectedItem(RequestInfo request);
}
