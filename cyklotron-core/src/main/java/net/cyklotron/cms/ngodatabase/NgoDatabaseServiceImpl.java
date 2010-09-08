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
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import java.util.Set;

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

import net.cyklotron.tools.Utils;

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
    
    private String dataEncoder;
    
    private String dataLocalDir;
    
    private String dataLocalName;
    
    private String dataLocalPath;
    
    private FileSystem fileSystem;
    
    private Organizations organizations;

    public NgoDatabaseServiceImpl(Configuration config, Logger logger, FileSystem fileSystem)
    {
        this.logger = logger;
        this.dataSourcePath = config.getChild("data_source_path").getValue("");
        this.dataEncoder = config.getChild("data_encoder").getValue("UTF-8");
        this.dataLocalDir = config.getChild("data_local_dir").getValue("/ngo/database");
        this.dataLocalName = config.getChild("data_local_name").getValue("organizations.xml");
        this.dataLocalPath = this.dataLocalDir + "/" + this.dataLocalName;     
        this.fileSystem = fileSystem;
        this.organizations = new Organizations();
        update();
    }
    
    @Override
    public void downloadDataSource()
    {
        try
        {
            String local_temp_path = this.dataLocalDir + "/tmp_" + this.dataLocalName;
            if(!fileSystem.isDirectory(this.dataLocalDir))
            {
                fileSystem.mkdirs(this.dataLocalDir);
            }
            if(!fileSystem.isFile(this.dataLocalPath))
            {
                fileSystem.createNewFile(this.dataLocalPath);
            }
            if(!fileSystem.isFile(local_temp_path))
            {
                fileSystem.createNewFile(local_temp_path);
            }
            fileSystem.write(local_temp_path, Utils.loadUrl(new URL(this.dataSourcePath)),
                this.dataEncoder);
            fileSystem.rename(local_temp_path, this.dataLocalPath);
            fileSystem.delete(local_temp_path);
        }
        catch(IOException e)
        {
            logger.info("Could not download ngo source data. " + e.getMessage());
        }
        catch(UnsupportedCharactersInFilePathException e)
        {
            logger.info(e.getMessage());
        }
    }

    @Override
    public void update()
    { 
        Document doc = new DOMDocument();
        if(!fileSystem.isFile(this.dataLocalPath))
        {
            downloadDataSource();
        }
        try
        {
            doc = readerToDom4j(fileSystem.getReader(this.dataLocalPath, this.dataEncoder));
            for(Element ogranization : (List<Element>)doc.selectNodes("/organizacje/organizacjaInfo"))
            {
               String name = ogranization.selectSingleNode("Nazwa_polska").getStringValue();
               Long id = Long.parseLong(ogranization.selectSingleNode("ID_Adresowego").getStringValue());
               String city = ogranization.selectSingleNode("Miasto").getStringValue();
               String aera = ogranization.selectSingleNode("Wojewodztwo").getStringValue();
               String street = ogranization.selectSingleNode("Ulica").getStringValue();
               String post_code = ogranization.selectSingleNode("Kod_pocztowy").getStringValue();
               this.organizations.addOrganization(new Organization(id,name,city,aera,street,post_code));
            }
        }
        catch(UnsupportedEncodingException e)
        {
            logger.info(e.getMessage());
        }
        catch(DocumentException e)
        {
            logger.info("Could not read ngo database source file " + this.dataLocalPath + " "
                + e.getMessage());
        }   
    }
    
    public Document readerToDom4j(Reader reader)
    throws DocumentException
    {
        SAXReader saxReader = new SAXReader();
        return saxReader.read(reader);
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
