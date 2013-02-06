package net.cyklotron.cms.search.util;

import org.apache.lucene.document.Document;
import org.objectledge.coral.store.Resource;

public interface FromDocumentMapper<T extends Resource>
{
    T fromDocument(Document document);
}
