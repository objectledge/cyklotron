package net.cyklotron.cms.accesslimits;

import com.google.common.base.Optional;

public interface ActionRegistry
{
    Optional<Action> getAction(String name);
}
