package net.cyklotron.cms.rest;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesTool;

@XmlRootElement
public class CmsFile { //aka ImmutableSausage
    
    private FileResource fileResource;
    private FilesTool tool;
    
    public CmsFile() {
        
    }
     
    public CmsFile(FileResource fileResource) {
        this(fileResource, null);
    }
    
    public CmsFile(FileResource fileResource, FilesTool tool) {
        this.fileResource = fileResource;
        this.tool = tool;
    }
    
    @XmlElement
    public String getName() {        
        return fileResource.getName();
    }
    
    @XmlElement
    public Date getCreationTime() {        
        return fileResource.getCreationTime();
    }
    
    @XmlElement
    public String getDescription() {        
        return fileResource.getDescription();
    }
    
    @XmlElement
    public long getId() {        
        return fileResource.getId();
    }
    
    @XmlElement
    public String getMimetype() {        
        return fileResource.getMimetype();
    }
    
    @XmlElement
    public long getSize() {        
        return fileResource.getSize();
    }    
    
    @XmlElement
    public String getLink() {    
        if(tool != null) {
            try
            {
                return tool.getLink(fileResource);
            }
            catch(FilesException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } 
        
        return null;        
    }
    

}
