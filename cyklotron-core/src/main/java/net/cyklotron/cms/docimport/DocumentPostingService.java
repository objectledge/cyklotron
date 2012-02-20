package net.cyklotron.cms.docimport;

import java.util.Collection;
import java.util.Map;

import net.cyklotron.cms.documents.DocumentNodeResource;
import net.cyklotron.cms.structure.StructureException;

/**
 * A service for posting documents with attachments to Cyklotron.
 * 
 * @author rafal.krzewski@objectledge.org
 */
public interface DocumentPostingService
{
    /**
     * Posts a collection of documents, assigning categories and storing attachments.
     * 
     * @param config posting configuration.
     * @param documents the documents to be posted.
     * @throws StructureException when posting fails
     */
    Map<DocumentData, DocumentNodeResource> postDocuments(ImportTargetConfiguration config,
        Collection<DocumentData> documents)
        throws StructureException;
}
