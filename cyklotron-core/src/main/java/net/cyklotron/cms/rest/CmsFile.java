package net.cyklotron.cms.rest;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.cyklotron.cms.files.FileResource;

@XmlRootElement
public class CmsFile { //aka ImmutableSausage
    
    protected FileResource fileResource;
    
    public CmsFile() {
        
    }
     
    public CmsFile(FileResource fileResource) {
        this.fileResource = fileResource;
    }
    
    @XmlElement
    public String getName() {        
        return "File NAAAAMEEEE!";
    }
    
    

}
