package net.cyklotron.cms.search.util;

import org.apache.lucene.document.Document;
import org.objectledge.coral.store.Resource;

public interface ToDocumentMapper<T extends Resource>
{
    Document toDocument(T resource);
}
