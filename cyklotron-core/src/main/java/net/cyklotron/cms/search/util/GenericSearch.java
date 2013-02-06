package net.cyklotron.cms.search.util;

import java.util.Collection;

import org.objectledge.coral.store.Resource;

public interface GenericSearch<T extends Resource>
{
    Collection<T> search(PerformSearch performSearch);
}
