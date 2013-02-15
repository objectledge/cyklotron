package net.cyklotron.cms.search.util;

import org.apache.lucene.document.Document;

public interface FromDocumentMapper<T>
{
    T fromDocument(Document document);
}
