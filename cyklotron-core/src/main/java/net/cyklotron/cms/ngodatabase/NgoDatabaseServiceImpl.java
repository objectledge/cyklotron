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

import java.io.IOException;
import java.io.InputStream;
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

/**
 * An implementation of <code>related.relationships</code> Coral resource class.
 * 
 * @author Coral Maven plugin
 */
public class NgoDatabaseServiceImpl
    implements NgoDatabaseService, Startable
{
    private Logger logger;

    private String dataSourcePath;

    private String dataLocalDir;

    private FileSystem fileSystem;

    private Organizations organizations;

    public NgoDatabaseServiceImpl(Configuration config, Logger logger, FileSystem fileSystem)
    {
        this.logger = logger;
        this.dataSourcePath = config.getChild("data_source_path").getValue("");
        this.dataLocalDir = config.getChild("data_local_dir").getValue("/ngo/database");
        this.fileSystem = fileSystem;
        this.organizations = new Organizations();
    }

    @Override
    public void downloadSource()
        throws IOException
    {
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(dataSourcePath);
        client.executeMethod(method);
        String sourceXmlPath = dataLocalDir + "/organizations.xml";
        String sourceTmpPath = sourceXmlPath + ".tmp";
        try
        {
            if(!fileSystem.isDirectory(dataLocalDir))
            {
                fileSystem.mkdirs(dataLocalDir);
            }
            fileSystem.write(sourceTmpPath, method.getResponseBodyAsStream());
            method.releaseConnection();

            fileSystem.rename(sourceTmpPath, sourceXmlPath);
        }
        catch(UnsupportedCharactersInFilePathException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update()
    {
        Document doc = new DOMDocument();
        String sourceXmlPath = dataLocalDir + "/organizations.xml";
        try
        {
            if(!fileSystem.isFile(sourceXmlPath))
            {
                downloadSource();
            }
            organizations.Clear();
            doc = streamToDom4j(fileSystem.getInputStream(sourceXmlPath));
            for(Element ogranization : (List<Element>)doc
                .selectNodes("/organizacje/organizacjaInfo"))
            {
                String name = ogranization.selectSingleNode("Nazwa_polska").getStringValue();
                Long id = Long.parseLong(ogranization.selectSingleNode("ID_Adresowego")
                    .getStringValue());
                String city = ogranization.selectSingleNode("Miasto").getStringValue();
                String aera = ogranization.selectSingleNode("Wojewodztwo").getStringValue();
                String street = ogranization.selectSingleNode("Ulica").getStringValue();
                String post_code = ogranization.selectSingleNode("Kod_pocztowy").getStringValue();
                this.organizations.addOrganization(new Organization(id, name, city, aera, street,
                    post_code));
            }
        }
        catch(DocumentException e)
        {
            logger.info("Could not read ngo data source file " + sourceXmlPath + " "
                + e.getMessage());
        }
        catch(IOException e)
        {
            logger.info("Could not read ngo data source file " + sourceXmlPath + " "
                + e.getMessage());
        }
    }

    public Document streamToDom4j(InputStream in)
        throws DocumentException
    {
        SAXReader saxReader = new SAXReader();
        return saxReader.read(in);
    }

    @Override
    public void start()
    {
        update();
    }

    @Override
    public void stop()
    {

    }

    @Override
    public Organization getOrganization(Long id)
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
