package net.cyklotron.cms.locations.poland;

import java.io.IOException;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;

public class PNAProvider
{
    public static final String SOURCE_DIRECTORY = "/ngo/locations/";

    public static final String PART_1 = "spispna-cz1.txt";

    private final FileSystem fileSystem;

    private final Logger logger;

    public PNAProvider(FileSystem fileSystem, Logger logger)
    {
        this.fileSystem = fileSystem;
        this.logger = logger;
    }

    /**
     * Does nothing, source files must be manually copied into {@link #SOURCE_DIRECTORY}.
     * 
     * @return true if download was successful.
     */
    public boolean downloadSource()
    {
        return true;
    }

    /**
     * Parse source file. Source file is parsed and on success {@link #cachedContent} variable is
     * updated.
     * 
     * @return true if parsing was successful.
     */
    public List<String[]> parseSource()
        throws IOException
    {
        PNASourceParser parser = new PNASourceParser(fileSystem, logger);

        parser.parse(SOURCE_DIRECTORY + PART_1);

        return parser.getContent();
    }

    public List<String[]> parseCache()
        throws IOException
    {
        return parseCache();
    }
}
