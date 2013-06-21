package net.cyklotron.cms.rewrite;

public class SitePath
{
    private final long siteId;

    private final String path;

    public SitePath(long siteId, String path)
    {
        this.siteId = siteId;
        this.path = path;
    }

    public long getSiteId()
    {
        return siteId;
    }

    public String getPath()
    {
        return path;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + (int)(siteId ^ (siteId >>> 32));
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
        SitePath other = (SitePath)obj;
        if(path == null)
        {
            if(other.path != null)
                return false;
        }
        else if(!path.equals(other.path))
            return false;
        if(siteId != other.siteId)
            return false;
        return true;
    }
}
