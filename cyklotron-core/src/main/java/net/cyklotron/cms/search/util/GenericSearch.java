package net.cyklotron.cms.search.util;

import java.util.Collection;

public interface GenericSearch<T>
{
    Collection<T> search(PerformSearch performSearch);

    Collection<String> getAllFieldNames();

    <R> R useSearcher(SearcherUser<R> searcherUser);
}
