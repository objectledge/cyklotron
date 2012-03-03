package net.cyklotron.cms.docimport;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

/**
 * A service for importing documents from remote sites.
 * 
 * @author rafal.krzewski@objectledge.org
 */
public interface DocumentImportService
{
    /**
     * Import a collection of documents from remote site.
     * 
     * @param configuration source configuration
     * @param start start date, may be {@code null}.
     * @param end end date, may be {@code null}.
     * @return a collection of documents.
     * @throws IOException
     */
    Collection<DocumentData> importDocuments(ImportSourceConfiguration configuration, Date start,
        Date end)
        throws IOException;
}
