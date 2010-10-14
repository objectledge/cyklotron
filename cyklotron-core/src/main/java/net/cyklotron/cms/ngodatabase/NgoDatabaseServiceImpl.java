// 
// Copyright (c) 2004, Caltha - Gajda, Krzewski, Mach, Potempski Sp.J. 
// All rights reserved. 
// 
// Redistribution and use in source and binary forms, with or without modification,  
// are permitted provided that the following conditions are met: 
// 
// * Redistributions of source code must retain the above copyright notice,  
//       this list of conditions and the following disclaimer. 
// * Redistributions in binary form must reproduce the above copyright notice,  
//       this list of conditions and the following disclaimer in the documentation  
//       and/or other materials provided with the distribution. 
// * Neither the name of the Caltha - Gajda, Krzewski, Mach, Potempski Sp.J.  
//       nor the names of its contributors may be used to endorse or promote products  
//       derived from this software without specific prior written permission. 
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
// IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,  
// INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,  
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
// OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,  
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)  
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  
// POSSIBILITY OF SUCH DAMAGE. 
// 

package net.cyklotron.cms.ngodatabase;

import static org.objectledge.filesystem.FileSystem.directoryPath;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.dom.DOMDocument;
import org.dom4j.io.SAXReader;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.Logger;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.UnsupportedCharactersInFilePathException;
import org.picocontainer.Startable;

public class NgoDatabaseServiceImpl
    implements NgoDatabaseService, Startable
{
    private static final String INCOMING_FILE = "ngo/database/incoming/organizations.xml";
    
    private static final String OUTGOING_DIR = "ngo/database/outgoing";
    
    private static final String FEEDS_DIR = "ngo/database/feeds";
    
    private Logger logger;

    private String sourceURL;

    private FileSystem fileSystem;

    private Organizations organizations = new Organizations();

    public NgoDatabaseServiceImpl(Configuration config, Logger logger, FileSystem fileSystem)
    {
        this.logger = logger;
        this.sourceURL = config.getChild("incoming").getChild("sourceURL").getValue("");
        this.fileSystem = fileSystem;
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

    @Override
    public void updateIncoming()
    {        
        readIncoming(true);
    }

    private void readIncoming(boolean updateFromSource)
    {
        Document doc = new DOMDocument();        
        try
        {
            if(updateFromSource || !fileSystem.isFile(INCOMING_FILE))
            {
                downloadIncoming();
            }
            organizations.Clear();
            SAXReader saxReader = new SAXReader();
            doc = saxReader.read(fileSystem.getInputStream(INCOMING_FILE));
            for(Element ogranization : (List<Element>)doc
                .selectNodes("/organizacje/organizacjaInfo"))
            {
                String name = ogranization.selectSingleNode("Nazwa_polska").getStringValue();
                Long id = Long.parseLong(ogranization.selectSingleNode("ID_Adresowego")
                    .getStringValue());
                String city = ogranization.selectSingleNode("Miasto").getStringValue();
                String province = ogranization.selectSingleNode("Wojewodztwo").getStringValue();
                String street = ogranization.selectSingleNode("Ulica").getStringValue();
                String post_code = ogranization.selectSingleNode("Kod_pocztowy").getStringValue();
                this.organizations.addOrganization(new Organization(id, name, province, city, street,
                    post_code));
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

    @Override
    public void start()
    {
        readIncoming(false);
    }

    @Override
    public void stop()
    {

    }

    @Override
    public Organization getOrganization(long id)
    {
        return organizations.getOrganization(id);
    }

    @Override
    public Organizations getOrganizations()
    {
        return organizations;
    }

    @Override
    public Set<Organization> getOrganizations(String substring)
    {
        return organizations.getOrganizations(substring);
    }
}
