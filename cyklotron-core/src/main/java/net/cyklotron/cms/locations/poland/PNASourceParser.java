package net.cyklotron.cms.locations.poland;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;

import net.cyklotron.files.util.CSVReader;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class PNASourceParser
{
    private final FileSystem fileSystem;

    private List<String[]> contents;

    private String[] headings;

    public PNASourceParser(FileSystem fileSystem, Logger logger)
    {
        this.fileSystem = fileSystem;
    }

    public void parse(String sourceLocation)
        throws IOException
    {
        Reader r = fileSystem.getReader(sourceLocation, "CP1250");
        CSVReader cr = new CSVReader(r, ';');
        headings = cr.readHeaders().toArray(new String[0]);
        contents = Lists.transform(cr.readData(), new Function<List<String>, String[]>()
            {
                @Override
                public String[] apply(List<String> input)
                {
                    return input.toArray(new String[input.size()]);
                }
            });
    }

    public String[] getHeadings()
    {
        return headings;
    }

    public List<String[]> getContents()
    {
        return contents;
    }
}
