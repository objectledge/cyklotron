package net.cyklotron.cms.docimport;

import java.net.URI;

/**
 * A value object that contains the data of an attachment loaded from a remote source.
 * 
 * @author rafal.krzewski@objectledge.org
 */
public class AttachmentData
{
    private final URI originalURI;

    private final byte[] contents;

    public AttachmentData(URI originalURI, byte[] contents)
    {
        this.originalURI = originalURI;
        this.contents = contents;
    }

    public URI getOriginalURI()
    {
        return originalURI;
    }

    public byte[] getContents()
    {
        return contents;
    }
}
