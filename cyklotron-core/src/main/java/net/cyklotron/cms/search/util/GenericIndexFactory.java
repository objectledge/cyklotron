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
     * @param resourceProvider
     * @return GenericIndex
     * @throws IOException
     */
    <T extends Resource, U> GenericIndex<T, U> createOrOpenIndex(String pathToDirectory,
        FromDocumentMapper<U> fromDocumentMapper, ToDocumentMapper<T> toDocumentMapper,
        ResourceProvider<T> resourceProvider)
        throws IOException;

    /**
     * Creates GenericIndex with provided analyzer
     * 
     * @param pathToDirectory
     * @param fromDocumentMapper
     * @param toDocumentMapper
     * @param resourceProvider
     * @param analyzerProvider
     * @return GenericIndex
     * @throws IOException
     */
    <T extends Resource, U> GenericIndex<T, U> createOrOpenIndex(String pathToDirectory,
        FromDocumentMapper<U> fromDocumentMapper, ToDocumentMapper<T> toDocumentMapper,
        ResourceProvider<T> resourceProvider, AnalyzerProvider analyzerProvider)
        throws IOException;

}
