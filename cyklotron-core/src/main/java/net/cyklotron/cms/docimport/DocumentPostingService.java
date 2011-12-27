package net.cyklotron.cms.docimport;

import java.util.Collection;

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
    void postDocuments(ImportTargetConfiguration config, Collection<DocumentData> documents)
        throws StructureException;
}
