package net.cyklotron.cms.sitemap;

import java.net.URI;
import java.util.Collection;
import java.util.Date;

/**
 * A resource referenced in a site map.
 * <p>
 * Resources are considered equal in the sense of {@link Object#equals(Object)} and
 * {@link Object#hashCode()} when they share resourceId.
 * </p>
 * 
 * @author rafal.krzewski@caltha.pl
 */
public class SitemapItem
{
    private final long resourceId;

    private final URI uri;

    private final Date lastModified;

    private final ChangeFrequency changeFrequency;

    private final Collection<SitemapImage> images;

    public SitemapItem(long resourceId, URI uri, Date lastModified,
        ChangeFrequency changeFrequency, Collection<SitemapImage> images)
    {
        this.resourceId = resourceId;
        this.uri = uri;
        this.lastModified = lastModified;
        this.changeFrequency = changeFrequency;
        this.images = images;
    }

    public long getResourceId()
    {
        return resourceId;
    }

    public URI getUri()
    {
        return uri;
    }

    public Date getLastModified()
    {
        return lastModified;
    }

    public ChangeFrequency getChangeFrequency()
    {
        return changeFrequency;
    }

    public Collection<SitemapImage> getImages()
    {
        return images;
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
        SitemapItem other = (SitemapItem)obj;
        if(resourceId != other.resourceId)
            return false;
        return true;
    }
}
