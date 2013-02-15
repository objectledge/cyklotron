package net.cyklotron.cms.search.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.Validate;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.session.CoralSessionFactory;
import org.objectledge.coral.store.Resource;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.LocalFileSystemProvider;

import net.cyklotron.cms.search.analysis.AnalyzerProvider;

public class GenericIndexFactoryImpl
    implements GenericIndexFactory
{

    private FileSystem fileSystem;

    private Logger logger;

    private IndexInitializer indexInitializator;

    public GenericIndexFactoryImpl(FileSystem fileSystem, Logger logger,
        CoralSessionFactory coralSessionFactory,
        IndexInitializer indexInitializator)
    {
        this.fileSystem = fileSystem;
        this.logger = logger;
        this.indexInitializator = indexInitializator;
    }

    private void validateNotNull(String pathToDirectory,
 FromDocumentMapper<?> fromDocumentMapper,
        ToDocumentMapper<? extends Resource> toDocumentMapper)
    {
        Validate.notBlank(pathToDirectory);
        Validate.notNull(fromDocumentMapper);
        Validate.notNull(toDocumentMapper);
    }

    @Override
    public <T extends Resource, U> GenericIndex<T, U> createOrOpenIndex(String pathToDirectory,
        FromDocumentMapper<U> fromDocumentMapper, ToDocumentMapper<T> toDocumentMapper,
        ResourceProvider<T> resourceProvider)
        throws IOException
    {
        validateNotNull(pathToDirectory, fromDocumentMapper, toDocumentMapper);

        return createOrOpenIndex(pathToDirectory, fromDocumentMapper, toDocumentMapper,
            resourceProvider, AnalyzerProvider.DEFAULT_PROVIDER);
    }

    @Override
    public <T extends Resource, U> GenericIndex<T, U> createOrOpenIndex(String pathToDirectory,
        FromDocumentMapper<U> fromDocumentMapper, ToDocumentMapper<T> toDocumentMapper,
        ResourceProvider<T> resourceProvider, AnalyzerProvider analyzerProvider)
        throws IOException
    {
        validateNotNull(pathToDirectory, fromDocumentMapper, toDocumentMapper);
        Validate.notNull(analyzerProvider);

        File indexLocation = ((LocalFileSystemProvider)fileSystem.getProvider("local"))
            .getFile(pathToDirectory);

        Directory directory = new NIOFSDirectory(indexLocation);
        indexInitializator.openIndex(directory);

        return new GenericIndex<T, U>(fileSystem, logger, pathToDirectory, analyzerProvider,
            fromDocumentMapper, toDocumentMapper, resourceProvider, directory);

    }

}
