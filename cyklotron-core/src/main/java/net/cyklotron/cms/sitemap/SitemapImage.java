package net.cyklotron.cms.sitemap;

import java.net.URI;

public class SitemapImage
{
    private long resourceId;

    private URI uri;

    private String caption;

    public SitemapImage(long resourceId, URI uri, String caption)
    {
        this.resourceId = resourceId;
        this.uri = uri;
        this.caption = caption;
    }

    public long getResourceId()
    {
        return resourceId;
    }

    public URI getUri()
    {
        return uri;
    }

    public String getCaption()
    {
        return caption;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int)(resourceId ^ (resourceId >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        SitemapImage other = (SitemapImage)obj;
        if(resourceId != other.resourceId)
            return false;
        return true;
    }
}
