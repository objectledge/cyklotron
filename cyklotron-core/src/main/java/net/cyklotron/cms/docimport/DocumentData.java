package net.cyklotron.cms.docimport;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/**
 * A value object that contains data of the document loaded from remote source.
 * 
 * @author rafal.krzewski@objectledge.org
 */
public class DocumentData
{
    private final String title;

    private final String _abstract;

    private final String content;

    private final String sourceName;

    private final URI originalURI;

    private final Date creationDate;

    private final Date modificationDate;

    private final Collection<AttachmentData> attachments;

    public DocumentData(String title, String _abstract, String content, String sourceName,
        URI originalURI, Date creationDate, Date modficicationDate,
        Collection<AttachmentData> attachments)
    {
        this.title = title;
        this._abstract = _abstract;
        this.content = content;
        this.sourceName = sourceName;
        this.originalURI = originalURI;
        this.creationDate = creationDate;
        modificationDate = modficicationDate;
        this.attachments = Collections.unmodifiableCollection(new ArrayList<AttachmentData>(
            attachments));
    }

    public String getTitle()
    {
        return title;
    }

    public String getAbstract()
    {
        return _abstract;
    }

    public String getContent()
    {
        return content;
    }

    public String getSourceName()
    {
        return sourceName;
    }

    public URI getOriginalURI()
    {
        return originalURI;
    }

    public String getOriginalName()
    {
        String[] pathSeg = originalURI.getPath().split("/");
        return pathSeg[pathSeg.length - 1];
    }

    public Date getCreationDate()
    {
        return creationDate;
    }

    public Date getModificationDate()
    {
        return modificationDate;
    }

    public Collection<AttachmentData> getAttachments()
    {
        return attachments;
    }
}
