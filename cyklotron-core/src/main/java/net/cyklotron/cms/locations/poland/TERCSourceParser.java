/**
 * 
 */
package net.cyklotron.cms.locations.poland;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;

public class TERCSourceParser
{
    private final Logger logger;

    private final FileSystem fileSystem;

    private String[] headings;

    private List<String[]> content = new ArrayList<String[]>();

    public TERCSourceParser(FileSystem fileSystem, Logger logger)
    {
        this.logger = logger;
        this.fileSystem = fileSystem;
    }

    public List<String[]> parse(String sourceLocation)
    {
        List<String[]> content = new ArrayList<String[]>();
        try
        {
            SAXReader saxReader = new SAXReader();
            Document doc = saxReader.read(fileSystem.getInputStream(sourceLocation));

            List<Element> tercHeadings = (List<Element>)doc.selectSingleNode("/teryt/catalog/row")
                .selectNodes("col");
            headings = new String[tercHeadings.size()];
            for(int i = 0; i < tercHeadings.size(); i++)
            {
                headings[i] = tercHeadings.get(i).attributeValue("name");
            }

            List<Element> tercContents = (List<Element>)doc.selectNodes("/teryt/catalog/row");
            for(Element row : tercContents)
            {
                List<Element> entities = (List<Element>)row.selectNodes("col");
                String[] line = new String[headings.length];
                for(int i = 0; i < entities.size(); i++)
                {
                    line[i] = entities.get(i).getText().isEmpty() ? null : entities.get(i).getText();
                }
                content.add(line);
            }
            this.content = content;
        }
        catch(DocumentException e)
        {
            logger.error("failed to parse source file " + sourceLocation, e);
        }
        return content;
    }

    public String[] getHeadings()
    {
        return headings;
    }

    public List<String[]> getContent()
    {
        return content;
    }
}
