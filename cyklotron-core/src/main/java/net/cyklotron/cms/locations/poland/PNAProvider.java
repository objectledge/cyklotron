package net.cyklotron.cms.locations.poland;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class PNAProvider
{
    public static final String SOURCE_DIRECTORY = "/ngo/locations/";

    public static final String PART_1 = "spispna-cz1.txt";

    public static final String PART_3 = "spispna-cz3.txt";

    private static final Pattern MAIN_OFFICE = Pattern.compile("UP .* 1");

    private final FileSystem fileSystem;

    private final Logger logger;

    private static final Predicate<String[]> IS_MAIN_OFFICE = new Predicate<String[]>()
        {
            @Override
            public boolean apply(String[] input)
            {
                return MAIN_OFFICE.matcher(input[1]).matches();
            }
        };

    private static final Function<String[], String[]> MAIN_OFFICE_TO_CITY = new Function<String[], String[]>()
        {
            @Override
            public String[] apply(String[] input)
            {
                input[1] = input[2];
                input[2] = "";
                input[3] = "";
                input[4] = input[5];
                input[5] = input[6];
                input[6] = input[7];
                return input;
            }
        };

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
        final List<String[]> part1 = parser.getContents();

        parser.parse(SOURCE_DIRECTORY + PART_3);
        final ArrayList<String[]> part3 = newArrayList(transform(
            filter(parser.getContents(), IS_MAIN_OFFICE), MAIN_OFFICE_TO_CITY));

        List<String[]> result = new ArrayList<>();
        result.addAll(part1);
        result.addAll(part3);
        return result;
    }

    public List<String[]> parseCache()
        throws IOException
    {
        return parseCache();
    }
}
