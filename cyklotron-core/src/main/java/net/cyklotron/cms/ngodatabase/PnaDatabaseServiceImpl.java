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
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.dom.DOMDocument;
import org.dom4j.io.SAXReader;
import org.jcontainer.dna.Configuration;
import org.jcontainer.dna.ConfigurationException;
import org.jcontainer.dna.Logger;
import org.objectledge.coral.BackendException;
import org.objectledge.coral.entity.EntityDoesNotExistException;
import org.objectledge.coral.schema.ResourceClass;
import org.objectledge.coral.session.CoralSession;
import org.objectledge.coral.store.InvalidResourceNameException;
import org.objectledge.coral.store.Resource;
import org.objectledge.coral.store.ValueRequiredException;
import org.objectledge.filesystem.FileSystem;
import org.objectledge.filesystem.UnsupportedCharactersInFilePathException;
import org.picocontainer.Startable;

import net.cyklotron.cms.CmsNodeResourceImpl;
import net.cyklotron.tools.Utils;

/**
 * An implementation of <code>related.relationships</code> Coral resource class.
 *
 * @author Coral Maven plugin
 */
public class PnaDatabaseServiceImpl
    implements PnaDatabaseService, Startable
{
    private Logger logger;
    
    private String dataSourcePath;
    
    private Integer parseStartPage;
    
    private Integer parseEndPage;
    
    private String dataLocalDir;
    
    private String dataLocalName;
    
    private String dataLocalPath;
    
    private FileSystem fileSystem;


    public PnaDatabaseServiceImpl(Configuration config, Logger logger, FileSystem fileSystem)
    {
        this.logger = logger;
        this.dataSourcePath = config.getChild("data_source_path").getValue("");
        this.parseStartPage = config.getChild("parse_start_page").getValueAsInteger(1);
        this.parseEndPage = config.getChild("parse_end_page").getValueAsInteger(1);
        this.dataLocalDir = config.getChild("data_local_dir").getValue("/ngo/database");
        this.dataLocalName = config.getChild("data_local_name").getValue("spispna.xml");
        this.dataLocalPath = this.dataLocalDir + "/" + this.dataLocalName;     
        this.fileSystem = fileSystem;
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
            String parsedString = pdfToText(new URL(this.dataSourcePath), parseStartPage , parseEndPage);
            fileSystem.write(local_temp_path, parsedString, "UTF-8");
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
    }
    
    
    public String pdfToText(URL url, Integer pageStart, Integer pageEnd)
        throws IOException
    {
        PDDocument doc = null;
        StringWriter sw = new StringWriter();
        try
        {
            doc = PDDocument.load(url);
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(pageStart);
            stripper.setEndPage(pageEnd);
            stripper.writeText(doc, sw);
        }
        finally
        {
            if(doc != null)
            {
                doc.close();
            }
        }
        return sw.toString();
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
}
