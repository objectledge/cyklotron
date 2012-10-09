package net.cyklotron.ngo.bazy.files;

import java.io.StringWriter;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.cyklotron.cms.files.FileResource;
import net.cyklotron.cms.files.FilesException;
import net.cyklotron.cms.files.FilesTool;

import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class CmsFile { //aka ImmutableSausage
    
    public FileResource getFileResource()
    {
        return fileResource;
    }

    public void setFileResource(FileResource fileResource)
    {
        this.fileResource = fileResource;
    }

    private FileResource fileResource;
    private FilesTool tool;
    private String name;
    private Date creationTime;
    private String description;
    private long id;
    private String mimeType;
    private long size;
    private String organizationFileType;
    
    public CmsFile() {

    }
     
    public CmsFile(FileResource fileResource) {
        this(fileResource, null);
    }
    
    public CmsFile(FileResource fileResource, FilesTool tool) {
        this.fileResource = fileResource;
        this.tool = tool;
        this.name = fileResource.getName();
        this.creationTime = fileResource.getCreationTime();
        this.description = fileResource.getDescription();
        this.id = fileResource.getId();
        this.mimeType = fileResource.getMimetype();
        this.size = fileResource.getSize();
    }
    
    @XmlElement
    public String getName() {        
        return name;
    }
    
    @XmlElement
    public Date getCreationTime() {        
        return creationTime;
    }
    
    @XmlElement
    public String getDescription() {        
        return description;
    }
    
    @XmlElement
    public long getId() {        
        return id;
    }
    
    @XmlElement
    public long getSize() {        
        return size;
    }    
       
    
    @XmlElement
    public String getLink() {    
        if(tool != null) {
            try
            {
                return tool.getLink(fileResource, true);
            }
            catch(FilesException e)
            {
                e.printStackTrace();
            }
        } 
        
        return null;        
    }

    @XmlElement
    public String getMimeType()
    {
        return mimeType;
    }
    @XmlElement
    public String getOrganizationFileType()
    {
        return organizationFileType;
    }

    //setters
    
    public void setOrganizationFileType(String organizationFileType)
    {
        this.organizationFileType = organizationFileType;
    }

    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setCreationTime(Date creationTime)
    {
        this.creationTime = creationTime;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public void setSize(long size)
    {
        this.size = size;
    }
    
    public String toXML() throws JAXBException {
        final StringWriter st = new StringWriter();
        JAXBContext jc = JAXBContext.newInstance(CmsFile.class);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(this, st);
        return st.toString();
    }
}
