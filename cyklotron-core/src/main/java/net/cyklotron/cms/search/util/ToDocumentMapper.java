package net.cyklotron.cms.search.util;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.objectledge.coral.store.Resource;

public interface ToDocumentMapper<T extends Resource>
{
    /**
     * Returns document mapped from resource
     * 
     * @param resource
     * @return document
     */
    Document toDocument(T resource);

    /**
     * Returns term which uniquely identifies this document
     * 
     * @param resource
     * @return Term which is the id of this document
     */
    Term getIdentifier(T resource);

    /**
     * Returns term which uniquely identifies a document
     * 
     * @param document identifier
     * @return Term which is the id of this document
     */
    Term getIdentifier(String identifier);
}
