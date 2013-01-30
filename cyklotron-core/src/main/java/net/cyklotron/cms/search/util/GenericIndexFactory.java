package net.cyklotron.cms.search.util;

import java.io.IOException;

import org.objectledge.coral.store.Resource;

import net.cyklotron.cms.search.analysis.AnalyzerProvider;

/**
 * GenericIndexFactory, creates or opens GenericIndexes. If index already exist then it is reopened.
 * If not it is created.
 * 
 * @author Marek Lewandowski
 */
public interface GenericIndexFactory
{
    /**
     * Creates GenericIndex with default analyzer
     * 
     * @param pathToDirectory
     * @param fromDocumentMapper
     * @param toDocumentMapper
     * @return GenericIndex
     * @throws IOException
     */
    GenericIndex<? extends Resource> createOrOpenIndex(String pathToDirectory,
        FromDocumentMapper<? extends Resource> fromDocumentMapper,
        ToDocumentMapper<? extends Resource> toDocumentMapper)
        throws IOException;

    /**
     * Creates GenericIndex with provided analyzer
     * 
     * @param pathToDirectory
     * @param fromDocumentMapper
     * @param toDocumentMapper
     * @param analyzerProvider
     * @return GenericIndex
     * @throws IOException
     */
    GenericIndex<? extends Resource> createOrOpenIndex(String pathToDirectory,
        FromDocumentMapper<? extends Resource> fromDocumentMapper,
        ToDocumentMapper<? extends Resource> toDocumentMapper, AnalyzerProvider analyzerProvider)
        throws IOException;

}
