package net.cyklotron.cms.ngodatabase.organizations;

import static org.objectledge.filesystem.FileSystem.directoryPath;

import java.io.IOException;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.UnsupportedCharactersInFilePathException;

import net.cyklotron.cms.ngodatabase.Organization;

public class IncomingOrganizationsService
{
    private static final String INCOMING_FILE = "ngo/database/incoming/update.xml";

    private final String sourceURL;
    
    private final OrganizationsIndex organizationsIndex;

    private final FileSystem fileSystem;

    private final Logger logger;

    public IncomingOrganizationsService(Configuration incomingConfig, FileSystem fileSystem,
        OrganizationsIndex organizationsIndex, Logger logger)
        throws ConfigurationException
    {
        this.sourceURL = incomingConfig.getChild("sourceURL").getValue();
        this.fileSystem = fileSystem;
        this.organizationsIndex = organizationsIndex;
        this.logger = logger;
    }

    private void downloadIncoming()
        throws IOException
    {
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(sourceURL);
        client.executeMethod(method);
        String incomingTempFile = INCOMING_FILE + ".tmp";
        try
        {
            if(!fileSystem.isDirectory(directoryPath(INCOMING_FILE)))
            {
                fileSystem.mkdirs(directoryPath(INCOMING_FILE));
            }
            fileSystem.write(incomingTempFile, method.getResponseBodyAsStream());
            method.releaseConnection();

            fileSystem.rename(incomingTempFile, INCOMING_FILE);
        }
        catch(UnsupportedCharactersInFilePathException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public void readIncoming(boolean updateFromSource)
    {
        try
        {
            boolean sourceUpdated = false;
            if(updateFromSource || !fileSystem.isFile(INCOMING_FILE))
            {
                downloadIncoming();
                sourceUpdated = true;
            }
            if(sourceUpdated || organizationsIndex.isEmpty())
            {
                organizationsIndex.startUpdate();
                SAXReader saxReader = new SAXReader();
                Document doc = saxReader.read(fileSystem.getInputStream(INCOMING_FILE));
                @SuppressWarnings("unchecked")
                List<Element> organizationElements = (List<Element>)doc
                    .selectNodes("/organizacje/organizacjaInfo");
                for(Element ogranization : organizationElements)
                {
                    String name = ogranization.selectSingleNode("Nazwa_polska").getStringValue();
                    Long id = Long.parseLong(ogranization.selectSingleNode("ID_Adresowego")
                        .getStringValue());
                    String city = ogranization.selectSingleNode("Miasto").getStringValue();
                    String province = ogranization.selectSingleNode("Wojewodztwo").getStringValue();
                    String street = ogranization.selectSingleNode("Ulica").getStringValue();
                    String post_code = ogranization.selectSingleNode("Kod_pocztowy")
                        .getStringValue();
                    this.organizationsIndex.addItem(new Organization(id, name, province, city, street,
                        post_code));
                }
                organizationsIndex.endUpdate();
            }
        }
        catch(DocumentException e)
        {
            logger.info("Could not read source file ", e);
        }
        catch(IOException e)
        {
            logger.info("Could not read source file ", e);
        }
    }    
}
